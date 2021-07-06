package com.scoperetail.fusion.retry.offline.application.port.in.command.create;

import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;

public interface PosterUseCase {
	void post(final String event, RetryLogEntity domainEntity, boolean isValid) throws Exception;
}
