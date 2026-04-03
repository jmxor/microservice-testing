package org.jmxor.springExample;

import org.jmxor.springExample.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class SpringExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringExampleApplication.class, args);
    }

}
