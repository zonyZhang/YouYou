package com.youyou.socketserver.domain;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

//错误编码
public enum ResponseCode {
	s_200(200, "成功"),
	e_201(201, "JSON对象不合法");

	private int responseCode;
	private String message;

	public int getResponseCode() {
		return responseCode;
	}

	public String getMessage() {
		return message;
	}

	private ResponseCode(int responseCode, String message) {
		this.responseCode = responseCode;
		this.message = message;
	}

	public JSONObject toJson(JSON data) {
		JSONObject retJson = new JSONObject();
		retJson.put("responseCode", responseCode);
		retJson.put("message", message);
		if (data != null) {
			retJson.put("data", data);
		}
		return retJson;
	}

	public JSONObject toJson() {
		return this.toJson(null);
	}
}
