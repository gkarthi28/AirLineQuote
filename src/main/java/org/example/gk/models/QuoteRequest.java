package org.example.gk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {
	private double fareAmount;
	private String currency;
	private String cabinClass;
	private String customerTier;
	private String promoCode;
}
