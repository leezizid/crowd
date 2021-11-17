package com.crowd.tool.tapis.ctp;

import java.util.Base64;

import org.json.JSONObject;

public class ConnectInfo {

	private String traderFront;
	private String brokerId;
	private String userId;
	private String password;
	private String investorId;
	private String authCode;
	private String appId;
	private String productInfo;
	private String macAddress;

	public ConnectInfo(String traderFront, String brokerId, String userId, String password, String investorId,
			String authCode, String appId, String productInfo, String macAddress) {
		super();
		this.traderFront = traderFront;
		this.brokerId = brokerId;
		this.userId = userId;
		this.password = password;
		this.investorId = investorId;
		this.authCode = authCode;
		this.appId = appId;
		this.productInfo = productInfo;
		this.macAddress = macAddress;
	}

	private JSONObject getJson() {
		JSONObject jo = new JSONObject();
		jo.put("traderFront", this.traderFront);
		jo.put("brokerID", this.brokerId);
		jo.put("userID", this.userId);
		jo.put("password", this.password);
		jo.put("investorID", this.investorId);
		jo.put("authCode", this.authCode);
		jo.put("appID", this.appId);
		jo.put("productInfo", this.productInfo);
		jo.put("macAddress", this.macAddress);
		return jo;
	}

	public String getJsonBase64String() {
		return Base64.getEncoder().encodeToString(getJson().toString().getBytes());
	}

}
