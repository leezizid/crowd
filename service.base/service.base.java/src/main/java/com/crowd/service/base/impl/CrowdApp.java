package com.crowd.service.base.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crowd.service.base.CrowdInvokeeInfo;
import com.crowd.service.base.CrowdMethod;
import com.crowd.service.base.CrowdService;
import com.crowd.service.base.CrowdSubscriber;
import com.crowd.service.base.CrowdWorker;
import com.crowd.service.base.TableDefine;
import com.crowd.service.base.TopicSubscriber;
import com.crowd.service.base.TopicSubscriberHandle;

public class CrowdApp {

	private static WSChannelWrapper channelWrapper;
	final static Map<Class<? extends TableDefine>, TableDefine> registerTables = new HashMap<Class<? extends TableDefine>, TableDefine>();
	private final static Map<String, CrowdInvokeeInfo> serviceMethods = new HashMap<String, CrowdInvokeeInfo>();
	private final static Map<String, List<CrowdInvokeeInfo>> serviceSubscribers = new HashMap<String, List<CrowdInvokeeInfo>>();
	private final static Map<String, List<TopicSubscriber>> runtimeSubscribers = new HashMap<String, List<TopicSubscriber>>();

	final static String SYSTEM_KEY_REQUEST_ID = "___REQUEST___";
	final static String CORE_SERVICE_DS = "___core.ds";
	final static String CORE_SERVICE_USER = "___core.user";
	final static String CORE_SERVICE_FILE = "___core.file";
	final static String CORE_SERVICE_WORKER = "___core.worker";
	final static String CORE_SERVICE_DML_INSERT = "/" + CORE_SERVICE_DS + "/insert";
	final static String CORE_SERVICE_DML_UPDATE = "/" + CORE_SERVICE_DS + "/update";
	final static String CORE_SERVICE_DML_DELETE = "/" + CORE_SERVICE_DS + "/delete";
	final static String CORE_SERVICE_DML_FIND = "/" + CORE_SERVICE_DS + "/find";
	final static String CORE_SERVICE_DML_EXECUTE_QUERY = "/" + CORE_SERVICE_DS + "/executeQuery";
	final static String CORE_SERVICE_DML_EXECUTE_UPDATE = "/" + CORE_SERVICE_DS + "/executeUpdate";
	final static String CORE_SERVICE_DML_RESOLVE = "/" + CORE_SERVICE_DS + "/resolve";
	final static String CORE_SERVICE_USER_CHANGE = "/" + CORE_SERVICE_USER + "/change";
	final static String CORE_SERVICE_USER_VALIDATE = "/" + CORE_SERVICE_USER + "/validate";
	final static String CORE_SERVICE_FILE_SAVE = "/" + CORE_SERVICE_FILE + "/save";
	final static String CORE_SERVICE_FILE_LOAD = "/" + CORE_SERVICE_FILE + "/load";
	final static String CORE_SERVICE_FILE_DELETE = "/" + CORE_SERVICE_FILE + "/delete";
	final static String CORE_SERVICE_WORKER_LIST = "/" + CORE_SERVICE_WORKER + "/list";
	final static String CORE_SERVICE_WORKER_DISPOSE = "/" + CORE_SERVICE_WORKER + "/dispose";

//	private final static String zkServerAddress;
//	private final static String serverIp;
//	private final static int serverPort;
//	private final static String domain;

	static {
//		zkServerAddress = System.getProperty("zkServer");
//		serverIp = System.getProperty("ip", "127.0.0.1");
//		serverPort = Integer.parseInt(System.getProperty("port", "9090"));
//		domain = System.getProperty("domain");
	}

	public static void main(String[] args) throws Throwable {

		JSONArray tableDefineArray = new JSONArray();

		//
		ServiceLoader<CrowdService> shapeLoader = ServiceLoader.load(CrowdService.class);
		Iterator<CrowdService> it = shapeLoader.iterator();
		while (it.hasNext()) {
			CrowdService crowdService = it.next();
			// 初始化服务
			CrowdInitContextImpl initContext = new CrowdInitContextImpl(crowdService.getName());
			crowdService.init(initContext);
			//
			for (TableDefine tableDefine : initContext.getRegisterTables()) {
				tableDefineArray.put(tableDefine.toJSON());
				registerTables.put(tableDefine.getClass(), tableDefine);
			}
			// 缓存对象和方法
			for (Method method : crowdService.getClass().getMethods()) {
				if (method.isAnnotationPresent(CrowdMethod.class) || method.isAnnotationPresent(CrowdWorker.class)) {
					serviceMethods.put("/" + crowdService.getName() + "/" + method.getName(),
							new CrowdInvokeeInfo(crowdService, method));
				}
				if (method.isAnnotationPresent(CrowdSubscriber.class)) {
					String topic = method.getAnnotation(CrowdSubscriber.class).value();
					//
					if (!serviceSubscribers.containsKey(topic)) {
						serviceSubscribers.put(topic, new ArrayList<CrowdInvokeeInfo>());
					}
					List<CrowdInvokeeInfo> methodList = serviceSubscribers.get(topic);
					methodList.add(new CrowdInvokeeInfo(crowdService, method));
				}
			}
		}

		JSONObject registerInfo = new JSONObject();
		registerInfo.put("tables", tableDefineArray);
		registerInfo.put("methods", new JSONArray(serviceMethods.keySet()));
		registerInfo.put("subscribers", new JSONArray(serviceSubscribers.keySet()));

		channelWrapper = new WSChannelWrapper("ws://127.0.0.1:33333/websocket", registerInfo);
		new Thread(channelWrapper).start();

		//
		int tryCount = 50;
		while (true) {
			if (channelWrapper.getStatus() == WSChannelWrapper.WS_CONNECTED) {
				break;
			}
			Thread.sleep(100);
			tryCount--;
			if(tryCount == 0) {
				System.out.println("连接核心服务超时，App Exited...");
				System.exit(-1);
			}
		}
		
		//
		System.out.println("App Started...");

		//
		it = shapeLoader.iterator();
		while (it.hasNext()) {
			CrowdService crowdService = it.next();
			CrowdInitContextImpl initContext = new CrowdInitContextImpl(crowdService.getName());
			crowdService.postInit(initContext);
		}
	}

	static final JSONObject invokeLocalMethod(CrowdContextImpl contextImpl, String apiPath, JSONObject inputObject) {
		JSONObject returnObject = new JSONObject();
		JSONObject outputObject = new JSONObject();
		try {
			CrowdInvokeeInfo invokeeInfo = serviceMethods.get(apiPath);
			if (invokeeInfo == null) {
				throw new UnsupportedOperationException("找不到可用的服务信息");
			}
			try {
				invokeeInfo.getMethod().invoke(invokeeInfo.getServiceInstance(), contextImpl, inputObject,
						outputObject);
			} catch (InvocationTargetException t) {
				throw t.getTargetException();
			}
			returnObject.put("data", outputObject);
		} catch (Throwable t) {
			JSONObject errorObject = new JSONObject();
			errorObject.put("type", t.getClass().getName());
			errorObject.put("message", t.getMessage() == null ? t.getClass().getName() : t.getMessage());
			returnObject.put("error", errorObject);
		}
		return returnObject;
	}

	static final void invokeLocalWorker(CrowdWorkerContextImpl contextImpl, String apiPath, JSONObject inputObject)
			throws Throwable {
		CrowdInvokeeInfo invokeeInfo = serviceMethods.get(apiPath);
		if (invokeeInfo == null) {
			throw new UnsupportedOperationException("找不到可用的服务信息");
		}
		invokeeInfo.getMethod().invoke(invokeeInfo.getServiceInstance(), contextImpl, inputObject);
	}

	static final void notifyLocalSubscribers(String topic, JSONObject messageObject) {
		List<CrowdInvokeeInfo> serviceSubscriberList = serviceSubscribers.get(topic);
		if (serviceSubscriberList != null && serviceSubscriberList.size() > 0) {
			for (final CrowdInvokeeInfo invokeeInfo : serviceSubscriberList) {
				// TODO：用线程池
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							invokeeInfo.getMethod().invoke(invokeeInfo.getServiceInstance(),
									new CrowdContextImpl(null, null, invokeeInfo.getServiceInstance().getName()),
									messageObject);
						} catch (Throwable t) {

						}
					}
				});
				t.setName("MessageSubscribe-" + topic + "-" + System.currentTimeMillis());
				t.start();
			}
		}
		List<TopicSubscriber> runtimeSubscriberList = runtimeSubscribers.get(topic);
		if (runtimeSubscriberList != null && runtimeSubscriberList.size() > 0) {
			for (final TopicSubscriber topicSubscriber : runtimeSubscriberList) {
				// TODO：用线程池
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							topicSubscriber.messageReceived(messageObject);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				});
				t.setName("MessageSubscribe-" + topic + "-" + System.currentTimeMillis());
				t.start();
			}
		}
	}

	static final TopicSubscriberHandle subscribeTopic(final String topic, final TopicSubscriber subscriber) {
		List<TopicSubscriber> runtimeSubscriberList = runtimeSubscribers.get(topic);
		if (runtimeSubscriberList == null) {
			runtimeSubscriberList = new ArrayList<TopicSubscriber>();
			runtimeSubscribers.put(topic, runtimeSubscriberList);
		}
		runtimeSubscriberList.add(subscriber);
		updateSubscribeTopics();
		return new TopicSubscriberHandle() {
			@Override
			public void unsubscribe() {
				List<TopicSubscriber> runtimeSubscriberList = runtimeSubscribers.get(topic);
				if (runtimeSubscriberList != null) {
					runtimeSubscriberList.remove(subscriber);
					if (runtimeSubscriberList.size() == 0) {
						runtimeSubscribers.remove(topic);
					}
					updateSubscribeTopics();
				}
			}
		};
	}

	static final void updateSubscribeTopics() {
		Set<String> topics = new HashSet<String>();
		topics.addAll(serviceSubscribers.keySet());
		topics.addAll(runtimeSubscribers.keySet());
		channelWrapper.updateSubscribeTopics(new JSONArray(topics));
	}

	static final JSONObject invokeRemoteService(String sid, String tid, String path, JSONObject inputObject,
			boolean async) throws Throwable {
		return channelWrapper.invokeRemoteService(sid, tid, path, inputObject, async);
	}

	static final void sendRemoteMessage(String topic, JSONObject messageObject) throws Throwable {
		channelWrapper.sendRemoteMessage(topic, messageObject);
	}

}
