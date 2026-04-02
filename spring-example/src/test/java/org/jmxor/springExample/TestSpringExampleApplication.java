package org.jmxor.springExample;

import org.springframework.boot.SpringApplication;

public class TestSpringExampleApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringExampleApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
