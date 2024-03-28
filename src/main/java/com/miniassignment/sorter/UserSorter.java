package com.miniassignment.sorter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.miniassignment.entity.User;

import java.util.List;
@Component
public class UserSorter {
	//references
	 private final SortingStrategy ageSortingStrategy;

	    private final SortingStrategy nameSortingStrategy;
    
    @Autowired
    public UserSorter(@Qualifier("ageSortingStrategy") SortingStrategy ageSortingStrategy,
                      @Qualifier("nameSortingStrategy") SortingStrategy nameSortingStrategy) {
        this.ageSortingStrategy = ageSortingStrategy;
        this.nameSortingStrategy = nameSortingStrategy;
    }

    public List<User> sortUsers(List<User> users, String sortType, String sortOrder) {
        if ("Name".equalsIgnoreCase(sortType)) {
            NameSortingStrategy nameSortingStrategy = new NameSortingStrategy();
            nameSortingStrategy.sortUsers(users, sortOrder);
        } else if ("Age".equalsIgnoreCase(sortType)) {
            AgeSortingStrategy ageSortingStrategy = new AgeSortingStrategy();
            ageSortingStrategy.sortUsers(users, sortOrder);
        } else {
           
        }

        return users;
    }
}

