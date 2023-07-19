package com.clepbo.hospital_management_system.staff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {
    private String status;
    private Object data;
    private String message;

    public CustomResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
