package com.centimo.api.ports.driven;

public interface JwtPort {

	String generateSessionToken(String username);

	String generatePreAuthToken(String username);

	String validateSessionToken(String token);

	String validatePreAuthToken(String token);
}
