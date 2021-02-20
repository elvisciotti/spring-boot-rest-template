package io.github.elvisciotti.auth;

public class SecurityConstants {
    public static final String KEY = "replaceme with random string";

    public static final String HEADER_NAME = "Authorization";

    public static final String RESPONSE_FIELD_JWT = "token";

    public static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7;
}
