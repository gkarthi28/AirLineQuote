package org.example.gk.enums;

public enum Currency {
	USD,
	EUR,
	JPY,
	GBP,
	CNY;

	public static Currency from(String value) {
		return Currency.valueOf(value.toUpperCase());
	}

	}
