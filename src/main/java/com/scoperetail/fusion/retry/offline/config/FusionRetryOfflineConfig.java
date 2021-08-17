package com.scoperetail.fusion.retry.offline.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.scoperetail.fusion.core.FusionCoreConfig;

@Configuration
@Import({FusionCoreConfig.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class FusionRetryOfflineConfig {}
