package com.eworld.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Address;
import com.eworld.entities.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

	public List<Address> findByUser(User user);
    
}
