package main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityValue {
	private int securityId;
	private String name;
	private String symbol;
	private String category;
	private String currency;
	private double value;
}
