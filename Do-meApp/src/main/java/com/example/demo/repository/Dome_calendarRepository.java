package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Dome_calendar;

public interface Dome_calendarRepository extends JpaRepository<Dome_calendar, Integer> {
	List<Dome_calendar> findByTitleAndStart(String title, String start);
}