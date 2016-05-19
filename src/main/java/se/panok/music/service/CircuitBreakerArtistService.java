package se.panok.music.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import se.panok.music.Application;
import se.panok.music.ext.coverart.CoverArtClient;
import se.panok.music.ext.coverart.GetCoverArtUrlCommand;
import se.panok.music.ext.musicbrainz.GetArtistCommand;
import se.panok.music.ext.musicbrainz.MusicBrainzClient;
import se.panok.music.ext.wikipedia.GetArtistExtractCommand;
import se.panok.music.ext.wikipedia.WikipediaClient;
import se.panok.music.model.Album;
import se.panok.music.model.Artist;
import se.panok.music.utils.LogMessage;

import com.google.common.collect.Lists;

@Service
public class CircuitBreakerArtistService implements ArtistService {

	private static final String RELATION_TYPE_WIKIPEDIA_FILTER = "wikipedia";

	private static final String RELEASE_GROUP_TYPE_ALBUM_FILTER = "Album";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MusicBrainzClient musicBrainzClient;

	@Autowired
	private WikipediaClient wikipediaClient;

	@Autowired
	private CoverArtClient coverArtClient;

	@Override
	@Cacheable(Application.CACHE_ARTISTS)
	public Artist getArtistInfo(final String mbid) {

		logger.info(LogMessage.createForAction("getArtistInfo")
				.addPart("mbid", mbid).toString());

		// blocking call, every other call depends on info from this
		final Artist artist = getArtist(mbid);

		// filter relations and albums
		artist.applyFilter(Artist.FilterType.RELATION_TYPE,
				RELATION_TYPE_WIKIPEDIA_FILTER);
		artist.applyFilter(Artist.FilterType.RELEASE_GROUP_TYPE,
				RELEASE_GROUP_TYPE_ALBUM_FILTER);

		// async fetch of artist description and album cover art images
		final List<Callable<Void>> callables = Lists.newArrayList();
		callables.addAll(createAssignAlbumCoverArtImageUrls(artist));
		callables.addAll(createAssignArtistDescriptionCallable(artist));

		spawnThreads(callables);

		logger.info(LogMessage.createForAction("getArtistInfo")
				.addPart("artist", artist).addPart("mbid", mbid).toString());

		return artist;
	}

	private Artist getArtist(final String mbid) {
		return new GetArtistCommand(musicBrainzClient, mbid).execute();
	}

	private List<Callable<Void>> createAssignArtistDescriptionCallable(
			final Artist artist) {
		final List<Callable<Void>> callables = Lists.newArrayList();
		callables.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				final String description = new GetArtistExtractCommand(
						wikipediaClient, artist.getWikipediaArtistId())
						.execute();
				artist.setDescription(description);
				return null;
			}
		});
		return callables;
	}

	private List<Callable<Void>> createAssignAlbumCoverArtImageUrls(
			final Artist artist) {
		List<Callable<Void>> callables = Lists.newArrayList();
		for (final Album album : artist.getAlbums()) {
			callables.add(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					final String coverArtImageUrl = new GetCoverArtUrlCommand(
							coverArtClient, album.getId()).execute();
					album.setImage(coverArtImageUrl);
					return null;
				}
			});
		}
		return callables;
	}

	private void spawnThreads(final List<Callable<Void>> callables) {
		ExecutorService executor = null;
		if (!CollectionUtils.isEmpty(callables)) {
			final int threadCount = callables.size();
			logger.info(LogMessage.createForAction("spawnThreads")
					.addPart("threadCount", threadCount).toString());
			try {
				executor = Executors.newFixedThreadPool(callables.size());
				executor.invokeAll(callables);
				logger.info(LogMessage.createForAction("spawnThreads")
						.addPart("message", "Done!").toString());
			} catch (final InterruptedException e) {
				logger.error(
						LogMessage.createForAction("callAsync").toString(), e);
			} finally {
				if (null != executor) {
					executor.shutdown();
				}
			}
		}
	}
}
