package com.miniassignment.dto;

public class NationalityDetails {

	private String countryId;
	private double probability;

	public NationalityDetails() {
		
	}

	public NationalityDetails(String countryId, double probability) {
		this.countryId = countryId;
		this.probability = probability;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "NationalityInfo [countryId=" + countryId + ", probability=" + probability + "]";
	}
}
