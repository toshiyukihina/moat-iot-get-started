/*
 * Copyright (C) 2013 InventIt Inc.
 * 
 * See https://github.com/inventit/moat-iot-get-started
 */
package com.yourinventit.moat.android.example2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.yourinventit.dmc.api.moat.ContextFactory;
import com.yourinventit.dmc.api.moat.Moat;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
public class SampleApplication extends Activity implements
		SerialInputOutputManager.Listener {

	/**
	 * {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SampleApplication.class);

	/**
	 * Baud rate for ZigBee UART
	 */
	public static final int BAUD_RATE = 19200;

	/**
	 * Threshold to detect if the button is kept pressed.
	 */
	static final long PRESS_THRESHOLD_MS = 300;

	/**
	 * The uid value for the connected zigbee device object.
	 */
	static final String ZIGBEE_DEVICE_UID = "8251370d-caeb-4b5b-9510-de17515b48d4";

	/**
	 * {@link ContextFactory}
	 */
	private static ContextFactory contextFactory;

	/**
	 * {@link ZigBeeDeviceModelMapper}
	 */
	private static ZigBeeDeviceModelMapper zigBeeDeviceModelMapper;

	/**
	 * {@link Moat}
	 */
	private static Moat moat;

	/**
	 * URN prefix
	 */
	private static String urnPrefix;

	/**
	 * {@link UsbManager}
	 */
	private UsbManager usbManager;

	/**
	 * {@link UsbSerialDriver}
	 */
	private UsbSerialDriver usbSerialDriver;

	/**
	 * {@link SerialInputOutputManager}
	 */
	private SerialInputOutputManager serialInputOutputManager;

	/**
	 * The incoming data buffer
	 */
	private ByteArrayOutputStream in;

	/**
	 * {@link ExecutorService}
	 */
	private final ExecutorService serialInputOutputExecutor = Executors
			.newSingleThreadExecutor();

	/**
	 * {@link ExecutorService}
	 */
	private final ScheduledExecutorService clickEventMonitorExecutor = Executors
			.newScheduledThreadPool(1);

	/**
	 * {@link ScrollView}
	 */
	private ScrollView scrollView;

	/**
	 * {@link TextView}
	 */
	private TextView textView;

	/**
	 * (static)
	 * 
	 * @return the contextFactory
	 */
	static ContextFactory getContextFactory() {
		return contextFactory;
	}

	/**
	 * (static)
	 * 
	 * @param zigBeeDeviceModelMapper
	 *            the zigBeeDeviceModelMapper to set
	 */
	static void setZigBeeDeviceModelMapper(
			ZigBeeDeviceModelMapper zigBeeDeviceModelMapper) {
		SampleApplication.zigBeeDeviceModelMapper = zigBeeDeviceModelMapper;
	}

	/**
	 * (static)
	 * 
	 * @return the zigBeeDeviceModelMapper
	 */
	static ZigBeeDeviceModelMapper getZigBeeDeviceModelMapper() {
		return zigBeeDeviceModelMapper;
	}

	/**
	 * (static)
	 * 
	 * @param moat
	 *            the moat to set
	 */
	static void setMoat(Moat moat) {
		SampleApplication.moat = moat;
	}

	/**
	 * (static)
	 * 
	 * @return the moat
	 */
	static Moat getMoat() {
		return moat;
	}

	/**
	 * (static)
	 * 
	 * @param urnPrefix
	 *            the urnPrefix to set
	 */
	static void setUrnPrefix(String urnPrefix) {
		SampleApplication.urnPrefix = urnPrefix;
	}

	/**
	 * (static)
	 * 
	 * @return the urnPrefix
	 */
	static String getUrnPrefix() {
		return urnPrefix;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Widgets
		setContentView(R.layout.simple);
		scrollView = (ScrollView) findViewById(R.id.scroll_view);
		textView = (TextView) findViewById(R.id.scroll_content);

		// ContextFactory
		contextFactory = new AndroidContextFactory(this);
		// Starting the MoatIoTService on this application starting up.
		startService(new Intent(this, MoatIoTService.class));

		// System USB service
		this.usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		// Timer task
		clickEventMonitorExecutor.scheduleAtFixedRate(new Runnable() {

			/**
			 * Monitors if the button is pressed longer then PRESS_THRESHOLD_MS.
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				try {
					final ZigBeeDevice zigBeeDevice = getZigBeeDeviceModelMapper()
							.findByUid(ZIGBEE_DEVICE_UID);
					if (zigBeeDevice == null) {
						return;
					}
					if (zigBeeDevice.isClicked()) {
						final long now = System.currentTimeMillis();
						if (now - zigBeeDevice.getLastClicked() > PRESS_THRESHOLD_MS) {
							LOGGER.info("[clickEventMonitorExecutor] Button state changed => clicked:false");
							// The button is NOT pressed.
							zigBeeDevice.setClicked(false);
							getMoat().sendNotification(
									MoatIoTService.getMoatUrn(getUrnPrefix(),
											"ShakeEvent", "1.0"), null,
									new Object[] { zigBeeDevice });
						}
					}
				} catch (IllegalStateException ignored) {
					// com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper.getConnectionSource
					// may throw IllegalStateException
				}
			}
		}, 0, 400, TimeUnit.MICROSECONDS);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		super.onPause();
		stopSerialInputOutputManager();
		closeUsbSerialDriver();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		if (inquireUsbSerialDriver()) {
			textView.setText("USB Serial Device is detected => { VID:"
					+ usbSerialDriver.getDevice().getVendorId() + ", PID:"
					+ usbSerialDriver.getDevice().getProductId() + "}\n");
		} else {
			textView.setText("USB Serial Device is missing.");
		}
	}

	/**
	 * Returns the serial input buffer.
	 * 
	 * @return the in
	 */
	ByteArrayOutputStream getIn() {
		if (in == null) {
			in = new ByteArrayOutputStream();
		}
		return in;
	}

	/**
	 * Resets the serial input buffer.
	 */
	void resetIn() {
		in = null;
	}

	/**
	 * Whether or not the serial input buffer is empty.
	 * 
	 * @return
	 */
	boolean isInEmpty() {
		return in == null || in.size() == 0;
	}

	/**
	 * Whether or not the data is a message delimiter.
	 * 
	 * @param c
	 * @return
	 */
	static boolean isDelimiter(int c) {
		return c == '\n' || c == '\r';
	}

	/**
	 * Whether or not the application is initialzied.
	 * 
	 * @return
	 */
	static boolean isNotInitialized() {
		return getZigBeeDeviceModelMapper() == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.moat.android.example2.SerialInputOutputManager.Listener#onNewData(byte[])
	 */
	@Override
	public void onNewData(byte[] data) {
		final String dataString = new String(data);
		LOGGER.info("[onNewData:IN] Read bytes: {}, Text: {}", data.length,
				dataString);
		String additionalInfo = "Nothing Done.\n";
		for (int i = 0; i < data.length; i++) {
			if (isDelimiter(data[i])) {
				if (isInEmpty() || isNotInitialized()) {
					continue;
				}
				final byte[] payload = getIn().toByteArray();
				resetIn();
				final ZigBeeDevice zigBeeDevice = getZigBeeDeviceModelMapper()
						.findByUid(ZIGBEE_DEVICE_UID);
				if (zigBeeDevice == null) {
					LOGGER.warn(
							"[onNewData] ZigBee Device data is missing. Message => {}",
							HexDump.dumpHexString(payload));
					continue;
				}
				final String message = new String(payload);
				LOGGER.info("[onNewData] message => {}", message);
				if (message.startsWith("TEMP:")) {
					additionalInfo += performTemperatureResponse(zigBeeDevice,
							message);
				} else if ("CLICKED:TRUE".equals(message)) {
					additionalInfo += performClicked(zigBeeDevice);
				} else {
					LOGGER.warn("[onNewData] Unknown message. Ignored.");
				}
			} else {
				getIn().write(data[i]);
			}
		}
		final String line = additionalInfo;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView.append(line);
				scrollView.smoothScrollTo(0, textView.getBottom());
			}
		});
	}

	/**
	 * 
	 * @param zigBeeDevice
	 * @param message
	 */
	private String performTemperatureResponse(ZigBeeDevice zigBeeDevice,
			String message) {
		final float t = Float.valueOf(message.substring(5));
		zigBeeDevice.setTemperature(t);
		getZigBeeDeviceModelMapper().update(zigBeeDevice);
		getMoat().sendNotification(
				MoatIoTService.getMoatUrn(getUrnPrefix(), "ShakeEvent", "1.0"),
				null, new Object[] { zigBeeDevice });
		return "Temp(C):" + t + " => Notified to Server!\n";
	}

	/**
	 * 
	 * @param zigBeeDevice
	 */
	private String performClicked(ZigBeeDevice zigBeeDevice) {
		final boolean isClicked = zigBeeDevice.isClicked();
		zigBeeDevice.setClicked(true);
		zigBeeDevice.setLastClicked(System.currentTimeMillis());
		getZigBeeDeviceModelMapper().update(zigBeeDevice);
		if (isClicked) {
			return "Clicked => Last Clicked Time Has Been Updated.\n";
		} else {
			LOGGER.info("[performClicked] Button state changed => clicked:true");
			getMoat().sendNotification(
					MoatIoTService.getMoatUrn(getUrnPrefix(), "ShakeEvent",
							"1.0"), null, new Object[] { zigBeeDevice });
			return "Clicked => Notified to Server!\n";
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.moat.android.example2.SerialInputOutputManager.Listener#onRunError(java.lang.Exception)
	 */
	@Override
	public void onRunError(Exception e) {
		LOGGER.error("Exception detected.", e);
	}

	/**
	 * @return the serialInputOutputManager
	 */
	protected SerialInputOutputManager getSerialInputOutputManager() {
		return serialInputOutputManager;
	}

	/**
	 * Inquires a USB serial driver and returns if any driver is detected.
	 * 
	 * @return true if a USB serial driver is found and is ready.
	 */
	protected boolean inquireUsbSerialDriver() {
		UsbSerialDriver usbSerialDriver = UsbSerialProber.acquire(usbManager);
		try {
			stopSerialInputOutputManager();
			if (usbSerialDriver != null) {
				usbSerialDriver.open();
				usbSerialDriver.setBaudRate(BAUD_RATE);
			}

		} catch (IOException exception) {
			usbSerialDriver = null;

		} finally {
			this.usbSerialDriver = usbSerialDriver;
			startSerialInputOutputManager();
		}
		return usbSerialDriver != null;
	}

	/**
	 * setup {@link SerialInputOutputManager}
	 */
	protected void startSerialInputOutputManager() {
		if (usbSerialDriver != null) {
			serialInputOutputManager = new SerialInputOutputManager(
					usbSerialDriver);
			serialInputOutputManager.setListener(this);
			serialInputOutputExecutor.submit(serialInputOutputManager);
		}
	}

	/**
	 * {@link SerialInputOutputManager#stop()}
	 */
	protected void stopSerialInputOutputManager() {
		if (serialInputOutputManager != null) {
			serialInputOutputManager.stop();
			serialInputOutputManager = null;
		}
	}

	/**
	 * Closes the current {@link UsbSerialDriver}.
	 */
	protected void closeUsbSerialDriver() {
		if (usbSerialDriver != null) {
			try {
				usbSerialDriver.close();
			} catch (IOException ignored) {
			} finally {
				usbSerialDriver = null;
			}
		}
	}

	/**
	 * When the image is tapped.
	 * 
	 * @param view
	 */
	public void tapToEnd(View view) {
		stopService(new Intent(this, MoatIoTService.class));
		finish();
	}
}
