package se.panok.music.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Album {

	private String id;

	private String title;

	private String image;

	@JsonProperty("primary-type")
	private String type;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
