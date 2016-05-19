package se.panok.music.integrationtests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import se.panok.music.BeanMock;
import se.panok.music.ext.coverart.CoverArtClient;
import se.panok.music.ext.coverart.CoverArtClientImpl;
import se.panok.music.ext.musicbrainz.MusicBrainzClient;
import se.panok.music.ext.musicbrainz.MusicBrainzClientImpl;
import se.panok.music.ext.wikipedia.WikipediaClient;
import se.panok.music.ext.wikipedia.WikipediaClientImpl;
import se.panok.music.util.FileUtil;
import se.panok.music.utils.LogMessage;

@Configuration
@BeanMock
public class HappyMusicControllerITConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@Primary
	public CoverArtClient coverArtClient() {
		return new CoverArtClientImpl() {

			@Override
			protected String fetchJson(String mbid) {
				try {
					final String jsonFile = "/json/coverart.json";
					logger.debug(LogMessage.createForAction("fetchJson")
							.addPart("message", "Mocked fetch json.")
							.addPart("jsonFile", jsonFile).toString());
					return FileUtil.readText(jsonFile);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Problems fetching json from file!");
				}
			}
		};
	}

	@Bean
	@Primary
	public MusicBrainzClient musicBrainzClient() {
		return new MusicBrainzClientImpl() {

			@Override
			protected String fetchJson(String mbid) {
				try {
					final String jsonFile = "/json/musicbrainz.json";
					logger.debug(LogMessage.createForAction("fetchJson")
							.addPart("message", "Mocked fetch json.")
							.addPart("jsonFile", jsonFile).toString());
					return FileUtil.readText(jsonFile);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Problems fetching json from file!");
				}
			}
		};
	}

	@Bean
	@Primary
	public WikipediaClient wikipediaClient() {
		return new WikipediaClientImpl() {

			@Override
			protected String fetchJson(String mbid) {
				try {
					final String jsonFile = "/json/wikipedia.json";
					logger.debug(LogMessage.createForAction("fetchJson")
							.addPart("message", "Mocked fetch json.")
							.addPart("jsonFile", jsonFile).toString());
					return FileUtil.readText(jsonFile);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Problems fetching json from file!");
				}
			}
		};
	}
}
