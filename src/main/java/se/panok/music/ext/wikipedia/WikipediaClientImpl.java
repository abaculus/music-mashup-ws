package se.panok.music.ext.wikipedia;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import se.panok.music.utils.LogMessage;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;

@Component
public class WikipediaClientImpl implements WikipediaClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${wikipedia.client.connectTimeout:1000}")
	private int connectTimeout;

	@Value("${wikipedia.client.readTimeout:3000}")
	private int readTimeout;

	@Value("${wikipedia.client.url:https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=%s}")
	private String url;

	@Value("${wikipedia.client.jsonPath.extract:$..extract}")
	private String jsonPathExtract;

	@Override
	public String getArtistExtract(final String artistId) {

		logger.debug(LogMessage.createForAction("getArtistExtract")
				.addPart("artistId", artistId).toString());

		String extract = null;

		if (StringUtils.isBlank(artistId)) {
			return extract;
		}

		final String json = fetchJson(artistId);
		logger.debug(LogMessage.createForAction("getArtistExtract")
				.addPart("json", json).toString());

		if (StringUtils.isNotBlank(json)) {
			try {
				final List<String> extracts = JsonPath.read(json,
						jsonPathExtract);
				final boolean isExtractsEmpty = CollectionUtils
						.isEmpty(extracts);
				logger.debug(LogMessage.createForAction("getArtistExtract")
						.addPart("isExtractsEmpty", isExtractsEmpty).toString());

				if (!isExtractsEmpty) {
					extract = extracts.get(0);
				}

				logger.debug(LogMessage.createForAction("getArtistExtract")
						.addPart("extract", extract).toString());
			} catch (final JsonPathException e) {
				logger.error(
						LogMessage.createForAction("getArtistExtract")
								.addPart("artistId", artistId)
								.addPart("json", json)
								.addPart("connectTimeout", connectTimeout)
								.addPart("readTimeout", readTimeout)
								.addPart("url", url)
								.addPart("jsonPathImage", jsonPathExtract)
								.toString(), e);
			}
		}
		return extract;
	}

	protected String fetchJson(final String artistId) {
		String json = null;
		String formattedUrl = null;
		try {
			logger.debug(LogMessage.createForAction("fetchJson")
					.addPart("url", url).addPart("artistId", artistId)
					.addPart("connectTimeout", connectTimeout)
					.addPart("readTimeout", readTimeout).toString());

			formattedUrl = String.format(url, artistId);
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
							.addPart("artistId", artistId)
							.addPart("connectTimeout", connectTimeout)
							.addPart("readTimeout", readTimeout).toString(), e);
		}
		return json;
	}

}
