package com.crowd.service.base.impl;

import org.json.JSONObject;

import com.crowd.service.base.CrowdWorkerContext;

public class CrowdWorkerContextImpl extends CrowdContextImpl implements CrowdWorkerContext {

	private String workerHandle;

	private boolean disposed;

	private long startTime;

	private String path;

	private String params;

	private float progress;

	private String info;

	private WSChannelWrapper channelWrapper;

	/**
	 * worker内发起一次调用，就是一个独立事务，如果需要多个调用形成一个事务，可以通过封装一个单独的服务来处理
	 * 
	 * @param workerHandle
	 * @param serviceName
	 */
	CrowdWorkerContextImpl(WSChannelWrapper channelWrapper, String workerHandle, String path, String params,
			String serviceName) {
		super(null, null, serviceName);
		this.channelWrapper = channelWrapper;
		this.workerHandle = workerHandle;
		this.path = path;
		this.params = params;
		this.startTime = System.currentTimeMillis();
		this.progress = 0;
		this.info = "";
	}

	@Override
	public void reportWork(float progress, String info) {
		this.progress = progress;
		this.info = info;
		channelWrapper.sendRemoteMessage("system.processor.worker.update", this.toJSON());
	}

	@Override
	public void setResult(JSONObject resultObject) {
		workerHandle.toString();
		// TODO：推送到Kern
	}

	@Override
	public void setException(JSONObject resultObject) {
		workerHandle.toString();
		// TODO：推送到Kern
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void dispose() {
		disposed = true;
	}

	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		o.put("handle", this.workerHandle);
		o.put("time", this.startTime);
		o.put("path", this.path);
		o.put("params", this.params);
		o.put("progress", this.progress);
		o.put("info", this.info);
		o.put("status", disposed ? "disposing" : "none");
		return o;
	}

}
