package com.agrisell.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.agrisell.model.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {}

