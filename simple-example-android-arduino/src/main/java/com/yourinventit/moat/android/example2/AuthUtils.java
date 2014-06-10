package com.yourinventit.moat.android.example2;

import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthUtils {
	private static final String HASH_ARGORITHM_HMACSHA1 = "HmacSHA1";
	private AuthUtils() {}
	
	public static byte[] hmacsha1(byte[] key, byte[] input) throws GeneralSecurityException {
		SecretKeySpec sk = new SecretKeySpec(key, HASH_ARGORITHM_HMACSHA1);
		Mac mac = Mac.getInstance(HASH_ARGORITHM_HMACSHA1);
		mac.init(sk);
		byte[] ret = mac.doFinal(input);
		
		return ret;
	}
}
