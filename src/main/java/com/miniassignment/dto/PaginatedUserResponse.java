package com.miniassignment.dto;

import java.util.List;


public class PaginatedUserResponse {

	private List<UserResponse> users;
	private PageInfo pageInfo;

	
	public PaginatedUserResponse(List<UserResponse> users, PageInfo pageInfo) {
		this.users = users;
		this.pageInfo = pageInfo;
	}

	public List<UserResponse> getUsers() {
		return users;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}
}
