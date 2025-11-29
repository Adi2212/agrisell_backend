package com.agrisell.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrisell.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
}

