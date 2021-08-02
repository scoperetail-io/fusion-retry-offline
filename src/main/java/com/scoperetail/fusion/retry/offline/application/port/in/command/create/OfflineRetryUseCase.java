package com.scoperetail.fusion.retry.offline.application.port.in.command.create;

public interface OfflineRetryUseCase {

  void doValidationFailure(String message);

  void retryOffline(Object message) throws Exception;

  void shutDownApplication();
}
