package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.model.Video;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long> {

	List<Video> findByTitle(String title);
	
}
