package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Dome_reservation;

public interface Dome_reservationRepository extends JpaRepository<Dome_reservation, Integer> {
	List<Dome_reservation> findByVersus(int versus);

	List<Dome_reservation> findByDateAndCourt(String date, String court);
}