package com.scoperetail.fusion.retry.offline.application.service.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OfflineRetryUseCase;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplicationTimeoutWatchdog {

  public ApplicationTimeoutWatchdog(
      @Value("${timeOutInSeconds}") final long timeOutInSeconds,
      final OfflineRetryUseCase offlineRetryUseCase) {
    timeout(timeOutInSeconds, offlineRetryUseCase);
  }

  private void timeout(final long timeOutInSeconds, final OfflineRetryUseCase offlineRetryUseCase) {
    new Thread(
            () -> {
              try {
                Thread.sleep(timeOutInSeconds * 1000);
                offlineRetryUseCase.shutDownApplication();
              } catch (final InterruptedException e) {
                log.error("Exception occured: {}", e);
              }
            })
        .start();
  }
}
