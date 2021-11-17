package com.crowd.core.webapi;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class SessionManager {

	private static Hashtable<String, Session> sessions = new Hashtable<String, Session>();

	public static Session getSession(String sessionId) {
		if (!sessions.containsKey(sessionId)) {
			throw new IllegalStateException();
		}
		Session session = sessions.get(sessionId);
		if (session.isExpired()) {
			sessions.remove(sessionId);
			throw new IllegalStateException();
		}
		session.active();
		return session;
	}

//	public static void disposeSession(String sessionId) {
//		sessions.remove(sessionId);
//		checkExpired();
//	}

	private static void checkExpired() {
		Set<String> expiredSessionIds = new HashSet<String>();
		for (String id : sessions.keySet()) {
			if (sessions.get(id).isExpired()) {
				expiredSessionIds.add(id);
			}
		}
		for (String id : expiredSessionIds) {
			sessions.remove(id);
		}
	}

	public static Session newSession() {
		checkExpired(); // TODO：应该改成定期检查清理，避免无效调用
		Session session = new Session();
		sessions.put(session.getId(), session);
		return session;
	}

}
