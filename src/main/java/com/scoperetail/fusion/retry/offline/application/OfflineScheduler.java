package com.scoperetail.fusion.retry.offline.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.scoperetail.fusion.core.adapter.out.web.http.PosterOutboundHttpOfflineAdapter;
import com.scoperetail.fusion.core.common.JsonUtils;
import com.scoperetail.fusion.messaging.config.FusionConfig;
import com.scoperetail.fusion.messaging.config.RetryPolicy;
import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;
import com.scoperetail.fusion.retry.persistence.repository.RetryLogRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OfflineScheduler {
	private static final String COMPLETE = "C";
	private static final String INCOMPLETE = "I";

	private RetryLogRepository retryLogRepository;
	private PosterOutboundHttpOfflineAdapter posterOutboundHttpOfflineAdapter;
	private FusionConfig fusionConfig;

	public OfflineScheduler(RetryLogRepository retryLogRepository,
			PosterOutboundHttpOfflineAdapter posterOutboundHttpOfflineAdapter, FusionConfig fusionConfig) {
		super();
		this.retryLogRepository = retryLogRepository;
		this.posterOutboundHttpOfflineAdapter = posterOutboundHttpOfflineAdapter;
		this.fusionConfig = fusionConfig;
	}

	@Scheduled(fixedDelayString = "#{${fusion.retryPolicies[1].backoffMS}}")
	public void processIncompleteAttempts() throws IOException {
		final Optional<RetryPolicy> retryPolicyOpt = fusionConfig.getRetryPolicies().stream()
				.filter(p -> p.getPolicyType().equals(RetryPolicy.PolicyType.OFFLINE)).findFirst();

		if (retryPolicyOpt.isPresent()) {
			RetryPolicy retryPolicy = retryPolicyOpt.get();
			int maxAttempts = retryPolicy.getMaxAttempt();

			List<RetryLogEntity> retryLogs = retryLogRepository.findAllNotComplete(maxAttempts, INCOMPLETE);

			for (RetryLogEntity itemRetryLog : retryLogs) {

				Map<String, String> headers = JsonUtils.unmarshal(Optional.ofNullable(itemRetryLog.getHeaders()),
						HashMap.class.getCanonicalName());

				ResponseEntity<String> response = posterOutboundHttpOfflineAdapter.post(itemRetryLog.getUrl(),
						itemRetryLog.getMethodType(), itemRetryLog.getPayload(), headers);

				if (response != null) {
					retryLogRepository.updateStatusByRetryKey(itemRetryLog.getRetryKey(), COMPLETE);
					log.trace("REST request sent to URL: {} and Response received is: {}", itemRetryLog.getUrl(),
							response);
				} else {
					retryLogRepository.increasingAttempts(itemRetryLog.getRetryKey());
					log.error("On recover after post failed. message: {}", itemRetryLog.getPayload());
				}
			}
		}
	}

}
