package com.clemble.casino.schedule.spring;

import com.clemble.casino.server.spring.WebBootSpringConfiguration;
import com.clemble.casino.server.spring.common.ClembleBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 3/31/15.
 */
@Configuration
@Import({ WebBootSpringConfiguration.class, ScheduleSpringConfiguration.class })
public class ScheduleApplication implements ClembleBootApplication {

     public static void main(String[] args) throws Exception {
        SpringApplication.run(ScheduleApplication.class, args);
    }

}
