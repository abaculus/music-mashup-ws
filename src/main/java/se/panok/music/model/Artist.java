package se.panok.music.model;

import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class Artist {

	@JsonProperty("id")
	private String mbid;

	private String description;

	private List<Relation> relations = Lists.newArrayList();

	@JsonProperty("release-groups")
	private List<Album> albums = Lists.newArrayList();

	public String getMbid() {
		return mbid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public String getWikipediaArtistId() {
		String wikipediaArtistId = null;
		if (null != relations && !relations.isEmpty()) {
			final Relation relation = relations.get(0);
			wikipediaArtistId = relation.getWikipediaArtistId();
		}
		return wikipediaArtistId;
	}

	public void applyFilter(final Filter filter) {
		if (null != filter) {
			filter.apply(this);
		}
	}

	// Cannot filter on null or empty string
	public void applyFilter(final FilterType filterType, final String value) {
		if (null == filterType || StringUtils.isEmpty(value)) {
			// do nothing
			return;
		}
		Filter filter = null;
		switch (filterType) {
		case RELEASE_GROUP_TYPE:
			filter = new ReleaseGroupTypeValueFilter(value);
			break;
		case RELATION_TYPE:
			filter = new RelationTypeValueFilter(value);
			break;
		default:
			// do nothing
			return;
		}
		filter.apply(this);
	}

	public enum FilterType {

		RELEASE_GROUP_TYPE,

		RELATION_TYPE;
	}

	private interface Filter {

		void apply(Artist artist);
	}

	private static final class ReleaseGroupTypeValueFilter implements Filter {

		private final String value;

		private ReleaseGroupTypeValueFilter(final String value) {
			this.value = value;
		}

		@Override
		public void apply(final Artist artist) {
			if (null != artist && !CollectionUtils.isEmpty(artist.getAlbums())) {
				artist.getAlbums().removeIf(
						a -> !value.equalsIgnoreCase(a.getType()));
			}
		}

	}

	private static final class RelationTypeValueFilter implements Filter {

		private final String value;

		private RelationTypeValueFilter(final String value) {
			this.value = value;
		}

		@Override
		public void apply(final Artist artist) {
			if (null != artist
					&& !CollectionUtils.isEmpty(artist.getRelations())) {
				artist.getRelations().removeIf(
						r -> !value.equalsIgnoreCase(r.getType()));
			}
		}

	}
}
