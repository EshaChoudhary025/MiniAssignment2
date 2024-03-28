package com.miniassignment.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.miniassignment.dto.UserName;

@Entity
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;
	@Embedded
	@AttributeOverrides({

			@AttributeOverride(name = "first", column = @Column(name = "name_first")),
			@AttributeOverride(name = "last", column = @Column(name = "name_last")) })

	private UserName name;
	@Column(name = "full_name") 
	private String fullName;
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name = "age")
	private int age;

	@Column(name = "gender")
	private String gender;
	@Column(name = "dob")

	private String dob;

	@Column(name = "nationality")
	private String nationality;

	@Column(name = "verification_status")
	private String verificationStatus;

	@Column(name = "date_created")
	private LocalDateTime dateCreated;

	@Column(name = "date_modified")
	private LocalDateTime dateModified;

	public User() {
		this.dateCreated = LocalDateTime.now();
		this.dateModified = LocalDateTime.now();
	}

	public User(UserName name, int age, String gender, String dob, String nationality) {
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.dob = dob;
		this.nationality = nationality;
		this.dateCreated = LocalDateTime.now();
		this.dateModified = LocalDateTime.now();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonProperty("name")
	public UserName getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(UserName name) {
		if (name == null) {
			this.name = new UserName(); 
		} else {
			this.name = new UserName();
			this.name.setFirst(name.getFirst());
			this.name.setLast(name.getLast());
			this.fullName = name.getFirst() + " " + name.getLast(); 
		}
	}

	@JsonProperty("age")
	public int getAge() {
		return age;
	}

	@JsonProperty("age")
	public void setAge(int age) {
		this.age = age;
	}

	@JsonProperty("gender")
	public String getGender() {
		return gender;
	}

	@JsonProperty("gender")
	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setDob(LocalDate dob) {
//        this.dob = dob.atStartOfDay();
	}

	@JsonProperty("dob")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	@JsonProperty("nationality")
	public String getNationality() {
		return nationality;
	}

	@JsonProperty("nationality")
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(String verificationStatus) {
		this.verificationStatus = (verificationStatus != null) ? verificationStatus : "DEFAULT_VALUE";

	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getDateModified() {
		return dateModified;
	}

	public void setDateModified(LocalDateTime dateModified) {
		this.dateModified = dateModified;
	}

	@Override
	public String toString() {
		return "User {userId=" + userId + ", name=" + name + ", age=" + age + ", gender=" + gender + ", dob=" + dob
				+ ", nationality=" + nationality + ", verificationStatus=" + verificationStatus + ", dateCreated="
				+ dateCreated + ", dateModified=" + dateModified + "}";
	}

	
}
