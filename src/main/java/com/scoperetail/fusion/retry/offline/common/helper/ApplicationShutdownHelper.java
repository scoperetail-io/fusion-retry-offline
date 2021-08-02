package com.scoperetail.fusion.retry.offline.common.helper;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ApplicationShutdownHelper {
  private final ApplicationContext applicationContext;
  private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

  public synchronized void shutDownApplication() {
    if (!isShuttingDown.get()) {
      isShuttingDown.set(true);
      log.info("Application shut down initiated");
      log.info("Closing spring context");
      final int exitCode =
          SpringApplication.exit(
              applicationContext,
              new ExitCodeGenerator() {
                @Override
                public int getExitCode() {
                  // no errors
                  return 0;
                }
              });
      log.info("Spring context closed.Exiting application...");
      System.exit(exitCode);
    }
  }
}
