package com.guillermodubon.lab03nuevocensohyrule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {
    private String message;
    private Integer status;
    private LocalDateTime timestamp;
    private String path;
    private T data;
}
