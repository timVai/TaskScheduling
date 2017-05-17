package com.jd.uwp.domain.model;

import java.util.HashMap;
import java.util.Map;

public enum MessageID implements TEnum {
	RequestLinkConnect(1),
	RequestLinkDisconnect(2),
    RequestAgentLogout(3),
    RequestAnswerTask(4),

	EventError(10),
	EventWelcome(11),
	EventPong(12),
	EventLinkConnected(13),
	EventLinkDisconnected(14);

	private final int value;
	
	private static final Map<Integer, String> clazzNames = new HashMap<Integer, String>();
	private static final String COMMON_EVT_PREFIX = "com.jd.uwp.domain.model.events.";
	static {
		clazzNames.put(1, COMMON_EVT_PREFIX + "RequestLinkConnect");
		clazzNames.put(2, COMMON_EVT_PREFIX + "RequestLinkDisconnect");
        clazzNames.put(3, COMMON_EVT_PREFIX + "RequestAgentLogout");
        clazzNames.put(4, COMMON_EVT_PREFIX + "RequestAnswerTask");

		clazzNames.put(10, COMMON_EVT_PREFIX + "EventError");
		clazzNames.put(11, COMMON_EVT_PREFIX + "EventWelcome");
		clazzNames.put(12, COMMON_EVT_PREFIX + "EventPong");
		clazzNames.put(13, COMMON_EVT_PREFIX + "EventLinkConnected");
		clazzNames.put(14, COMMON_EVT_PREFIX + "EventLinkDisconnected");

	}

	private MessageID(int value) {
		this.value = value;
	}

	public int intValue() {
		return value;
	}

	public static MessageID valueOf(final int intValue) {
		for (MessageID messageID : MessageID.values()) {
			if (messageID.intValue() == intValue) {
				return messageID;
			}
		}
		return null;
	}
	
	public static String getClazzName(final int intValue) {
		return clazzNames.get(intValue);
	}
}
