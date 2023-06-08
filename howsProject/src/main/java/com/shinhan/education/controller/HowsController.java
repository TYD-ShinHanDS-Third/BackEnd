package com.shinhan.education.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.vo.Pans;

@RestController
@RequestMapping("/hows")
public class HowsController {
	@Autowired
	PanRepository panRepo;

	@GetMapping("/notice")
	public List<Pans> allpans() {
		return (List<Pans>) panRepo.findAll();
	}

}
