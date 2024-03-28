package com.miniassignment.test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.miniassignment.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniassignment.dto.UserName;

import com.miniassignment.service.UserService;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerGetTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	// method to test get users end point
	@Test
    public void testGetUsersEndpoint() throws Exception {
    	//sets up the mock behavior for the getTotalUsers method
        // Mocking the service response
        when(userService.getTotalUsers()).thenReturn(90L);

        // Mocking the service response for getUsersFromDb
        when(userService.getUsersFromDb(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(createSampleUserList());

        // Perform the GET request
        mockMvc.perform(get("/users")
                .param("sortType", "Age")
                .param("sortOrder", "Even")
                .param("limit", "5")
                .param("offset", "0")
                //request content type and accepted media type 
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        //assertions on the expected properties of the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(5)))
                .andExpect(jsonPath("$.users[0].name").value("Vildan Aşıkoğlu"))
                .andExpect(jsonPath("$.users[0].gender").value("female"))
                .andExpect(jsonPath("$.users[0].age").value(24))
                .andExpect(jsonPath("$.users[0].nationality").value("TR"))
                .andExpect(jsonPath("$.users[0].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$.users[0].dob").value("1999-03-04T02:44:17.110"))
                .andExpect(jsonPath("$.users[1].name").value("Daniel Rasmussen"))
                .andExpect(jsonPath("$.users[1].gender").value("male"))
                .andExpect(jsonPath("$.users[1].age").value(38))
                .andExpect(jsonPath("$.users[1].nationality").value("DK"))
                .andExpect(jsonPath("$.users[1].verificationStatus").value("TO_BE_VERIFIED"))
                .andExpect(jsonPath("$.users[1].dob").value("1985-05-04T20:25:52.225"))
                .andExpect(jsonPath("$.users[2].name").value("Aapo Lehtinen"))
                .andExpect(jsonPath("$.users[2].gender").value("male"))
                .andExpect(jsonPath("$.users[2].age").value(68))
                .andExpect(jsonPath("$.users[2].nationality").value("FI"))
                .andExpect(jsonPath("$.users[2].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$.users[2].dob").value("1955-05-06T15:14:28.108"))
                .andExpect(jsonPath("$.users[3].name").value("علی محمدخان"))
                .andExpect(jsonPath("$.users[3].gender").value("male"))
                .andExpect(jsonPath("$.users[3].age").value(42))
                .andExpect(jsonPath("$.users[3].nationality").value("IR"))
                .andExpect(jsonPath("$.users[3].verificationStatus").value("VERIFIED"))
                .andExpect(jsonPath("$.users[3].dob").value("1981-09-08T21:00:45.834"))
                .andExpect(jsonPath("$.users[4].name").value("Suraje Novaes"))
                .andExpect(jsonPath("$.users[4].gender").value("Unknown Gender"))
                .andExpect(jsonPath("$.users[4].age").value(78))
                .andExpect(jsonPath("$.users[4].nationality").value("BR"))
                .andExpect(jsonPath("$.users[4].verificationStatus").value("TO_BE_VERIFIED"))
                .andExpect(jsonPath("$.users[4].dob").value("1945-08-23T06:04:53.567"))
                .andExpect(jsonPath("$.pageInfo.hasNextPage").value(true))
                .andExpect(jsonPath("$.pageInfo.hasPreviousPage").value(false))
                .andExpect(jsonPath("$.pageInfo.total").value(90));

        // Verify that the userService.getTotalUsers and userService.getUsersFromDb methods were called
        verify(userService, times(1)).getTotalUsers();
        verify(userService, times(1)).getUsersFromDb("Age", "Even", 5, 0);
    }

	// helper method to create a sample list of users for testing
	private List<User> createSampleUserList() {
		List<User> users = new ArrayList<>();

		users.add(createUser("Vildan", "Aşıkoğlu", "female", 24, "TR", "VERIFIED", "1999-03-04T02:44:17.110"));
		users.add(createUser("Daniel", "Rasmussen", "male", 38, "DK", "TO_BE_VERIFIED", "1985-05-04T20:25:52.225"));
		users.add(createUser("Aapo", "Lehtinen", "male", 68, "FI", "VERIFIED", "1955-05-06T15:14:28.108"));
		users.add(createUser("علی", "محمدخان", "male", 42, "IR", "VERIFIED", "1981-09-08T21:00:45.834"));
		users.add(createUser("Suraje", "Novaes", "Unknown Gender", 78, "BR", "TO_BE_VERIFIED",
				"1945-08-23T06:04:53.567"));

		return users;
	}

//helper method to create user object
	private User createUser(String firstName, String lastName, String gender, int age, String nationality,
			String verificationStatus, String dob) {
		User user = new User();
		user.setName(new UserName(firstName, lastName));
		user.setGender(gender);
		user.setAge(age);
		user.setNationality(nationality);
		user.setVerificationStatus(verificationStatus);
		user.setDob(dob);
		return user;
	}
}
