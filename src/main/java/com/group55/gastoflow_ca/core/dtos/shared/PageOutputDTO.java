package com.group55.gastoflow_ca.core.dtos.shared;

import java.util.List;

public record PageOutputDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

}
