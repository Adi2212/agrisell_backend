package com.agrisell.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrisell.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
