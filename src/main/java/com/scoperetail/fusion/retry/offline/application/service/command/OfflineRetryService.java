package com.scoperetail.fusion.retry.offline.application.service.command;

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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import com.scoperetail.fusion.core.adapter.out.web.http.PosterOutboundHttpAdapter;
import com.scoperetail.fusion.core.application.port.in.command.create.PosterUseCase;
import com.scoperetail.fusion.core.common.HttpRequest;
import com.scoperetail.fusion.core.common.HttpRequestWrapper;
import com.scoperetail.fusion.core.common.JsonUtils;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OfflineRetryUseCase;
import com.scoperetail.fusion.retry.offline.common.helper.ApplicationShutdownHelper;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@AllArgsConstructor
@Slf4j
public class OfflineRetryService implements OfflineRetryUseCase {

  private PosterOutboundHttpAdapter posterOutboundHttpAdapter;
  private ApplicationShutdownHelper applicationShutdownHelper;
  private PosterUseCase posterUseCase;

  @Override
  public void doValidationFailure(final String event, final String message) {
    try {
      posterUseCase.post(event, message, false);
    } catch (final Exception e) {
      log.error(
          "An exception occured while executing doValidationFailure() for Usecase: {},  Exception: {}",
          event,
          e);
    }
  }

  @Override
  public void retryOffline(final Object message) throws Exception {
    final HttpRequestWrapper httpRequestWrapper = unmarshal(message);
    final HttpRequest httpRequest = httpRequestWrapper.getHttpRequest();
    httpRequestWrapper
        .getRetryCustomizers()
        .forEach(customizer -> applyCustomization(httpRequest, customizer));
    posterOutboundHttpAdapter.post(httpRequest);
  }

  @Override
  public void shutDownApplication() {
    applicationShutdownHelper.shutDownApplication();
  }

  private HttpRequestWrapper unmarshal(final Object message) throws IOException {
    return JsonUtils.unmarshal(
        Optional.ofNullable(message.toString()), HttpRequestWrapper.class.getCanonicalName());
  }

  private void applyCustomization(final HttpRequest httpRequest, final String customizerClassName) {
    try {
      final Class customizerClazz = Class.forName(customizerClassName);
      final Method method =
          customizerClazz.getDeclaredMethod("applyCustomization", HttpRequest.class);
      method.invoke(null, httpRequest);
    } catch (final Exception e) {
      log.error(
          "Skipping customization. Unable to load configured customizer: {}", customizerClassName);
    }
  }
}
