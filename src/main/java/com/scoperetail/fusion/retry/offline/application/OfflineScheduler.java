package com.scoperetail.fusion.retry.offline.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scoperetail.fusion.messaging.config.FusionConfig;
import com.scoperetail.fusion.messaging.config.RetryPolicy;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OrderDropUseCase;
import com.scoperetail.fusion.retry.offline.common.JsonUtils;
import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;
import com.scoperetail.fusion.retry.persistence.repository.RetryLogRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OfflineScheduler {
	private static final String INCOMPLETE = "I";

	private final RetryLogRepository retryLogRepository;
	private final FusionConfig fusionConfig;
	private final OrderDropUseCase orderDropUseCase;

	public OfflineScheduler(RetryLogRepository retryLogRepository, FusionConfig fusionConfig,
			OrderDropUseCase orderDropUseCase) {
		super();
		this.retryLogRepository = retryLogRepository;
		this.fusionConfig = fusionConfig;
		this.orderDropUseCase = orderDropUseCase;
	}

	@Scheduled(fixedDelayString = "#{${fusion.retryPolicies[1].backoffMS}}")
	public void processIncompleteAttempts() throws Exception {
		final Optional<RetryPolicy> retryPolicyOpt = fusionConfig.getRetryPolicies().stream()
				.filter(p -> p.getPolicyType().equals(RetryPolicy.PolicyType.OFFLINE)).findFirst();

		if (retryPolicyOpt.isPresent()) {
			RetryPolicy retryPolicy = retryPolicyOpt.get();
			int maxAttempts = retryPolicy.getMaxAttempt();

			List<RetryLogEntity> retryLogs = retryLogRepository.findAllNotComplete(maxAttempts, INCOMPLETE);

			for (RetryLogEntity itemRetryLog : retryLogs) {

				List<Map<String, Object>> eventMap = JsonUtils.unmarshal(Optional.ofNullable(itemRetryLog.getPayload()),
						Optional.ofNullable(new TypeReference<List<Map<String, Object>>>() {
						}));

				String eventName = eventMap.stream().findFirst().get().get("eventName").toString();

				orderDropUseCase.dropOrder(eventName, itemRetryLog);

			}
		}
	}

}
