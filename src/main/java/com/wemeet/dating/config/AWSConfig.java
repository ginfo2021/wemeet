package com.wemeet.dating.config;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AWSConfig {

    @Autowired
    private WemeetConfig wemeetConfig;

    @Bean
    public AmazonS3 amazonS3() {
        InstanceProfileCredentialsProvider provider
                = new InstanceProfileCredentialsProvider(true);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    @Bean
    public SimpleStorageResourceLoader simpleStorageResourceLoader(AmazonS3 amazonS3) {
        return new SimpleStorageResourceLoader(amazonS3);
    }

//    @Bean
//    @Primary
//    public AWSCredentialsProvider awsCredentialsProvider() {
//        return new DefaultAWSCredentialsProviderChain();
//    }
}
