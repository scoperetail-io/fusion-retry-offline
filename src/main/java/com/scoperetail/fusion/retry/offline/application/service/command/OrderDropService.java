package com.scoperetail.fusion.retry.offline.application.service.command;

import com.scoperetail.fusion.retry.offline.application.port.in.command.create.OrderDropUseCase;
import com.scoperetail.fusion.retry.offline.application.port.in.command.create.PosterUseCase;
import com.scoperetail.fusion.retry.persistence.entity.RetryLogEntity;
import com.scoperetail.fusion.shared.kernel.common.annotation.UseCase;

import lombok.AllArgsConstructor;

@UseCase
@AllArgsConstructor
public class OrderDropService implements OrderDropUseCase {
	private final PosterUseCase posterUseCase;

	@Override
	public void dropOrder(final String event, RetryLogEntity orderDropEventRequest) throws Exception {
		posterUseCase.post(event, orderDropEventRequest, true);
	}
}
