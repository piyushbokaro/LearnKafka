package com.kafka.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());
    public static void main(String[] args) {
        LOGGER.info("I am a Kafka Producer!");

        String groupId = "my-java-application";
        String topic = "demo_topic";
        //Create producer Properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "https://huge-mudfish-14820-eu2-kafka.upstash.io:9092");
        properties.put("sasl.mechanism", "SCRAM-SHA-256");
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"aHVnZS1tdWRmaXNoLTE0ODIwJLOvzfDAWM62jf98tRlHTQtsXSkkkGfKh7kXE8I\" password=\"OWE4ZmJlOWYtZDY3Ny00NmE3LTg4MjUtMDc1NGRiODQ4NTU3\";");

        // Set Consumer Properties
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

        properties.put("group.id", groupId);
        properties.put("auto.offset.reset", "earliest");

        //Create the Consumer
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        // Subscribe a topic
        kafkaConsumer.subscribe(Arrays.asList(topic));

        //Consume Data
        while (true) {
            LOGGER.info("Polling Data");
            ConsumerRecords<String, String> consumerRecords= kafkaConsumer.poll(Duration.ofMillis(1000));
            consumerRecords.forEach(consumerRecord -> {
                LOGGER.info("Key::" + consumerRecord.key(), " Value::" + consumerRecord.value());
                LOGGER.info("Partition::" + consumerRecord.partition() + " Offset::" + consumerRecord.offset());

            });
        }
    }
}