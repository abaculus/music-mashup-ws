package se.panok.music.ext.musicbrainz;

import se.panok.music.model.Artist;

public interface MusicBrainzClient {

	Artist getArtist(final String mbid);
}
