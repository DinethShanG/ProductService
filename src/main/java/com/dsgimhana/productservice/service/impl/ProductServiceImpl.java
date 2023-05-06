package com.dsgimhana.productservice.service.impl;

import com.dsgimhana.productservice.dto.request.ProductRQ;
import com.dsgimhana.productservice.entity.ProductEntity;
import com.dsgimhana.productservice.exception.NotFoundException;
import com.dsgimhana.productservice.repository.ProductRepository;
import com.dsgimhana.productservice.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

	private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product with id %d not found";

	private final ProductRepository productRepository;

	@Autowired
	private ModelMapper modelMapper;


	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	@Cacheable(value = "products")
	public List<ProductEntity> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public ProductEntity getProductById(Long id) {
		Optional<ProductEntity> product = productRepository.findById(id);
		if (product.isPresent()) {
			return product.get();
		} else {
			throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id));
		}
	}

	@Override
	@CacheEvict(value = "products", allEntries = true)
	public ProductEntity createProduct(ProductRQ product) {
		return productRepository.save(modelMapper.map(product, ProductEntity.class));
	}

	@Override
	@CacheEvict(value = "products", allEntries = true)
	public ProductEntity updateProduct(Long id, ProductRQ product) {
		Optional<ProductEntity> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			ProductEntity existingProductEntity = optionalProduct.get();
			existingProductEntity.setTitle(product.getTitle());
			existingProductEntity.setPrice(product.getPrice());
			return productRepository.save(existingProductEntity);
		} else {
			throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id));
		}
	}

	@Override
	@CacheEvict(value = "products", allEntries = true)
	public void deleteProduct(Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
		} else {
			throw new NotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id));
		}
	}
}
