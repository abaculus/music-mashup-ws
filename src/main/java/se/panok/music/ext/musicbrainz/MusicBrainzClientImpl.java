package se.panok.music.ext.musicbrainz;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import se.panok.music.model.Artist;
import se.panok.music.utils.LogMessage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Component
public class MusicBrainzClientImpl implements MusicBrainzClient {

	private final static Artist EMPTY_ARTIST = new Artist();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${musicBrainz.client.connectTimeout:1000}")
	private int connectTimeout;

	@Value("${musicBrainz.client.readTimeout:3000}")
	private int readTimeout;

	@Value("${musicBrainz.client.url:http://musicbrainz.org/ws/2/artist/%s?&fmt=json&inc=url-rels+release-groups}")
	private String url;

	private ObjectReader musicBrainzArtistReader;

	@Override
	public Artist getArtist(final String mbid) {
		logger.debug(LogMessage.createForAction("getArtist")
				.addPart("mbid", mbid).toString());

		if (StringUtils.isBlank(mbid)) {
			throw new IllegalArgumentException("Blank 'mbid' is no allowed!");
		}

		final String json = fetchJson(mbid);
		logger.debug(LogMessage.createForAction("getArtist")
				.addPart("json", json).toString());

		Artist artist = EMPTY_ARTIST;
		if (!StringUtils.isEmpty(json)) {
			try {
				artist = musicBrainzArtistReader.readValue(json);
				logger.debug(LogMessage.createForAction("getArtist")
						.addPart("artist", artist).toString());
			} catch (final IOException e) {
				logger.error(
						LogMessage.createForAction("getArtist")
								.addPart("mbid", mbid).addPart("json", json)
								.addPart("connectTimeout", connectTimeout)
								.addPart("readTimeout", readTimeout)
								.addPart("url", url).toString(), e);
			}
		}
		return artist;
	}

	@PostConstruct
	public void init() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		musicBrainzArtistReader = objectMapper.reader(Artist.class);
	}

	protected String fetchJson(final String mbid) {
		String json = null;
		String formattedUrl = null;
		try {
			logger.debug(LogMessage.createForAction("fetchJson")
					.addPart("url", url).addPart("mbid", mbid)
					.addPart("connectTimeout", connectTimeout)
					.addPart("readTimeout", readTimeout).toString());

			formattedUrl = String.format(url, mbid);
			logger.debug(LogMessage.createForAction("fetchJson")
					.addPart("formattedUrl", formattedUrl).toString());

			json = Request
					.Get(formattedUrl)
					.connectTimeout(connectTimeout)
					.socketTimeout(readTimeout)
					.addHeader(
							new BasicHeader(HttpHeaders.ACCEPT,
									ContentType.APPLICATION_JSON.getMimeType()))
					.execute().returnContent().asString();
			logger.debug(LogMessage.createForAction("fetchJson")
					.addPart("json", json).toString());
		} catch (final IOException e) {
			logger.error(
					LogMessage.createForAction("fetchJson").addPart("url", url)
							.addPart("formattedUrl", formattedUrl)
							.addPart("mbid", mbid)
							.addPart("connectTimeout", connectTimeout)
							.addPart("readTimeout", readTimeout).toString(), e);
		}
		return json;
	}
}
