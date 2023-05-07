package com.dsgimhana.productservice.model;

import lombok.Data;

@Data
public class ProductMessage {
	private Long id;
	private String title;
	private String price;
	private ProductAction action;
}
