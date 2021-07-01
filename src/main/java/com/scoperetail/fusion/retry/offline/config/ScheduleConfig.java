package com.scoperetail.fusion.retry.offline.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.scoperetail.fusion.core.FusionCoreConfig;

@Configuration
@EnableScheduling
@EnableConfigurationProperties
@Import({ FusionCoreConfig.class })
public class ScheduleConfig {

}
