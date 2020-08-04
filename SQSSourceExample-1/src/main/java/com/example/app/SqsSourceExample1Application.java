package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
//import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication(exclude = ContextStackAutoConfiguration.class)
@EnableBinding({Source.class})
@EnableScheduling
@EnableIntegration
public class SqsSourceExample1Application {

    public static void main(String[] args) {
        SpringApplication.run(SqsSourceExample1Application.class, args);
    }
//    @Autowired
//    private QueueMessagingTemplate queueMessagingTemplate;
    @Autowired
    private Source source;
   // private AmazonSQS basicSqsClient;
	@Value("${cloud.aws.region.static}")
	private String awsRegion;
    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;
    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;
    @Bean
    @Primary
    public AWSCredentialsProvider buildDefaultAWSCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
    }

//    @Autowired
//    private MySource mySource;

    @Scheduled(fixedRate = 1000L)
    void publishPlainTextMessageJob() {
        String payload = "Current time is " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        System.out.println(payload);
        source.output().send(MessageBuilder.withPayload(payload).build());
//        queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(payload).build());
        
    }

//    @Scheduled(fixedRate = 1000L)
//    void publishJsonMessageJob() {
//        mySource.output().send(MessageBuilder.withPayload(new Person("Lena"))
//                                           .build());
//    }

//     if there is a need for more outputs they can be defined in separate interface
    interface MySource {
        @Output("customOutput") // configuration for this output is in application.yml
        MessageChannel output();
    }

    static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
