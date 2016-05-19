package se.panok.music.web.dto;

import java.util.List;

import com.google.common.collect.Lists;

public class Album {

	private se.panok.music.model.Album delegate;

	private Album(final se.panok.music.model.Album delegate) {
		this.delegate = delegate;
	}

	public String getId() {
		return delegate.getId();
	}

	public String getTitle() {
		return delegate.getTitle();
	}

	public String getImage() {
		return delegate.getImage();
	}

	public static Album convert(final se.panok.music.model.Album delegate) {
		return new Album(delegate);
	}

	public static List<Album> convert(
			final List<se.panok.music.model.Album> albumList) {
		final List<Album> albums = Lists.newArrayList();
		if (null != albumList) {
			for (final se.panok.music.model.Album albumDelegate : albumList) {
				albums.add(convert(albumDelegate));
			}
		}
		return albums;
	}

}
