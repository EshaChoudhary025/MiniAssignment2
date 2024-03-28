package com.miniassignment.dto;

public class GenderDetails {

	private int count;
	private String gender;
	private String name;
	private double probability;

	public GenderDetails() {
		
	}

	public GenderDetails(int count, String gender, String name, double probability) {
		this.count = count;
		this.gender = gender;
		this.name = name;
		this.probability = probability;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "GenderInfo [count=" + count + ", gender=" + gender + ", name=" + name + ", probability=" + probability
				+ "]";
	}
}
