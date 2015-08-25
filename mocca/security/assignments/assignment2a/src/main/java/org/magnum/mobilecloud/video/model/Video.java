package org.magnum.mobilecloud.video.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;


@Entity
public class Video {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String title;
	private String url;
	private long duration;
	private String location;
	private String subject;
	private String contentType;
	
	@JsonIgnore
	private double averageRating;

	// We don't want to bother unmarshalling or marshalling
	// any owner data in the JSON. Why? We definitely don't
	// want the client trying to tell us who the owner is.
	// We also might want to keep the owner secret.
	@JsonIgnore
	private String owner;
	
	@JsonIgnore
	@OneToMany(mappedBy="video", cascade = CascadeType.ALL)
	@MapKey(name="user")
	private Map<String, UserVideoRating> ratings;

	public Video() {
	}

	public Video(String owner, String name, String url, long duration,
			long likes, Set<String> likedBy) {
		super();
		this.owner = owner;
		this.title = name;
		this.url = url;
		this.duration = duration;
		this.ratings = new HashMap<String, UserVideoRating>();
		this.averageRating = 0;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@JsonIgnore
	private void recalculateAverageRating() {
		double avg = 0;
		int ctr = 0;

		for(UserVideoRating uvr : ratings.values())
			avg = ((avg * ctr) + uvr.getRating()) / ++ctr;
		
		averageRating = avg;
	}
	
	@JsonIgnore
	public Map<String, UserVideoRating> getRatings() {
		return ratings;
	}
	
	@JsonIgnore
	public void addRating(UserVideoRating uvr) {
		ratings.put(uvr.getUser(), uvr);
		recalculateAverageRating();
	}
	
	@JsonIgnore
	public double getAverageRating() {
		return this.averageRating;
	}
	
	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their name, url, and duration.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(title, url, duration, owner);
	}

	/**
	 * Two Videos are considered equal if they have exactly the same values for
	 * their name, url, and duration.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Video) {
			Video other = (Video) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(title, other.title)
					&& Objects.equal(url, other.url)
					//&& Objects.equal(owner, other.owner)
					&& duration == other.duration;
		} else {
			return false;
		}
	}
}
