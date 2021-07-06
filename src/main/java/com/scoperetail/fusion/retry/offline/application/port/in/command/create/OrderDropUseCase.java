package com.scoperetail.fusion.retry.offline.application.port.in.command.create;

import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;

public interface OrderDropUseCase {
	void dropOrder(final String event, RetryLogEntity orderDropEventRequest) throws Exception;
}
