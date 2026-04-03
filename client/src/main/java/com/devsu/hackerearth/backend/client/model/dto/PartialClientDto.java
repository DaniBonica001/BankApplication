package com.devsu.hackerearth.backend.client.model.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialClientDto {

    @NotNull
    private Boolean isActive;
}
