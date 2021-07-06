package com.scoperetail.fusion.retry.offline.application.port.out.jms;

public interface PosterOutboundJmsPort {
  void post(String brokerId, String queueName, String payload);
}
