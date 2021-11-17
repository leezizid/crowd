//package com.crowd.core.webapi;
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONObject;
//
//public abstract class DMLServiceHandler {
//
//	public final boolean forUpdate;
//
//	public final boolean resolveTrans;
//
//	private static Method stopTransMethod;
//	private static Method releaseMethod;
//
//	public DMLServiceHandler() {
//		this(false, false);
//	}
//
//	public DMLServiceHandler(boolean forUpdate) {
//		this(forUpdate, false);
//	}
//
//	public DMLServiceHandler(boolean forUpdate, boolean resolveTrans) {
//		this.forUpdate = forUpdate;
//		this.resolveTrans = resolveTrans;
//	}
//
//	public abstract void run(Context context, JSONObject input, JSONObject output) throws Throwable;
//
//	private final static Map<String, DMLServiceHandler> serviceHandlers = new HashMap<String, DMLServiceHandler>();
//
//	static {
//		serviceHandlers.put("/___core.ds/executeQuery", new DMLServiceHandler() {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.executeQuery(context, input, output);
//			}
//		});
//		serviceHandlers.put("/___core.ds/executeUpdate", new DMLServiceHandler(true) {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.executeUpdate(context, input, output);
//			}
//		});
//		serviceHandlers.put("/___core.ds/insert", new DMLServiceHandler(true) {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.insert(context, input, output);
//			}
//		});
//		serviceHandlers.put("/___core.ds/update", new DMLServiceHandler(true) {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.update(context, input, output);
//			}
//		});
//		serviceHandlers.put("/___core.ds/delete", new DMLServiceHandler(true) {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.delete(context, input, output);
//			}
//		});
//		serviceHandlers.put("/___core.ds/find", new DMLServiceHandler() {
//			@Override
//			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
//				DMLService.find(context, input, output);
//			}
//		});
////		serviceHandlers.put("/___core.ds/resolve", new DMLServiceHandler(false, true) {
////
////			// XXX：增加自动捕捉事务发起放宕机后回滚事务的机制
////
////			private Method stopTransMethod;
////			private Method releaseMethod;
////
////			@Override
////			public void run(Context context, JSONObject input, JSONObject output) throws Throwable {
////				boolean commit = input.getBoolean("commit");
////				String tid = AppUtil.crossThreadTransactionId.get();
////				AppUtil.crossThreadTransactionId.remove();
////				Object o = AppUtil.crossThreadConnections.remove(tid);
////				if (stopTransMethod == null) {
////					stopTransMethod = o.getClass().getMethod("stopTrans", boolean.class);
////				}
////				if (releaseMethod == null) {
////					releaseMethod = o.getClass().getMethod("release");
////				}
////				try {
////					stopTransMethod.invoke(o, commit);
////				} finally {
////					releaseMethod.invoke(o);
////				}
////			}
////		});
//	}
//
//	static void invoke(Session session, String tid, String apiPath, JSONObject input, JSONObject output)
//			throws Throwable {
//		DMLServiceHandler serviceHandler = serviceHandlers.get(apiPath);
//		if (serviceHandler == null) {
//			throw new IllegalArgumentException("错误的请求路径");
//		}
//		// 如果存在跨线程事务连接对象，或者是更新操作，就标记跨线程事务
//		if (AppUtil.crossThreadConnections.containsKey(tid) || serviceHandler.forUpdate) {
//			AppUtil.crossThreadTransactionId.set(tid);
//		}
//		try {
//			Context context = session.newContext(false);
//			try {
//				serviceHandler.run(context, input, output);
//			} finally {
//				context.dispose();
//			}
//		} finally {
//			AppUtil.crossThreadTransactionId.remove();
//		}
//	}
//
//	static void resolve(String tid, boolean commit) throws Throwable {
//		Object o = AppUtil.crossThreadConnections.remove(tid);
//		if (o == null) {
//			return;
//		}
//		if (stopTransMethod == null) {
//			stopTransMethod = o.getClass().getMethod("stopTrans", boolean.class);
//		}
//		if (releaseMethod == null) {
//			releaseMethod = o.getClass().getMethod("release");
//		}
//		try {
//			stopTransMethod.invoke(o, commit);
//		} finally {
//			releaseMethod.invoke(o);
//		}
//	}
//}
