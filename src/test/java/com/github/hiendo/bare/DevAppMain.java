package com.github.hiendo.bare;

import com.github.hiendo.bare.config.AppConfiguration;
import org.springframework.boot.SpringApplication;


public class DevAppMain {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(AppConfiguration.class);
        app.run(new String[]{"--debug"});
    }
}
