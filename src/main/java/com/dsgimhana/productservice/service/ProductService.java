/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.service;

import com.dsgimhana.productservice.dto.request.ProductRQ;
import com.dsgimhana.productservice.entity.ProductEntity;
import java.util.List;

public interface ProductService {
  List<ProductEntity> getAllProducts();

  ProductEntity getProductById(Long id);

  ProductEntity createProduct(ProductRQ product);

  ProductEntity updateProduct(Long id, ProductRQ product);

  void deleteProduct(Long id);
}
