package se.panok.music.model;

import org.springframework.util.StringUtils;

public class Relation {

	private String type;

	private ResourceUrl url;

	public String getType() {
		return type;
	}

	public ResourceUrl getUrl() {
		return url;
	}

	public String getWikipediaArtistId() {
		String artistId = null;
		if (null != url && null != url.getResource()) {
			final String resourceUrl = url.getResource();
			if (!StringUtils.isEmpty(resourceUrl)) {
				final String artistIdCandidate = resourceUrl
						.substring(resourceUrl.lastIndexOf("/") + 1);
				artistId = StringUtils.isEmpty(artistIdCandidate) ? null
						: artistIdCandidate;
			}
		}
		return artistId;
	}
}
