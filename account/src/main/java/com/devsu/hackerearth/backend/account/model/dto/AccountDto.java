package com.devsu.hackerearth.backend.account.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

	private Long id;
	@NotBlank
	private String number;
	@NotBlank
	private String type;
	@Min(0)
	private double initialAmount;
	@NotNull
	private Boolean isActive;
	@NotNull
	private Long clientId;
}
