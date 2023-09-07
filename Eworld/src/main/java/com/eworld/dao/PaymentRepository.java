package com.eworld.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eworld.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

	public List<Payment> findByRzporderId(String rzporderId);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM Payment p WHERE p.id IN :paymentIds")
    public void deletePaymentsByIds(@Param("paymentIds") List<String> paymentIds);
	
	@Query("SELECT p.id FROM Payment p WHERE p.status = :status AND p.createdDate < :dateTime")
	public List<String> findIdByStatusAndCreatedDateBefore(String status, LocalDateTime dateTime);

}
