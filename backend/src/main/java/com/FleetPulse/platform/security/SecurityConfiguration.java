package com.FleetPulse.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/tenants").hasAuthority("SCOPE_platform:admin")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/tenants/*/status").hasAuthority("SCOPE_platform:admin")
						.requestMatchers(HttpMethod.POST, "/api/v1/tenants/*/members").hasAuthority("SCOPE_tenants:members:write")
						.requestMatchers(HttpMethod.GET, "/api/v1/customers/**").hasAuthority("SCOPE_customers:read")
						.requestMatchers(HttpMethod.POST, "/api/v1/customers").hasAuthority("SCOPE_customers:write")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/customers/**").hasAuthority("SCOPE_customers:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/customers/*/addresses/**").hasAuthority("SCOPE_customers:write")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/customers/*/addresses/**").hasAuthority("SCOPE_customers:write")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/customers/*/addresses/**").hasAuthority("SCOPE_customers:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAuthority("SCOPE_orders:write")
						.requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAuthority("SCOPE_orders:read")
						.requestMatchers(HttpMethod.POST, "/api/v1/shipments/*/cancel").hasAuthority("SCOPE_orders:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/warehouses").hasAuthority("SCOPE_inventory:write")
						.requestMatchers(HttpMethod.GET, "/api/v1/warehouses/**").hasAuthority("SCOPE_inventory:read")
						.requestMatchers(HttpMethod.POST, "/api/v1/warehouses/*/stock-adjustments").hasAuthority("SCOPE_inventory:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/inventory/reservations").hasAuthority("SCOPE_inventory:write")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/inventory/reservations/**").hasAuthority("SCOPE_inventory:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/payments").hasAuthority("SCOPE_payments:write")
						.requestMatchers(HttpMethod.GET, "/api/v1/payments/**").hasAuthority("SCOPE_payments:read")
						.requestMatchers(HttpMethod.GET, "/api/v1/delivery-partners/**").hasAuthority("SCOPE_delivery:read")
						.requestMatchers(HttpMethod.POST, "/api/v1/delivery-partners").hasAuthority("SCOPE_delivery:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/shipments/*/assignments").hasAuthority("SCOPE_delivery:write")
						.requestMatchers(HttpMethod.PATCH, "/api/v1/delivery-assignments/**").hasAuthority("SCOPE_delivery:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/delivery-assignments/**").hasAuthority("SCOPE_delivery:write")
						.requestMatchers(HttpMethod.POST, "/api/v1/tracking/location-updates").hasAuthority("SCOPE_tracking:write")
						.requestMatchers(HttpMethod.GET, "/api/v1/shipments/*/tracking").hasAuthority("SCOPE_tracking:read")
						.anyRequest().authenticated())
				.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
				.build();
	}
}
