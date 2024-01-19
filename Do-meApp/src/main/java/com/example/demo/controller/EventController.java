package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Dome_calendar;
import com.example.demo.repository.Dome_calendarRepository;

@RestController
@RequestMapping("/events")
public class EventController {
	@Autowired
	Dome_calendarRepository dome_calendarRepository;

	@GetMapping("/all")
	public ResponseEntity<List<Dome_calendar>> getAllEvents() {
		List<Dome_calendar> events = dome_calendarRepository.findAll();
		return new ResponseEntity<>(events, HttpStatus.OK);
	}
}