package com.centimo.api.ports.driven;

public interface PasswordPort {

	String encode(String raw);

	boolean matches(String raw, String encoded);
}
