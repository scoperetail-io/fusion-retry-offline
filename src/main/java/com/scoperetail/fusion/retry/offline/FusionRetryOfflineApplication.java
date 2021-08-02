package com.scoperetail.fusion.retry.offline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.scoperetail.fusion.core.FusionCoreConfig;
import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor
@Import({FusionCoreConfig.class})
public class FusionRetryOfflineApplication {

  public static void main(final String[] args) {
    SpringApplication.run(FusionRetryOfflineApplication.class, args);
  }
}
