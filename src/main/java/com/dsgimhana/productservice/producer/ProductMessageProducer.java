/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.producer;

import com.dsgimhana.productservice.config.kafka.KafkaProperties;
import com.dsgimhana.productservice.model.ProductMessage;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductMessageProducer {
  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired private KafkaProperties kafkaProperties;

  public void sendProductMessage(ProductMessage payload) {
    CompletableFuture<SendResult<String, Object>> completableFuture =
        kafkaTemplate
            .send(
                new ProducerRecord<>(
                    kafkaProperties.getProductPublishTopic(), payload.getId().toString(), payload))
            .toCompletableFuture();
    completableFuture.whenComplete(
        (sendResult, exception) -> {
          if (exception != null) {
            handleFailure(payload, exception);
          } else {
            handleSuccess(payload, sendResult);
          }
        });
  }

  private void handleSuccess(ProductMessage value, SendResult<String, Object> result) {
    log.info(
        "The product with value : {} is produced successfully to offset {}",
        value.toString(),
        result.getRecordMetadata().offset());
  }

  private void handleFailure(ProductMessage value, Throwable ex) {
    log.error(
        "The product with value: {} cannot be published! caused by {}",
        value.toString(),
        ex.getMessage());
  }
}
