package com.miniassignment.sorter;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.miniassignment.entity.User;

@Component
public class AgeSortingStrategy implements SortingStrategy {

    private static final Comparator<User> EVEN_AGE_COMPARATOR = Comparator.comparingInt(user -> user.getAge() % 2);

    private static final Comparator<User> ODD_AGE_COMPARATOR = EVEN_AGE_COMPARATOR.reversed();

    @Override
    public void sortUsers(List<User> users) {
        users.sort(ODD_AGE_COMPARATOR); // Default to odd first
    }

    public void sortUsers(List<User> users, String sortOrder) {
        if (sortOrder.equalsIgnoreCase("EVEN")) {
            users.sort(EVEN_AGE_COMPARATOR);
        } else {
            users.sort(ODD_AGE_COMPARATOR);
        }
    }
}
