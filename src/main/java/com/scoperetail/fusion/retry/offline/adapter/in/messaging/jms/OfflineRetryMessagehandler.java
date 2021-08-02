package com.scoperetail.fusion.retry.offline.adapter.in.messaging.jms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.scoperetail.fusion.core.adapter.in.messaging.jms.AbstractMessageListener;
import com.scoperetail.fusion.messaging.adapter.out.messaging.jms.MessageRouterReceiver;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OfflineRetryUseCase;

@Component
public class OfflineRetryMessagehandler extends AbstractMessageListener {
  private final OfflineRetryUseCase offlineRetryUseCase;

  public OfflineRetryMessagehandler(
      final MessageRouterReceiver messageRouterReceiver,
      @Value("${brokerId}") final String brokerId,
      @Value("${queueName}") final String queueName,
      final OfflineRetryUseCase offlineRetryUseCase) {
    super(brokerId, queueName, MessageType.JSON, null, null, messageRouterReceiver);
    this.offlineRetryUseCase = offlineRetryUseCase;
  }

  @Override
  public void handleValidationFailure(final String message) throws Exception {
    offlineRetryUseCase.doValidationFailure(message);
  }

  @Override
  public void handleMessage(final Object event) throws Exception {
    offlineRetryUseCase.retryOffline(event);
  }

  @Override
  public void handleFailure(final String message) {
    offlineRetryUseCase.shutDownApplication();
  }
}
