package com.crowd.tool.tapis.ctp;

import java.io.File;

public abstract class CtpBaseApi implements SpiCallback {

	protected String id;

	protected String disposeReason;

	private int requestID;

	public CtpBaseApi(String id) {
		this.id = id;
	}

	public abstract void handleMessage(String type, String message);

	protected abstract boolean checkContextDisposed();

	public final void dispose(String reason) {
		disposeReason = reason;
	}

	public final boolean isDisposed() {
		return disposeReason != null || checkContextDisposed();
	}

	protected int getRequestID() {
		return ++requestID;
	}

	public final void run() {
		// 创建flow文件夹
		File flowDir = new File("flow" + File.separator + id);
		// 如果文件夹不存在则创建
		if (!flowDir.exists() && !flowDir.isDirectory()) {
			flowDir.mkdir();
		}
		//
		doInit("." + File.separator + "flow" + File.separator + id + File.separator);
		//
		while (!isDisposed()) {
			try {

			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				try {
					Thread.sleep(3000);
				} catch (Throwable t) {

				}
			}
		}
		//
		doRelease();
	}

	protected abstract void doInit(String flowDir);

	protected abstract void doRelease();

}
