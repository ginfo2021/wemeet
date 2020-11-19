package com.wemeet.dating.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private WemeetConfig wemeetConfig;

    @Bean
    public AmazonS3 amazonS3Client(AWSCredentialsProvider awsCredentialsProvider) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        builder.withCredentials(awsCredentialsProvider);
        builder.setRegion(wemeetConfig.getRegion());
        AmazonS3 amazonS3 = builder.build();
        return amazonS3;
    }

    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader(AmazonS3 amazonS3Client) {
        return new SimpleStorageResourceLoader(amazonS3Client);
    }

    @Bean
    @Primary
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }
}
