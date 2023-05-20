/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.utils;

import com.dsgimhana.productservice.model.ProductMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ProductMessageSerializer implements Serializer<ProductMessage> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public byte[] serialize(String topic, ProductMessage data) {
    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new SerializationException("Error occurred during ProductMessage serialization", e);
    }
  }
}
