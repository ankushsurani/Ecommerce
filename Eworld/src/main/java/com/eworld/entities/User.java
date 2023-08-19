package com.eworld.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.eworld.validation.EditProfileValidation;
import com.eworld.validation.SignupValidation;

@Entity
public class User {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@Size(groups = {SignupValidation.class, EditProfileValidation.class}, min = 3, max = 100, message = "Name must have 3 to 100 characters")
	private String fullName;

	@Column(unique = true)
	@Email(groups = {SignupValidation.class}, regexp = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$")
	private String email;

	@Pattern(groups = {SignupValidation.class}, regexp = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9].*[0-9])(?=.*[^a-zA-Z0-9]).{8,}", message = "Password must have 1 uppercase alphabet, 1 lowercase alphabet, 2 digits and 1 special character. Also the minimum allowed length is 8 characters")
	private String password;

	@Size(groups = {EditProfileValidation.class}, min = 10, max = 10, message = "Mobile Number Must have 10 Characters")
	private String mobilenum;

	private String gender;

	private String profilePic;

	private String role;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

	@Column(columnDefinition = "boolean default false")
	private boolean status;

	private String emailVerification;

	private LocalDateTime creationDateTime;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Address> address = new ArrayList<Address>();

	public User() {
		super();
	}

	public User(String id, String fullName, String email, String password, String mobilenum, String gender,
			String profilePic, String role, Date dob, boolean status) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.mobilenum = mobilenum;
		this.gender = gender;
		this.profilePic = profilePic;
		this.role = role;
		this.dob = dob;
		this.status = status;
	}

	public User(String fullName, String email, String password, String mobilenum, String gender, String profilePic,
			String role, Date dob, boolean status) {
		super();
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.mobilenum = mobilenum;
		this.gender = gender;
		this.profilePic = profilePic;
		this.role = role;
		this.dob = dob;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", fullName=" + fullName + ", email=" + email + ", password=" + password
				+ ", mobilenum=" + mobilenum + ", gender=" + gender + ", profilePic=" + profilePic + ", role=" + role
				+ ", dob=" + dob + ", status=" + status + ", emailVerification=" + emailVerification
				+ ", creationDateTime=" + creationDateTime + ", address=" + address + "]";
	}

}