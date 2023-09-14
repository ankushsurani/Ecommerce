package com.eworld.helper;

import java.util.Random;

public class OTPGenerator {

	public static int generateOTP() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return otp;
	}

}
