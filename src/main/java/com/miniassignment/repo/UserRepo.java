package com.miniassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miniassignment.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepo extends JpaRepository<User, Long> {
	
	
    
}

