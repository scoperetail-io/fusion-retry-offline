package com.scoperetail.fusion.retry.offline.application.service.command;

import static com.scoperetail.fusion.messaging.application.port.in.UsecaseResult.FAILURE;
import static com.scoperetail.fusion.messaging.application.port.in.UsecaseResult.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.scoperetail.fusion.messaging.application.port.in.UsecaseResult;
import com.scoperetail.fusion.messaging.config.Adapter;
import com.scoperetail.fusion.messaging.config.Adapter.TransportType;
import com.scoperetail.fusion.messaging.config.Config;
import com.scoperetail.fusion.messaging.config.FusionConfig;
import com.scoperetail.fusion.messaging.config.UseCaseConfig;
import com.scoperetail.fusion.retry.offline.adapter.out.web.http.PosterOutboundHttpOfflineAdapter;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.PosterUseCase;
import com.scoperetail.fusion.retry.offline.application.port.out.jms.PosterOutboundJmsPort;
import com.scoperetail.fusion.retry.offline.common.JsonUtils;
import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;
import com.scoperetail.fusion.retry.persistence.repository.RetryLogRepository;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@AllArgsConstructor
@Slf4j
class PosterService implements PosterUseCase {

	private static final String COMPLETE = "C";

	private final FusionConfig fusionConfig;
	private final PosterOutboundJmsPort posterOutboundJmsPort;
	private final PosterOutboundHttpOfflineAdapter posterOutboundHttpOfflineAdapter;
	private final RetryLogRepository retryLogRepository;

	@Override
	public void post(final String event, final RetryLogEntity domainEntity, final boolean isValid) throws Exception {
		handleEvent(event, domainEntity, isValid);
	}

	private void handleEvent(final String event, final RetryLogEntity domainEntity, final boolean isValid) throws Exception {
		final Optional<UseCaseConfig> optUseCase = fusionConfig.getUsecases().stream()
				.filter(u -> u.getName().equals(event)).findFirst();

		if (optUseCase.isPresent()) {
			final UseCaseConfig useCase = optUseCase.get();
			final String activeConfig = useCase.getActiveConfig();

			final Optional<Config> optConfig = useCase.getConfigs().stream()
					.filter(c -> activeConfig.equals(c.getName())).findFirst();

			if (optConfig.isPresent()) {
				final Config config = optConfig.get();
				final UsecaseResult usecaseResult = isValid ? SUCCESS : FAILURE;
				final List<Adapter> adapters = config.getAdapters().stream()
						.filter(c -> c.getAdapterType().equals(Adapter.AdapterType.OUTBOUND)
								&& c.getUsecaseResult().equals(usecaseResult))
						.collect(Collectors.toList());

				for (final Adapter adapter : adapters) {
					log.trace("Notifying outbound adapter: {}", adapter);

					final TransportType trasnportType = adapter.getTrasnportType();
					switch (trasnportType) {
					case JMS:
						notifyJms(event, domainEntity, adapter);
						break;
					case REST:
						notifyRest(event, domainEntity, adapter);
						break;
					default:
						log.error("Invalid adapter transport type: {} for adapter: {}", trasnportType, adapter);
					}
				}
			}
		}
	}

	private void notifyJms(final String event, final RetryLogEntity domainEntity, final Adapter adapter) throws Exception {
		String payload = JsonUtils.marshal(Optional.ofNullable(domainEntity.getPayload()));
		
		posterOutboundJmsPort.post(adapter.getBrokerId(), adapter.getQueueName(), payload);
	}

	private void notifyRest(final String event, final RetryLogEntity domainEntity, final Adapter adapter) throws Exception {
			
		
		Map<String, String> httpHeadersMap = JsonUtils.unmarshal(Optional.ofNullable(domainEntity.getHeaders()),
				HashMap.class.getCanonicalName());
		ResponseEntity<String> result = posterOutboundHttpOfflineAdapter.post(domainEntity.getUrl(),
				domainEntity.getMethodType(), domainEntity.getPayload(), httpHeadersMap);

		if (result != null) {
			retryLogRepository.updateStatusByRetryKey(domainEntity.getRetryKey(), COMPLETE);
		} else {
			retryLogRepository.increasingAttempts(domainEntity.getRetryKey());
		}
	}

}
