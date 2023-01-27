package com.epam.training.microservicefoundation.resourceservice.service.kafka;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaExtension implements BeforeAllCallback, AfterAllCallback {

    @Container
    private KafkaContainer container;

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        container.start();

        System.setProperty("kafka.bootstrap-servers", container.getBootstrapServers());
//        System.setProperty("kafka.consumer.concurrency", "3");
//        System.setProperty("kafka.topic.partitions.count", "3");
//        System.setProperty("kafka.topic.replication.factor", "3");
//        System.setProperty("kafka.topic.resources", "resources");
    }
}
