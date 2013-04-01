/*
 * Copyright (C) 2013 InventIt Inc.
 * 
 * See https://github.com/inventit/moat-iot-get-started
 */
package com.yourinventit.moat.gae.example.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.yourinventit.moat.gae.example.Constants;
import com.yourinventit.moat.gae.example.models.RequestHistory;
import com.yourinventit.moat.gae.example.models.ShakeEvent;
import com.yourinventit.moat.gae.example.models.SysDevice;
import com.yourinventit.moat.gae.example.models.SysDmjob;
import com.yourinventit.moat.gae.example.models.VibrationDevice;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
@SuppressWarnings("serial")
public class DashboardControllerServlet extends HttpServlet {

	static final String VIEW_PATH = "/dashboard.jsp";

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		final String pathInfo = req.getPathInfo();
		if ("/delete_device".equalsIgnoreCase(pathInfo)) {
			// delete_device
			SysDevice.delete(req.getParameter("device_uid"));
		} else if ("/vibrate".equalsIgnoreCase(pathInfo)) {
			// vibrate
			final SysDmjob entity = new SysDmjob();
			entity.setJobServiceId(Constants.getInstance()
					.getURNVibrateDevice());
			entity.setName(req.getParameter("name"));
			entity.setActivatedAt(new Date());
			entity.setExpiredAt(new Date(
					System.currentTimeMillis() + 15 * 60 * 1000));
			//
			// See http://developer.android.com/reference/android/os/Vibrator.html#vibrate(long[], int)
			// The first value indicates the number of milliseconds to wait before turning the vibrator on.
			// The next value indicates the number of milliseconds for which to keep the vibrator on before turning it off.
			// Subsequent values alternate between durations in milliseconds to turn the vibrator off or to turn the vibrator on.
			//
			// S-O-S * 2
			final JSONObject arguments = new JSONObject();
			try {
				arguments.put("options", new JSONArray("[0, 0, 0"
						// vibrate then wait
						+ ",200, 200, 200, 200, 200" + ",500"
						+ ",500, 200, 500, 200, 500" + ",500"
						+ ",200, 200, 200, 200, 200" + ",1000"
						+ ",200, 200, 200, 200, 200" + ",500"
						+ ",500, 200, 500, 200, 500" + ",500"
						+ ",200, 200, 200, 200, 200" + "]"));
			} catch (JSONException e) {
				throw new IllegalStateException(e);
			}
			entity.setArguments(arguments);
			SysDmjob.save(entity);
		} else if ("/cancel".equalsIgnoreCase(pathInfo)) {
			// cancel
			SysDmjob.delete(req.getParameter("uid"));
		} else if ("/delete_all_shake_events".equalsIgnoreCase(pathInfo)) {
			// delete_all_shake_events
			ShakeEvent.delete(req.getParameter("uids"),
					req.getParameter("device_uid"));
		}
		// index
		req.setAttribute("vibration_devices", VibrationDevice.find(0, -1));
		final List<SysDevice> devices = SysDevice.find(0, -1);
		req.setAttribute("devices", devices);
		final Map<String, List<ShakeEvent>> shakeEvents = new HashMap<String, List<ShakeEvent>>();
		for (SysDevice device : devices) {
			shakeEvents.put(device.getName(),
					ShakeEvent.find(device.getUid(), 0, -1));
		}
		req.setAttribute("shake_events", shakeEvents);
		req.setAttribute("job_list", SysDmjob.find(0, -1));
		req.setAttribute("job_histories", RequestHistory.find());

		req.getRequestDispatcher(VIEW_PATH).forward(req, resp);
	}
}
