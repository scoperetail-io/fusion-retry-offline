package com.scoperetail.fusion.retry.offline.adapter.in.messaging.jms;

/*-
 * *****
 * fusion-retry-offline
 * -----
 * Copyright (C) 2018 - 2021 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.scoperetail.fusion.config.FusionConfig;
import com.scoperetail.fusion.core.adapter.in.messaging.jms.AbstractMessageListener;
import com.scoperetail.fusion.messaging.adapter.out.messaging.jms.MessageRouterReceiver;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OfflineRetryUseCase;

@Component
public class OfflineRetryMessageHandler extends AbstractMessageListener {
  private final OfflineRetryUseCase offlineRetryUseCase;
  private final String usecase;

  public OfflineRetryMessageHandler(
      @Value("${usecase}") final String usecase,
      final FusionConfig fusionConfig,
      final MessageRouterReceiver messageRouterReceiver,
      final OfflineRetryUseCase offlineRetryUseCase) {
    super(usecase, null, messageRouterReceiver, fusionConfig);
    this.usecase = usecase;
    this.offlineRetryUseCase = offlineRetryUseCase;
  }

  @Override
  public void handleValidationFailure(final String message) throws Exception {
    offlineRetryUseCase.doValidationFailure(usecase, message);
  }

  @Override
  public void handleMessage(final Object event) throws Exception {
    offlineRetryUseCase.retryOffline(event);
  }

  @Override
  public void handleFailure(final String message) {
    //offlineRetryUseCase.shutDownApplication();
  }
}
