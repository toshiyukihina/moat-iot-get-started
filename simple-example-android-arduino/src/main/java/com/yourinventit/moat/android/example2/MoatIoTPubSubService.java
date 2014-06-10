package com.yourinventit.moat.android.example2;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;

public class MoatIoTPubSubService extends Service {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MoatIoTPubSubService.class);
	
	private static final String HOST = "sandbox-ssmb.inventit.io";
	private static final String PORT = "1883";
	
	// thina's user account.
	private static final String APP_ID = "38627566-7535-4584-b6b4-399db2563f9a";
	private static final String CLIENT_ID = "admin@e03f60e66ba69720b4d4c248f486c4bd58531b1d";
	private static final String CLIENT_SECRET = "597ebfe734727a6a5d2401a741e3015f";
	
	private static final String PACKAGE_ID = "simple-example";
	private static final String MODEL_NAME = "SensingData";
	
	private static final String DEVICE_UUID = "ff808181464b4da2014683f69cbc7e9f"; // NETWORK_ADDRESS::UUID (sandbox db)
	private static final byte[] AUTH_USER_ID = Base64.decode("8UYMIFo4Msl/OxZs0dzqtr2dW1K1P2lAEyL6pu6hYL4=", Base64.DEFAULT); // DEVICE::AUTH_PASSWORD (sandbox db)
	
	MQTT mMqtt;
	FutureConnection mConn;
	
	public class MoatIoTPubSubServiceBinder extends Binder {
		
		MoatIoTPubSubService getService() {
			return MoatIoTPubSubService.this;
		}
		
	}

	private final IBinder mBinder = new MoatIoTPubSubServiceBinder();
	
	private String deviceDomain() {
		return CLIENT_ID.substring(CLIENT_ID.indexOf('@') + 1);
	}
	
	private String userName() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	private String nonceSeed(String userName) {
		return DEVICE_UUID + ":" + userName;
	}
	
	// See: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
	private static String bytesToHex(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	private String password(String userName) {
		try {
			final byte[] key = AUTH_USER_ID;
			final byte[] input = nonceSeed(userName).getBytes("UTF-8");
			LOGGER.info("key: " + bytesToHex(key));
			LOGGER.info("input: " + bytesToHex(input));
			return bytesToHex(AuthUtils.hmacsha1(key, input));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String topic() {
		final StringBuffer sb = new StringBuffer();
		sb.append("/");
		sb.append(APP_ID);
		sb.append("/");
		sb.append(PACKAGE_ID);
		sb.append("/");
		sb.append(MODEL_NAME);
		sb.append("/");
		sb.append(deviceDomain());
		sb.append("/");
		sb.append(DEVICE_UUID);
		return sb.toString();
	}
	
	@Override
	public void onCreate() {
		LOGGER.info("onCreate()");
		super.onCreate();
		
		String userName = userName() + "?c=Raw";
		
		// For debug.
		LOGGER.info("userName: " + userName);
		LOGGER.info("Nonce Seed: " + nonceSeed(userName));
		LOGGER.info("Password: " + password(userName));
		LOGGER.info("Topic: " + topic());
		LOGGER.info("Device Domain: " + deviceDomain());
		
		mMqtt = new MQTT();
		mMqtt.setReconnectAttemptsMax(0);
		try {
			mMqtt.setHost("tcp://" + HOST + ":" + PORT);
			mMqtt.setClientId("dev:" + DEVICE_UUID);
			mMqtt.setUserName(userName);
			mMqtt.setPassword(password(userName));
			mConn = doConnect(mMqtt);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		LOGGER.info("onDestroy()");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		LOGGER.info("onBind()");
		return mBinder;
	}

	private FutureConnection doConnect(MQTT mqtt) {
		final FutureConnection c = mqtt.futureConnection();
		try {
			final Future<Void> f = c.connect();
			f.await(5000, TimeUnit.MILLISECONDS); // TOOD
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Future<Void> f = c.disconnect();
				f.await(1000, TimeUnit.MILLISECONDS); // TODO
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			
			LOGGER.warn("Connection not establised!");
			
			return null;
		}
		
		LOGGER.info("Connection established !!!");

		return c;
	}
	
	private void doPublish(String payload) {
		try {
			Future<Void> f = mConn.publish(topic(), payload.getBytes(), QoS.AT_MOST_ONCE, false);
			f.await(100, TimeUnit.MILLISECONDS); // TODO
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}
	}
	
	public void publish(String payload) {
		//LOGGER.debug("[PUB] " + payload);
		doPublish(payload);
	}

}
