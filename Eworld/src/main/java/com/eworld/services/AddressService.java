package com.eworld.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eworld.dao.AddressRepository;
import com.eworld.entities.Address;
import com.eworld.entities.User;

@Service
public class AddressService {

	@Autowired
	private AddressRepository addressRepository;

	public void saveAddress(Address address) {
		this.addressRepository.save(address);
	}

	public List<Address> getAddressByUser(User user) {
		return this.addressRepository.findByUser(user);
	}

	public Address getAddressById(String addressId) {
		return this.addressRepository.findById(addressId).get();
	}

	public void deleteAddress(Address address) {
		this.addressRepository.delete(address);
	}

}
