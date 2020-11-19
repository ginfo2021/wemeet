package com.wemeet.dating.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class AWSConfig {
    
    @Bean
    public AmazonS3 client(AWSCredentialsProvider awsCredentialsProvider) {
        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentialsProvider);
        amazonS3Client.setRegion(Region.getRegion(Regions.valueOf("eu-west-1")));
        return amazonS3Client;
    }

    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader(AmazonS3 client) {
        return new SimpleStorageResourceLoader(client);
    }

    @Bean
    @Primary
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }
}
