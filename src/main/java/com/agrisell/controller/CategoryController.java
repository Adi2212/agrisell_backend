package com.agrisell.controller;

import java.util.List;

import com.agrisell.dto.CreateCategoryRequest;
import com.agrisell.model.Category;
import com.agrisell.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrisell.dto.CategoryDTO;
import com.agrisell.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/main")
    public  ResponseEntity<?> getMainCategories() {
        return  ResponseEntity.ok(categoryService.getMainCategories());
    }

    @GetMapping("/sub/{parentId}")
    public ResponseEntity<?> getSubCategories(@PathVariable Long parentId) {
        return  ResponseEntity.ok(categoryService.getSubCategories(parentId));
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CreateCategoryRequest req) {
        return categoryService.addCategory(req);
    }



    // 🔹 Get all categories
    @GetMapping("/")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {

        return ResponseEntity.ok(categoryService.getCategories());
    }

    // 🔹 Get single category
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
