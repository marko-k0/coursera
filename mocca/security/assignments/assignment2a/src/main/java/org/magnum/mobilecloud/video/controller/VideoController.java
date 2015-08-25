/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.mobilecloud.video.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.model.AverageVideoRating;
import org.magnum.mobilecloud.video.model.UserVideoRating;
import org.magnum.mobilecloud.video.model.Video;
import org.magnum.mobilecloud.video.model.VideoStatus;
import org.magnum.mobilecloud.video.model.VideoStatus.VideoState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoController {
	
    @Autowired
    private VideoRepository videoRepository;

	@RequestMapping(value="/video", method=RequestMethod.GET)
	public @ResponseBody List<Video> getVideoList() {
		ArrayList<Video> videoList = new ArrayList<Video>();
		for(Video v : videoRepository.findAll())
			videoList.add(v);

		return videoList;	
	}
	
	@RequestMapping(value="/video", method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video video, Principal principal) {
		video.setOwner(principal.getName());
		Video savedVideo = videoRepository.save(video);

		return savedVideo;
	}

	@RequestMapping(value="/video/{id}/rating/{rate}", method=RequestMethod.POST)
	public @ResponseBody AverageVideoRating addVideoRating(
			@PathVariable("id") long id,
			@PathVariable("rate") float rate,
			Principal principal,
			HttpServletResponse response) throws Exception {
		
		Video video = videoRepository.findOne(id);
		if(video == null) {
			String msg = "(POST) Video with this id does not exists!";
			response.sendError(404, msg);
			return null;
		}
		
		String user = principal.getName();
		video.addRating(new UserVideoRating(video, rate, user));
		videoRepository.save(video);	

		return new AverageVideoRating(
				video.getAverageRating(),
				video.getId(), 
				video.getRatings().size());
	}

	@RequestMapping(value="/video/{id}/rating", method=RequestMethod.GET)
	public @ResponseBody AverageVideoRating getVideoRating(
			@PathVariable("id") long id,
			HttpServletResponse response) throws Exception {
		
		Video video = videoRepository.findOne(id);
		if(video == null) {
			String msg = "(POST) Video with this id does not exists!";
			response.sendError(404, msg);
			return null;
		}

		return new AverageVideoRating(
				video.getAverageRating(),
				video.getId(), 
				video.getRatings().size());
	}
	
	@RequestMapping(value="/video/{id}/data", method=RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(
			@PathVariable("id") long id, 
			@RequestParam("data" ) MultipartFile videoData,
			Principal principal,
			HttpServletResponse response) throws Exception {
		
		Video video = videoRepository.findOne(id);
		if(video == null) {
			String msg = "(GET) Video with this id does not exists!";
			response.sendError(404, msg);
			return null;
		} else if (! video.getOwner().equals(principal.getName())) {
			String msg = "(POST) Overriding someone else's video not allowed!";
			response.sendError(405, msg);
			return null;
		}
		
		InputStream inStream = videoData.getInputStream();
		VideoFileManager vfm = new VideoFileManager();
		vfm.saveVideoData(video, inStream);
		
		return new VideoStatus(VideoState.READY);
	}
	
	@RequestMapping(value="/video/{id}/data", method=RequestMethod.GET)
	public @ResponseBody FileSystemResource getVideoData(
			@PathVariable("id") long id,
			HttpServletResponse response) throws IOException {
		
		Video video = videoRepository.findOne(id);
		if(video == null) {
			String msg = "(POST) Video with this id does not exists!";
			response.sendError(404, msg);
			return null;
		}
		
		return new FileSystemResource(new VideoFileManager().getFile(video));
	}
	
	
	private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
       HttpServletRequest request = 
           ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       String base = 
          "http://"+request.getServerName() 
          + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
       return base;
    }

}
