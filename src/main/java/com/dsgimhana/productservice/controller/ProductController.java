package com.dsgimhana.productservice.controller;

import com.dsgimhana.productservice.dto.request.ProductRQ;
import com.dsgimhana.productservice.dto.response.ProductRS;
import com.dsgimhana.productservice.entity.ProductEntity;
import com.dsgimhana.productservice.exception.NotFoundException;
import com.dsgimhana.productservice.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<ProductRS>> getAllProducts() {
		List<ProductRS> products = productService.getAllProducts()
				.stream()
				.map(productEntity -> modelMapper.map(productEntity, ProductRS.class))
				.toList();
		return ResponseEntity.ok().body(products);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductRS> getProductById(@PathVariable Long id) {
		try {
			ProductEntity productEntity = productService.getProductById(id);
			return ResponseEntity.ok(modelMapper.map(productEntity,ProductRS.class));
		} catch (NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@CacheEvict(value = "products", allEntries = true)
	@PostMapping
	public ResponseEntity<ProductRS> createProduct(@RequestBody ProductRQ product) {
		ProductEntity createdProductEntity = productService.createProduct(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdProductEntity, ProductRS.class));
	}

	@CacheEvict(value = "products", key = "#id")
	@PutMapping("/{id}")
	public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody ProductRQ product) {
		try {
			ProductEntity updatedProductEntity = productService.updateProduct(id, product);
			return ResponseEntity.ok(updatedProductEntity);
		} catch (NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Long> deleteProduct(@PathVariable Long id) {
		try {
			productService.deleteProduct(id);
			return ResponseEntity.ok(id);
		} catch (NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

}
