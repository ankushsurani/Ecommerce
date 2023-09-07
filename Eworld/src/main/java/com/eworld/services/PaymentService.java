package com.eworld.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eworld.dao.PaymentRepository;
import com.eworld.entities.Payment;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;

	public void savePayment(Payment payment) {
		this.paymentRepository.save(payment);
	}

	public List<Payment> findByRzporderId(String rzporderId) {
		return this.paymentRepository.findByRzporderId(rzporderId);
	}

	// This method will run every 2 minutes (120000 milliseconds)
	@Scheduled(fixedRate = 120000)
	public void deletePaymentWithStatusCreated() {
		List<String> paymentIdsWithStatusCreatedAfter1Hr = this.paymentRepository
				.findIdByStatusAndCreatedDateBefore("created", LocalDateTime.now().minusHours(1));
		this.paymentRepository.deletePaymentsByIds(paymentIdsWithStatusCreatedAfter1Hr);
	}

}
