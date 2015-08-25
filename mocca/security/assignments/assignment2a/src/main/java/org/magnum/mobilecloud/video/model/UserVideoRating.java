package org.magnum.mobilecloud.video.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.common.base.Objects;

@Entity
public class UserVideoRating {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	//private long videoId;
	@ManyToOne
	private Video video;

	private double rating;

	private String user;

	public UserVideoRating() {
	}

	public UserVideoRating(Video video, double rating, String user) {
		super();
		this.video = video;
		this.rating = rating;
		this.user = user;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(video.getId(), user);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserVideoRating) {
			UserVideoRating other = (UserVideoRating) obj;
			return Objects.equal(video.getId(), other.video.getId())
					&& Objects.equal(user, other.user);
		} else {
			return false;
		}
	}

}
