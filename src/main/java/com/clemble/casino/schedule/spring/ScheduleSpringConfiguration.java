package com.clemble.casino.schedule.spring;

import com.clemble.casino.schedule.job.ScheduleJobExecutor;
import com.clemble.casino.schedule.job.ScheduleJobExecutorFactory;
import com.clemble.casino.schedule.listener.SystemAddJobScheduleEventListener;
import com.clemble.casino.schedule.listener.SystemRemoveJobScheduleEventListener;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.player.notification.SystemNotificationServiceListener;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by mavarazy on 11/8/14.
 */
@Configuration
@Import({ CommonSpringConfiguration.class })
public class ScheduleSpringConfiguration implements SpringConfiguration {

    @Autowired
    public Environment env;

    @Bean
    public ScheduleJobExecutor jobExecutor(ObjectMapper objectMapper, SystemNotificationService notificationService){
        return new ScheduleJobExecutor(objectMapper, notificationService);
    }

    @Bean
    public SystemAddJobScheduleEventListener scheduleAddJobListener(
        ObjectMapper objectMapper,
        Scheduler scheduler,
        SystemNotificationServiceListener notificationServiceListener) {
        SystemAddJobScheduleEventListener systemAddJobScheduleEventListener = new SystemAddJobScheduleEventListener(objectMapper, scheduler);
        notificationServiceListener.subscribe(systemAddJobScheduleEventListener);
        return systemAddJobScheduleEventListener;
    }

    @Bean
    public SystemRemoveJobScheduleEventListener scheduleRemoveJobListener(
        Scheduler scheduler,
        SystemNotificationServiceListener notificationServiceListener) {
        SystemRemoveJobScheduleEventListener systemRemoveJobScheduleEventListener = new SystemRemoveJobScheduleEventListener(scheduler);
        notificationServiceListener.subscribe(systemRemoveJobScheduleEventListener);
        return systemRemoveJobScheduleEventListener;
    }

    @Bean
    public ScheduleJobExecutorFactory scheduleJobExecutorFactory(ScheduleJobExecutor jobExecutor) {
        return new ScheduleJobExecutorFactory(jobExecutor);
    }



    @Bean(destroyMethod = "shutdown")
    public Scheduler scheduler(
            ScheduleJobExecutorFactory jobExecutorFactory,
            @Value("${MONGO_SERVICE_SERVICE_HOST}") String host,
            @Value("${MONGO_SERVICE_SERVICE_PORT}") int port) throws SchedulerException, IOException {
        // Step 1. Read mongo properties
        Properties properties = new Properties();
        if (Arrays.asList(env.getActiveProfiles()).contains("cloud")) {
            properties.load(getClass().getResourceAsStream("/quartz.cloud.properties"));
        } else {
            properties.load(getClass().getResourceAsStream("/quartz.properties"));
        }
        properties.setProperty("org.quartz.jobStore.mongoUri", "mongodb://" + host + ":" + port);
        // Step 2. Initialize Schedule factory
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        schedulerFactory.initialize(properties);
        // Step 3. Create new scheduler
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.setJobFactory(jobExecutorFactory);
        scheduler.start();
        return scheduler;
    }

}
