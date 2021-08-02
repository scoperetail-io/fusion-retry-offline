package com.scoperetail.fusion.retry.offline.application.service.command;

import static com.scoperetail.fusion.retry.offline.common.Event.OfflineRetry;
import java.io.IOException;
import java.util.Optional;
import com.scoperetail.fusion.core.adapter.out.web.http.PosterOutboundHttpAdapter;
import com.scoperetail.fusion.core.application.port.in.command.create.PosterUseCase;
import com.scoperetail.fusion.core.common.HttpRequest;
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
  public void doValidationFailure(final String message) {
    try {
      posterUseCase.post(OfflineRetry.name(), message, false);
    } catch (final Exception e) {
      log.error(
          "An exception occured while executing doValidationFailure() for Usecase: {},  Exception: {}",
          OfflineRetry.name(),
          e);
    }
  }

  @Override
  public void retryOffline(final Object message) throws Exception {
    final HttpRequest httpRequest = unmarshal(message);
    posterOutboundHttpAdapter.post(httpRequest);
  }

  @Override
  public void shutDownApplication() {
    applicationShutdownHelper.shutDownApplication();
  }

  private HttpRequest unmarshal(final Object message) throws IOException {
    return JsonUtils.unmarshal(
        Optional.ofNullable(message.toString()), HttpRequest.class.getCanonicalName());
  }
}
