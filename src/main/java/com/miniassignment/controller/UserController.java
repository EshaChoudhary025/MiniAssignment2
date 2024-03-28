package com.miniassignment.controller;

import com.miniassignment.dto.PaginatedUserResponse;
import com.miniassignment.dto.PageInfo;
import com.miniassignment.dto.UserResponse;
import com.miniassignment.entity.User;
import com.miniassignment.service.UserService;
import com.miniassignment.validator.InputValidator;
import com.miniassignment.validator.ValidatorFactory;

import reactor.core.publisher.Mono;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService service;

	@Autowired
	private ValidatorFactory validatorFactory;
	// logger for the class
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// method to handle post request
	@PostMapping
	public Mono<ResponseEntity<List<UserResponse>>> createUsersforDb(@RequestParam(name = "size") int size) {

		return service.createUsersForDb(size)
				// Maps each user emitted by the reactive stream to a UserResponse
				.map(this::changeToUserResponse)
				// collect all the user response objects
				.collectList()
				// Maps the List of UserResponse objects to a ResponseEntity
				.map(savedUsers -> new ResponseEntity<>(savedUsers, HttpStatus.CREATED));
	}

	// method to transform User object to UserResponse object
	private UserResponse changeToUserResponse(User user) {
		try {

			String name = user.getFullName();
			String dob = user.getDob();
			int age = user.getAge();
			String gender = user.getGender();
			String nationality = user.getNationality();
			String verificationStatus = user.getVerificationStatus();

			logger.info("Fetched Data From Random User API - Name: {}, DOB: {}, Age: {}, Gender: {}, Nationality: {}",
					name, dob, age, gender, nationality);

			UserResponse u = new UserResponse();
			u.setName(name);
			u.setDob(dob);
			u.setAge(age);
			u.setGender(gender);
			u.setNationality(nationality);
			u.setVerificationStatus(verificationStatus);

			return u;
		} catch (Exception e) {
			throw new RuntimeException("Error in converting API1 response to UserResponse", e);
		}
	}

	// method to handle get requests
	@GetMapping
	public ResponseEntity<PaginatedUserResponse> getUsersFromDb(@RequestParam(required = false) String sortType,
			@RequestParam(required = false) String sortOrder,
			@RequestParam(required = false, defaultValue = "5") int limit,
			@RequestParam(required = false, defaultValue = "0") int offset) {

		validateNumericInput(limit);
		validateNumericInput(offset);

		validatorFactory.getValidator("alphabets").validate(sortType);
		validatorFactory.getValidator("alphabets").validate(sortOrder);

		// Fetch users without pagination for total count
		Long totalUsers = service.getTotalUsers();

		// Fetch paginated users based on the provided parameters
		List<User> users = service.getUsersFromDb(sortType, sortOrder, limit, offset);

		boolean hasNextPage = offset + limit < totalUsers;
		boolean hasPreviousPage = offset > 0;

		PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, totalUsers);

		// Convert users to UserResponse objects
		List<UserResponse> userResponses = users.stream().map(this::changeToUserResponse).collect(Collectors.toList());

		PaginatedUserResponse paginatedUserResponse = new PaginatedUserResponse(userResponses, pageInfo);

		return new ResponseEntity<>(paginatedUserResponse, HttpStatus.OK);
	}

	private void validateNumericInput(int numericInput) {
		validateInput(Integer.toString(numericInput), "numeric");
	}

//generic method that can validate input of any type
	private <T> void validateInput(T input, String parameterType) {
		// retrieves an InputValidator<T> from a validatorFactory based on the specified
		// parameterType
		InputValidator<T> validator = validatorFactory.getValidator(parameterType);
		if (!validator.validate(input)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid input for parameterType: " + parameterType + ", value: " + input, null);
		}
	}

}
