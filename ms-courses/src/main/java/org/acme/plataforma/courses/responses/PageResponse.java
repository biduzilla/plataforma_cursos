package org.acme.plataforma.courses.responses;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int page,
        int pageSize
) {}
