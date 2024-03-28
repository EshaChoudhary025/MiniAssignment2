package com.miniassignment.validator;

import org.springframework.stereotype.Component;

@Component
public class NumericValidator implements InputValidator<String> {

	@Override
	public boolean validate(String i) {

		return i.matches("\\d+");
	}
}
