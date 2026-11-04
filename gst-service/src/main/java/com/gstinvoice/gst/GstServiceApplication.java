package com.gstinvoice.gst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GstServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GstServiceApplication.class, args);
    }
}

