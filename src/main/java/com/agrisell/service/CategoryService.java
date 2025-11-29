package com.agrisell.service;

import java.util.List;
import java.util.stream.Collectors;

import com.agrisell.dto.CreateCategoryRequest;
import com.agrisell.dto.MainCategoryDTO;
import com.agrisell.dto.SubCategoryDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrisell.dto.CategoryDTO;
import com.agrisell.model.Category;
import com.agrisell.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
	private final ModelMapper mapper;
	private final CategoryRepository categoryRepo;

    public ResponseEntity<?> addCategory(CreateCategoryRequest req) {

        Category category = new Category();
        category.setName(req.getName());
        category.setImageUrl(req.getImageUrl());

        // 🔥 If parentId = null → MAIN CATEGORY
        if (req.getParentId() != null) {
            Category parent = categoryRepo.findById(req.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null); // Main Category
        }

        Category saved = categoryRepo.save(category);
        if (req.getParentId() != null) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(saved, SubCategoryDTO.class));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(saved, MainCategoryDTO.class));
    }



    public List<CategoryDTO> getCategories() {
	        return categoryRepo.findAll()
	                .stream()
	                .map(category -> mapper.map(category, CategoryDTO.class))
	                .collect(Collectors.toList());
	    }
	 
	 public CategoryDTO getCategoryById(Long id) {
	        Category category = categoryRepo.findById(id)
	                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
	        return mapper.map(category, CategoryDTO.class);
	    }

    public List<MainCategoryDTO> getMainCategories() {
        return categoryRepo.findByParentIsNull()
                .stream()
                .map(category -> mapper.map(category, MainCategoryDTO.class))
                .toList();
    }

    public List<SubCategoryDTO> getSubCategories(Long parentId) {
        return categoryRepo.findByParentId(parentId)
                .stream()
                .map(category -> mapper.map(category, SubCategoryDTO.class))
                .toList();
    }
}
