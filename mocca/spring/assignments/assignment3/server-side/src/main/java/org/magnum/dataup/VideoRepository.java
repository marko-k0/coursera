package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long> {

	List<Video> findByTitle(String title);
	
}
