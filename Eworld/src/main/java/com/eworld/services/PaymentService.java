package com.eworld.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

}
