/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  @Autowired private KafkaProperties kafkaProperties;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic gdsPublishTopicCreation() {
    return TopicBuilder.name(kafkaProperties.getProductPublishTopic())
        .partitions(kafkaProperties.getProductPublishTopicPartitionsCount())
        .replicas(kafkaProperties.getReplicationFactor())
        .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, kafkaProperties.getMinInSyncReplica())
        .build();
  }
}
