package com.miniassignment.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.miniassignment.entity.User;
import com.miniassignment.service.UserService;

import reactor.core.publisher.Flux;

import com.miniassignment.dto.UserName;
import com.miniassignment.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.hasSize;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@Test
    void testCreateUsersEndpoint() throws Exception {
        // Mocking the service response
    	//it will return a Flux of sample users created by createSampleUser()
        when(userService.createUsersForDb(anyInt()))
                .thenReturn(Flux.fromIterable(createSampleUser()));

        // Perform the POST request to the /users 
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // for debugging purpose
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_TYPE)); // Check that Content-Type is not set

        // Extract the content from the response asynchronously
        MvcResult result = resultActions.andReturn();
        //performing asynchronous dispatch to continue processing in a separate thread
        mockMvc.perform(asyncDispatch(result))
                .andExpect(jsonPath("$[0].name").value("Franciane Ramos"))
                .andExpect(jsonPath("$[0].gender").value("female"))
                .andExpect(jsonPath("$[0].age").value(75))
                .andExpect(jsonPath("$[0].nationality").value("BR"))
                .andExpect(jsonPath("$[0].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$[0].dob").value("1948-06-23T17:24:07.090"));
    }

	// Helper method to create a sample user for testing
	private List<User> createSampleUser() {
		UserName userName = new UserName("Franciane", "Ramos");

		User user = new User();
		user.setName(userName);
		user.setGender("female");
		user.setAge(75);
		user.setNationality("BR");
		user.setVerificationStatus("VERIFIED");
		user.setDob("1948-06-23T17:24:07.090");

		return Collections.singletonList(user);
	}

	@Test
    void testCreateTwoUsersEndpoint() throws Exception {
        // Mocking the service response for two users
        when(userService.createUsersForDb(eq(2)))
                .thenReturn(Flux.fromIterable(createSampleUsers(2)));

        // Perform the POST request and verify the response for two users
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // Print the response to the console
                .andExpect(request().asyncStarted())
                .andReturn();

        // Continue processing in a separate thread
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated()) // Update to expect 201 status code
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Calvin"))
                .andExpect(jsonPath("$[0].gender").value("Male"))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[0].nationality").value("US"))
                .andExpect(jsonPath("$[0].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$[0].dob").value("1997-08-10T08:15:30.000"))
                .andExpect(jsonPath("$[1].name").value("Alicia Smith"))
                .andExpect(jsonPath("$[1].gender").value("Female"))
                .andExpect(jsonPath("$[1].age").value(32))
                .andExpect(jsonPath("$[1].nationality").value("CA"))
                .andExpect(jsonPath("$[1].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$[1].dob").value("1992-05-22T11:45:00.000"));
    }

	@Test
	void testCreateUsersEndpointWithInvalidSize() throws Exception {
		// Perform the POST request with an invalid size (greater than 5)
		mockMvc.perform(MockMvcRequestBuilders.post("/users").param("size", "10") // Set size greater than 5
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andDo(print()) // for
																											// debugging
																											// purpose
				.andExpect(status().isInternalServerError()) // Expect Internal Server Error status (500)
				.andExpect(jsonPath("$.message").value("Internal Server Error")) // Update the expected message
																					// accordingly
				.andExpect(jsonPath("$.code").value(500)).andExpect(jsonPath("$.timestamp").exists());

		// Verify that userService.saveUserIntoDb was NOT called
		verify(userService, never()).saveUserIntoDb();
	}

	// Helper method to create sample users for testing
	private List<User> createSampleUsers(int count) {
		List<User> users = new ArrayList<>();

		// User 1
		UserName userName1 = new UserName("John", "Calvin");
		User user1 = new User();
		user1.setName(userName1);
		user1.setGender("Male");
		user1.setAge(20);
		user1.setNationality("US");
		user1.setVerificationStatus("VERIFIED");
		user1.setDob("1997-08-10T08:15:30.000");
		users.add(user1);

		// User 2
		UserName userName2 = new UserName("Alicia", "Smith");
		User user2 = new User();
		user2.setName(userName2);
		user2.setGender("Female");
		user2.setAge(32);
		user2.setNationality("CA");
		user2.setVerificationStatus("VERIFIED");
		user2.setDob("1992-05-22T11:45:00.000");
		users.add(user2);

		return users;
	}
}
