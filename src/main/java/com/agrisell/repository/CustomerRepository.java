package com.agrisell.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrisell.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
