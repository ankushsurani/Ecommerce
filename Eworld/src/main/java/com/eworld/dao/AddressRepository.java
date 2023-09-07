package com.eworld.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Address;
import com.eworld.entities.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

	public List<Address> findByUser(User user);

	@Modifying
	@Transactional
	@Query("DELETE FROM Address a WHERE a.id = :id")
	public void deleteAddressById(String id);
	
	public Address findByUserAndId(User user, String id);
	
	public Optional<Address> findByUserAndAddressTypeAndActive(User user, String addressType, Boolean active);

}
