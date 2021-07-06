package com.scoperetail.fusion.retry.offline.adapter.out.web.http;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface PosterOutboundHttpOfflineAdapter {
	public ResponseEntity<String> post(final String url, final String methodType, final String requestBody,
			final Map<String, String> httpHeaders);
}
