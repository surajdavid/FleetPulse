package com.FleetPulse.tracking.config;

import com.FleetPulse.tracking.security.TenantWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class TrackingWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final TenantWebSocketInterceptor interceptor;
	private final String allowedOrigin;
	public TrackingWebSocketConfiguration(TenantWebSocketInterceptor interceptor,
			@Value("${fleetpulse.websocket.allowed-origin:http://localhost:5173}") String allowedOrigin) {
		this.interceptor = interceptor; this.allowedOrigin = allowedOrigin;
	}
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins(allowedOrigin);
	}
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(interceptor);
	}
}
