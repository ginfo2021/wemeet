package com.wemeet.dating;

import com.wemeet.dating.dao.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class DatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatingApplication.class, args);
    }

}
