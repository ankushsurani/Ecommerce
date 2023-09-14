package com.eworld.services;

import java.util.List;

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

	public Address getAddressByUserAndId(User user, String addressId) {
		return this.addressRepository.findByUserAndId(user, addressId);
	}

	public void deleteAddress(String addressId) {
		this.addressRepository.deleteAddressById(addressId);
	}

	public void changeAddressStatus(User user, String addressType, Boolean active) {
		Address address = this.addressRepository.findByUserAndAddressTypeAndActive(user, addressType, active)
				.orElse(null);
		if (address != null) {
			address.setAddressType("OTHER");
			saveAddress(address);
		}
	}

}
