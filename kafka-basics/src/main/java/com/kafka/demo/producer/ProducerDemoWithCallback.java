package com.kafka.demo.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());
    public static void main(String[] args) {
        LOGGER.info("I am a Kafka Producer!");

        //Create producer Properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "https://huge-mudfish-14820-eu2-kafka.upstash.io:9092");
        properties.put("sasl.mechanism", "SCRAM-SHA-256");
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"aHVnZS1tdWRmaXNoLTE0ODIwJLOvzfDAWM62jf98tRlHTQtsXSkkkGfKh7kXE8I\" password=\"OWE4ZmJlOWYtZDY3Ny00NmE3LTg4MjUtMDc1NGRiODQ4NTU3\";");

        // Set Producer Properties
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("batch.size", 400);

//        properties.put("partitioner.class", RoundRobinPartitioner.class.getName());

        //Create the Producer
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);


        for (int j=0;j<10;j++) {
            for (int i=0;i<30;i++) {
                // Create Producer Record
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_topic", "Hello World! Piyush....: " + i);

                kafkaProducer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        // This is executed when a message is successful sent or Exception thrown
                        if (e == null) {
                            LOGGER.info("Received new Metadata \n\nTopic: {}\nPartition: {}\nOffset: {}\nTimeStamp: {}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                        }
                    }
                });
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



        // Tell the Producer to send all the data and block untill done - Synchronous
        kafkaProducer.flush();


        // flush and close the producer
        kafkaProducer.close();
    }
}