package se.panok.music.web.dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;

public class Artist {

	private se.panok.music.model.Artist delegate;

	private Artist(final se.panok.music.model.Artist delegate) {
		this.delegate = delegate;
	}

	public String getMbid() {
		return delegate.getMbid();
	}

	public String getDescription() {
		return delegate.getDescription();
	}

	public List<Album> getAlbums() {
		return Album.convert(delegate.getAlbums());
	}

	public static Artist convert(final se.panok.music.model.Artist delegate) {
		return new Artist(delegate);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("mbid", getMbid())
				.append("descriptionLength",
						StringUtils.length(getDescription()))
				.append("albumCount", getAlbums().size()).toString();
	}

}
