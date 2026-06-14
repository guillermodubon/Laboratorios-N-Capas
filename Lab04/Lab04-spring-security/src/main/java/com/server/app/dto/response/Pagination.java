package com.server.app.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record Pagination<T>(
        List<T> data,
        PaginationMeta pagination
) {
    public static <T> Pagination<T> from(Page<T> page) {
        return new Pagination<>(
                page.getContent(),
                new PaginationMeta(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements())
        );
    }
}
