package com.scoperetail.fusion.retry.offline.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.scoperetail.fusion"})
public class ScheduleConfig {

}
