package com.eworld.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
	
	public List<Payment> findByRzporderId(String rzporderId);

}
