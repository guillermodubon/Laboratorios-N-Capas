package com.guillermodubon.lab03nuevocensohyrule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private Integer status;
    private LocalDateTime timestamp;
    private String path;
    private List<String> errors;
}
