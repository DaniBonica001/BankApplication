package com.devsu.hackerearth.backend.client.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

	private Long id;
	@NotBlank
	private String dni;
	@NotBlank
	private String name;
	@NotBlank
	private String password;
	@NotBlank
	private String gender;
	@Min(0)
	private int age;
	@NotBlank
	private String address;
	@NotBlank
	private String phone;
	@NotNull
	private Boolean isActive;
}
