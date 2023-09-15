package com.clepbo.hospital_management_system.patient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientAddressResponseDTO {
    private Long id;
    private String address;
    private String nextOfKin;
    private String nextOfKinPhoneNumber;
    private String nextOfKinAddress;
    private String nextOfKinRelationship;
}
