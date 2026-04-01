package org.acme.plataforma.courses.application.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {}
