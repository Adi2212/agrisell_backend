package com.agrisell.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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

	public CategoryDTO addCategory(CategoryDTO categoryDTO) {
		Category category = mapper.map(categoryDTO, Category.class);
        Category saved = categoryRepo.save(category);
        return mapper.map(saved, CategoryDTO.class);
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
}
