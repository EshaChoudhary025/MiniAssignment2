package com.miniassignment.sorter;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.miniassignment.entity.User;

@Component
public class NameSortingStrategy implements SortingStrategy {

	private static int calculateNameLength(User user) {
		String fullName = user.getName().getFirst() + " " + user.getName().getLast();
		return fullName.length();
	}

	private static final Comparator<User> ODD_NAME_COMPARATOR = Comparator
			.comparingInt((User user) -> calculateNameLength(user) % 2);

	private static final Comparator<User> EVEN_NAME_COMPARATOR = ODD_NAME_COMPARATOR.reversed();

	@Override
	public void sortUsers(List<User> users) {
		users.sort(ODD_NAME_COMPARATOR); // Default to odd first
	}

	public void sortUsers(List<User> users, String sortOrder) {
		if (sortOrder.equalsIgnoreCase("EVEN")) {
			users.sort(EVEN_NAME_COMPARATOR);
		} else {
			users.sort(ODD_NAME_COMPARATOR);
		}
	}
}
