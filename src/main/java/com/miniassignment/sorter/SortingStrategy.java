package com.miniassignment.sorter;

import java.util.List;

import com.miniassignment.entity.User;

public interface SortingStrategy {
	void sortUsers(List<User> users);
}
