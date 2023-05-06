package com.dsgimhana.productservice.repository;

import com.dsgimhana.productservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository  extends JpaRepository<ProductEntity, Long> {
}
