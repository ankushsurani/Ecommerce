package com.eworld.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class User {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String userId;

	private String name;

	private String surname;

	@Column(unique = true)
	@Email(regexp = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$")
	private String email;

	@Pattern(regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9].*[0-9])(?=.*[^a-zA-Z0-9]).{8,}", message = "Password must have 1 uppercase alphabet, 1 lowercase alphabet, 2 digits and 1 special character. Also the minimum allowed length is 8 characters")
	private String password;

	@Size(min = 10, max = 10, message = "Mobile Number must have 10 digits")
	private String mobilenum;

	private String gender;

	private String profilePic;

	private String role;

	@Column(columnDefinition = "boolean default false")
	private boolean status;

	private String emailVerification;

	private LocalDateTime creationDateTime;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Address> address = new ArrayList<Address>();

	public User() {
		super();
	}

	public User(String userId, String name, String surname, String email, String password, String mobilenum,
			String gender, String profilePic, String role, boolean status) {
		super();
		this.userId = userId;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.mobilenum = mobilenum;
		this.gender = gender;
		this.profilePic = profilePic;
		this.role = role;
		this.status = status;
	}

	public User(String name, String surname, String email, String password, String mobilenum, String gender,
			String profilePic, String role, boolean status) {
		super();
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.mobilenum = mobilenum;
		this.gender = gender;
		this.profilePic = profilePic;
		this.role = role;
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobilenum() {
		return mobilenum;
	}

	public void setMobilenum(String mobilenum) {
		this.mobilenum = mobilenum;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getEmailVerification() {
		return emailVerification;
	}

	public void setEmailVerification(String emailVerification) {
		this.emailVerification = emailVerification;
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", surname=" + surname + ", email=" + email + ", password="
				+ password + ", mobilenum=" + mobilenum + ", gender=" + gender + ", profilePic=" + profilePic
				+ ", role=" + role + ", status=" + status + ", emailVerification=" + emailVerification
				+ ", creationDateTime=" + creationDateTime + "]";
	}

}