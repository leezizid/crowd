package com.crowd.service.base;

import org.json.JSONObject;

public interface CrowdWorkerContext extends CrowdContext{

	public void reportWork(float progress, String info);

	public void setResult(JSONObject dataObject);

	public void setException(JSONObject errorObject);

	public boolean isDisposed();

	public void dispose();

}
