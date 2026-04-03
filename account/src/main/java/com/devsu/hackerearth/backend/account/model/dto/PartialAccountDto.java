package com.devsu.hackerearth.backend.account.model.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialAccountDto {

	@NotNull
	private Boolean isActive;
}
