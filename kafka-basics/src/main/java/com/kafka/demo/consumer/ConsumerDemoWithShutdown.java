package com.kafka.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWithShutdown {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoWithShutdown.class.getSimpleName());
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

        //Get a reference to the main thread.
        final Thread thread = Thread.currentThread();

        //Adding a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOGGER.info("Detected a shutdown. Lets exit by calling consumer.wakeup()...");
                kafkaConsumer.wakeup();

                // Join the main thread to allow the execution of the code in the main thread

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        try {
            // Subscribe a topic
            kafkaConsumer.subscribe(Arrays.asList(topic));
            //Consume Data
            while (true) {
                ConsumerRecords<String, String> consumerRecords= kafkaConsumer.poll(Duration.ofMillis(1000));
                consumerRecords.forEach(consumerRecord -> {
                    LOGGER.info("Key::" + consumerRecord.key(), " Value::" + consumerRecord.value());
                    LOGGER.info("Partition::" + consumerRecord.partition() + " Offset::" + consumerRecord.offset());

                });
            }
        } catch (WakeupException e) {
            LOGGER.info("Consumer is Starting to Shutdown");
        } catch (Exception e) {
            LOGGER.error("UnExpected exception in the consumer " + e);
        } finally {
            kafkaConsumer.close(); // Consumer will close and also it will commit the offset
            LOGGER.info("THe consumer is gracefully shutdown");
        }
    }
}