/*
 * Copyright (C) 2013 InventIt Inc.
 * 
 * See https://github.com/inventit/moat-iot-get-started
 */
package com.yourinventit.moat.gae.example.models;

import java.util.List;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
public class VibrationDevice extends MoatModel {

	private String uid;

	private JSONObject image = new JSONObject();

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the image
	 */
	public JSONObject getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(JSONObject image) {
		this.image = image;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.moat.gae.example.models.MoatModel#asJson()
	 */
	@Override
	public String asJson() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.moat.gae.example.models.MoatModel#updateFrom(com.google.appengine.labs.repackaged.org.json.JSONObject)
	 */
	@Override
	public void updateFrom(JSONObject jsonObject) {
		setImage(jsonObject.optJSONObject("image"));
	}

	/**
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	public static List<VibrationDevice> find(int offset, int limit) {
		return find(VibrationDevice.class, offset, limit);
	}

}
