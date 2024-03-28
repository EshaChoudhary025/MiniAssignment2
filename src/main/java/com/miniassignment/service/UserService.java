package com.miniassignment.service;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.Collections;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.miniassignment.dto.GenderDetails;
import com.miniassignment.dto.NationalityDetails;

import com.miniassignment.entity.User;
import com.miniassignment.dto.UserName;
import com.miniassignment.repo.UserRepo;

import com.miniassignment.validator.InputValidator;

import com.miniassignment.validator.ValidatorFactory;
import com.miniassignment.sorter.UserSorter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private ValidatorFactory validatorFactory;
	@Autowired
	private UserSorter userSorter;

	private final ObjectMapper objectMapper;

	private final WebClient api1; // instance of WebClient for API1
	private final WebClient api2; // instance of WebClient for API2
	private final WebClient api3; // instance of WebClient for API3
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	// constructor
	public UserService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
		 // Initialize WebClients and ObjectMapper in the constructor
		this.api1 = webClientBuilder.baseUrl("https://randomuser.me/api").build();
		this.api2 = webClientBuilder.baseUrl("https://api.nationalize.io").build();
		this.api3 = webClientBuilder.baseUrl("https://api.genderize.io").build();
		this.objectMapper = objectMapper;

	}

	// creation of users according to size parameter
	public Flux<User> createUsersForDb(int size) {
		logger.info("Received size: " + size);
		validateNumericInput(size);
		if (size <= 0 || size > 5) {
			throw new IllegalArgumentException("Size must be between 1 and 5 (inclusive)");
		}

		return Flux.range(0, size)// creating a reactive flux
				
				//concatMap is used here to maintain the order of emitted values
				.concatMap(i -> saveUserIntoDb().doOnNext(user -> logger.info("User created: {} on thread: {}",
						user.getName(), Thread.currentThread().getName())))
				// logs a message when all users are created successfully
				.doOnComplete(() -> logger.info("All users created successfully."));
	}

	// method to save user
	@Transactional
	public Mono<User> saveUserIntoDb() {
		// Calls the fetchRandomUserFromApi method
		return fetchRandomUserFromApi().flatMap(randomUser -> {
			String firstName = randomUser.getName().getFirst();
			// calling getNationalityDetails method
			Mono<List<NationalityDetails>> nationalityDetailsMono = getNationalityDetails(firstName)
					.subscribeOn(Schedulers.parallel());
			// calling getGenderDetails method
			Mono<GenderDetails> genderDetailsMono = getGenderDetails(firstName).subscribeOn(Schedulers.parallel());
			// Combining the results of the nationality and gender Mono objects into a
			// single Mono
			return Mono.zip(nationalityDetailsMono, genderDetailsMono).map(tuple -> {
				// Extracting the results from the tuple
				List<NationalityDetails> nationalityDetailsList = tuple.getT1();
				GenderDetails genderDetails = tuple.getT2();

				setVerificationStatus(randomUser, genderDetails, nationalityDetailsList);

				randomUser.setDateCreated(LocalDateTime.now());
				randomUser.setDateModified(LocalDateTime.now());

				logger.info("Before saving, Verification Status: {}", randomUser.getVerificationStatus());

				// Saving random user to the database
				User savedUser = userRepository.save(randomUser);

				
				logger.info("After saving, Verification Status: {}", savedUser.getVerificationStatus());

				logger.info("User saved successfully: {}", savedUser);

				return savedUser;
			});
		}).onErrorResume(e -> handleException(e, "Error saving user"));
	}
 
	//method to fetch users from database
	public List<User> getUsersFromDb(String sortType, String sortOrder, int limit, int offset) {
		try {
			validateNumericInput(offset);
			validateNumericInput(limit);
			validateInput(sortType, "alphabets");
			validateInput(sortOrder, "alphabets");

			//fetching all the users from database
			List<User> allUsers = userRepository.findAll();

			// Use the UserSorter to apply sorting strategy
			userSorter.sortUsers(allUsers, sortType, sortOrder);

			// Check if limit is within the allowed range
			if (limit <= 0 || limit > 5) {
				throw new IllegalArgumentException("Limit must be between 1 and 5 (inclusive)");
			}

			// Apply limit and offset manually
			int startIndex = Math.min(offset, allUsers.size());
			int endIndex = Math.min(offset + limit, allUsers.size());

			return allUsers.subList(startIndex, endIndex);

		} catch (IllegalArgumentException e) {
			// Log exception details
			logger.error("Exception while processing getUsersFromDb: {}", e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		} catch (Exception e) {
			// Log exception details
			logger.error("Exception while processing getUsersFromDb: {}", e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
		}
	}
 
	//method to validate numeric input
	private void validateNumericInput(int numericInput) {
		validateInput(String.valueOf(numericInput), "numeric");
	}

	//method to validate inputs of any type
	private <T> void validateInput(T input, String parameterType) {
		//Retrieves a specific InputValidator based on the parameterType
		InputValidator<T> validator = validatorFactory.getValidator(parameterType);
		if (!validator.validate(input)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid input for parameterType: " + parameterType + ", value: " + input, null);
		}
	}

//count of total users in db
	public long getTotalUsers() {
		return userRepository.count();
	}

//method to fetch  random user from api1
	private Mono<User> fetchRandomUserFromApi() {
		logger.info("Fetching random user...");
  //using webclient to make a get req to the api and retrieve the response body as a mono of string
		return api1.get().retrieve().bodyToMono(String.class).flatMap(response -> {
			try {
				// Parse the JSON response using Jackson ObjectMapper
				JsonNode jsonNode = objectMapper.readTree(response);
				 // Extract information about the user from the API response
				//userNode represents json obj for an user
				JsonNode userNode = jsonNode.path("results").get(0);
                 //extract info about user
				String firstName = userNode.path("name").path("first").asText();
				String lastName = userNode.path("name").path("last").asText();
				String fullName = firstName + " " + lastName;
                // Parse date of birth and format it to a standard string representation
				String dob = userNode.path("dob").path("date").asText();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				LocalDateTime parsedDateTime = formatter.parse(dob, LocalDateTime::from);
				String formattedDob = parsedDateTime.toString();

				int age = userNode.path("dob").path("age").asInt();
				String gender = userNode.path("gender").asText();
				String nationality = userNode.path("nat").asText();

				logger.info(
						"Fetched Data From Random User API - Name: {}, DOB: {}, Age: {}, Gender: {}, Nationality: {}",
						fullName, dob, age, gender, nationality);
             // Create a User object with the extracted information
				User user = new User();
				user.setName(new UserName(firstName, lastName));
				user.setDob(formattedDob);
				user.setAge(age);
				user.setGender(gender);
				user.setNationality(nationality);
           // Return a Mono containing the fetched User object
				return Mono.just(user);
			} catch (JsonProcessingException e) {
				return Mono.error(new RuntimeException("Error in JSON Processing", e));
			}
		}).onErrorResume(e -> {
			//if the error is an instance of WebClientResponseException
			if (e instanceof WebClientResponseException) {
				WebClientException((WebClientResponseException) e);
			} else {
				GenericException(e);
			}
			// Return an empty Mono in case of an error
			return Mono.empty();
		});
	}

	// method to fetch data from api3
	private Mono<GenderDetails> getGenderDetails(String firstName) {

		logger.info("Calling API3 with name: {}", firstName);
        // Using WebClient to make a GET request to API3 with the provided name as a query parameter
		return api3.get().uri("/?name={name}", firstName).retrieve().bodyToMono(GenderDetails.class)//converting response body to mono
				//log on success
				.doOnSuccess(genderInfo -> {
					logger.info("API3 Response: {}", genderInfo);
					//log error
				}).doOnError(error -> {
					logger.error("Error in API3 request", error);
				}).onErrorResume(e -> {
					logger.error("Error handling in API3 request", e);
					return Mono.just(new GenderDetails()); // Return an empty GenderDetails or handle the error as needed
				});
	}

	

	// method to fetch data from api2
	private Mono<List<NationalityDetails>> getNationalityDetails(String firstName) {

		return api2.get().uri("/?name={name}", firstName).retrieve().bodyToMono(String.class).map(nationality -> {
			try {
				// Parse the JSON response from API2 using Jackson ObjectMapper
				JsonNode jsonNode = objectMapper.readTree(nationality);
				// Extract information about the country from the API2 response
				JsonNode nationalityNode = jsonNode.path("country").get(0);
				String countryId = nationalityNode.path("country_id").asText();

				logger.info("Nationality of {} is {}", firstName, countryId);

				// Create a NationalityDetails object with the extracted information
				NationalityDetails nationalityInfo = new NationalityDetails();
				nationalityInfo.setCountryId(countryId);
              // Return a list containing the NationalityDetails object
				return Collections.singletonList(nationalityInfo);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error in JSON Processing", e);
			}
		}).doOnError(error -> {
			logger.error("Error in API2 request", error);
		}).onErrorResume(e -> {
			logger.error("Error handling in API2 request", e);
			return Mono.just(Collections.emptyList()); // Return an empty list 
		});
	}

	// method to set verification status by comparing data of api1 with api2 and
	// api3
	private void setVerificationStatus(User randomUser, GenderDetails genderDetails,
			List<NationalityDetails> nationalityDetailsList) {
		// Handle API3 response
		if (genderDetails != null && genderDetails.getGender() != null) {
			randomUser.setGender(genderDetails.getGender());

			// Check if the gender from API3 matches with API1
			if (randomUser.getGender() != null && genderDetails.getGender().equalsIgnoreCase(randomUser.getGender())) {
				// Check if the nationality from API2 matches with API1
				if (!nationalityDetailsList.isEmpty()) {
					NationalityDetails api2NationalityInfo = nationalityDetailsList.get(0);
					String api2CountryId = api2NationalityInfo.getCountryId();

					if (api2CountryId.equals(randomUser.getNationality())) {
						randomUser.setVerificationStatus("VERIFIED");
					} else {
						randomUser.setVerificationStatus("TO_BE_VERIFIED");
					}
				} else {
					randomUser.setVerificationStatus("TO_BE_VERIFIED");
				}
			} else {
				randomUser.setVerificationStatus("TO_BE_VERIFIED");
			}
		} else {
			// Handle the case when gender information is not available
			// For now, set it to "Unknown Gender"
			randomUser.setGender("Unknown Gender");
			randomUser.setVerificationStatus("TO_BE_VERIFIED");
		}
	}
	// Method to handle generic exceptions
		private void GenericException(Throwable throwable) {
			// Handle other exceptions here
			logger.error("Exception: {}", throwable.getMessage(), throwable);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", throwable);
		}

		private Mono<User> handleException(Throwable throwable, String message) {
			// Log the full stack trace
			logger.error("{}: {}", message, throwable.getMessage(), throwable);

			// Return a ResponseStatusException with a 500 status code
			//Creating a Mono with Mono.error
			return Mono.error(
					new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", throwable));
		}

	private void WebClientException(WebClientResponseException e) {
		// Handle WebClient response exceptions here
		logger.error("WebClient response exception: {} - {}", e.getStatusCode(), e.getLocalizedMessage(), e);
		throw new ResponseStatusException(e.getStatusCode(), e.getLocalizedMessage(), e);
	}

}
