package com.FleetPulse.tracking.security;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantWebSocketInterceptor implements ChannelInterceptor {
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor == null || accessor.getCommand() != StompCommand.SUBSCRIBE) return message;
		String destination = accessor.getDestination();
		if (destination == null) throw new AccessDeniedException("Subscription destination is required");
		String[] parts = destination.split("/");
		if (parts.length != 6 || !parts[1].equals("topic") || !parts[2].equals("tenants") || !parts[4].equals("shipments")) {
			throw new AccessDeniedException("Subscription destination is not allowed");
		}
		if (!(accessor.getUser() instanceof JwtAuthenticationToken authentication)) throw new AccessDeniedException("JWT identity is required");
		try {
			UUID requestedTenant = UUID.fromString(parts[3]);
			UUID claimTenant = UUID.fromString(authentication.getToken().getClaimAsString("tenant_id"));
			UUID.fromString(parts[5]);
			if (!requestedTenant.equals(claimTenant)) throw new AccessDeniedException("Cross-tenant subscription is not allowed");
		} catch (IllegalArgumentException exception) {
			throw new AccessDeniedException("Invalid subscription destination");
		}
		return message;
	}
}
