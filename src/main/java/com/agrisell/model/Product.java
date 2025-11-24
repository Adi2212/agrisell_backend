package com.agrisell.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String description;

	private Double price;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	private String imageUrl;
	
	private int stockQuantity;

	// ✅ Loosely coupled link to user (farmer)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // Comes from token
}
