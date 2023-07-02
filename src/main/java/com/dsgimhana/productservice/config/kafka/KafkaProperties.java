/*
 * Copyright (c) 2023 DSGIMHANA
 * Author: H.G.D.S GIMHANA
 */
package com.dsgimhana.productservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

  private String bootstrapAddress;
  private String groupId;
  private int replicationFactor;
  private String productPublishTopic;

  private int productPublishTopicPartitionsCount;

  private String minInSyncReplica;
}
