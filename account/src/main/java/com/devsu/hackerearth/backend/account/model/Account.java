package com.devsu.hackerearth.backend.account.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends Base {
	private String number;
	private String type;
	private double initialAmount;
	private boolean isActive;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "current_balance")
	private double currentBalance;
}
