/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProductMessage implements Serializable {
  private Long id;
  private String title;
  private String price;
  private ProductAction action;
}
