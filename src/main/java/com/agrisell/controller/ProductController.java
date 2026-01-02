package com.agrisell.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrisell.dto.AddProductDTO;
import com.agrisell.dto.ProductDTO;
import com.agrisell.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_FARMER','ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ProductDTO> add(@RequestBody AddProductDTO dto, HttpServletRequest request) {
        return ResponseEntity.ok(productService.addProduct(dto, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_FARMER','ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody AddProductDTO dto, HttpServletRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, dto, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_FARMER','ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            //System.out.println("Authenticated User: " + request.getUserPrincipal());

            return ResponseEntity.ok(productService.deleteProduct(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_FARMER')")
    @GetMapping("/farmer")
    public ResponseEntity<List<ProductDTO>> getFarmer( HttpServletRequest request) {
        return ResponseEntity.ok(productService.getProductsByUserId(request));
    }
}
