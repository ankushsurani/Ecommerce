package com.eworld.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Address {

	@Id
	@GeneratedValue(generator = "custom-uuid-generator")
	@GenericGenerator(name = "custom-uuid-generator", strategy = "com.eworld.helper.CustomUUIDGenerator")
	private String id;

	@Size(min = 3, max = 50)
	private String name;

	@Size(min = 10, max = 10)
	private String mobilenum;

	@NotBlank
	private String pincode;

	@Size(min = 5, max = 500)
	private String fullAddress;

	@NotBlank
	private String localityOrTown;

	@NotBlank
	private String state;

	@NotBlank
	private String city;

	private String addressType;

	private Boolean active = true;

	@ManyToOne
	@JsonIgnore
	private User user;

	public Address() {
		super();
	}

	public Address(String id, String name, String mobilenum, String pincode, String fullAddress, String localityOrTown,
			String state, String city, String addressType, Boolean active, User user) {
		super();
		this.id = id;
		this.name = name;
		this.mobilenum = mobilenum;
		this.pincode = pincode;
		this.fullAddress = fullAddress;
		this.state = state;
		this.city = city;
		this.addressType = addressType;
		this.active = active;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobilenum() {
		return mobilenum;
	}

	public void setMobilenum(String mobilenum) {
		this.mobilenum = mobilenum;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getLocalityOrTown() {
		return localityOrTown;
	}

	public void setLocalityOrTown(String localityOrTown) {
		this.localityOrTown = localityOrTown;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", name=" + name + ", mobilenum=" + mobilenum + ", pincode=" + pincode
				+ ", fullAddress=" + fullAddress + ", localityOrTown=" + localityOrTown + ", state=" + state + ", city="
				+ city + ", addressType=" + addressType + ", active=" + active + ", user=" + user + "]";
	}

}