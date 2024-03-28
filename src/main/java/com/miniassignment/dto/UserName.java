package com.miniassignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserName {

	@JsonProperty("first")

	private String first;

	@JsonProperty("last")

	private String last;

	public UserName(UserName name) {

		this.first = name.getFirst();
		this.last = name.getLast();
	}

	public UserName(String first, String last) {
		this.first = first;
		this.last = last;
	}

	public UserName() {
		// TODO Auto-generated constructor stub
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	
}
