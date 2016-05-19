package se.panok.music.ext.coverart;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import se.panok.music.utils.LogMessage;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;

@Component
public class CoverArtClientImpl implements CoverArtClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${coverArt.client.connectTimeout:1000}")
	private int connectTimeout;

	@Value("${coverArt.client.readTimeout:3000}")
	private int readTimeout;

	@Value("${coverArt.client.url:http://coverartarchive.org/release-group/%s}")
	private String url;

	@Value("${coverArt.client.jsonPath.image:$.images[0].image}")
	private String jsonPathImage;

	@Override
	public String getCoverArtUrl(final String mbid) {
		logger.debug(LogMessage.createForAction("getCoverArtUrl")
				.addPart("mbid", mbid).toString());

		if (StringUtils.isBlank(mbid)) {
			return null;
		}

		String coverArtUrl = null;
		String json = null;

		json = fetchJson(mbid);
		logger.debug(LogMessage.createForAction("getCoverArtUrl")
				.addPart("json", json).toString());

		if (StringUtils.isNotBlank(json)) {
			try {

				coverArtUrl = JsonPath.read(json, jsonPathImage);

				logger.debug(LogMessage.createForAction("getCoverArtUrl")
						.addPart("covertArtUrl", coverArtUrl).toString());
			} catch (final JsonPathException e) {
				logger.error(
						LogMessage.createForAction("getCoverArtUrl")
								.addPart("mbid", mbid).addPart("json", json)
								.addPart("connectTimeout", connectTimeout)
								.addPart("readTimeout", readTimeout)
								.addPart("url", url)
								.addPart("jsonPathImage", jsonPathImage)
								.toString(), e);
			}
		}

		return coverArtUrl;
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
							.addPart("readTimeout", readTimeout)
							.addPart("exceptionMessage", e.getMessage())
							.toString(), e);
		}
		return json;
	}
}
