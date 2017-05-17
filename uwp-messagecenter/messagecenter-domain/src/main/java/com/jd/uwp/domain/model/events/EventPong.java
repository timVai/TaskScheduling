package com.jd.uwp.domain.model.events;

import com.jd.uwp.domain.model.EventMessage;
import com.jd.uwp.domain.model.MessageID;
import com.jd.uwp.domain.model.utils.CommonUtils;

public class EventPong extends EventMessage {
	private static final long serialVersionUID = 2159775167371546166L;
	private String message;
	private final long creationTime;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EventPong() {
		super();
		creationTime = System.currentTimeMillis();
	}

	@Override
	public int getMessageId() {
		return MessageID.EventPong.intValue();
	}
	
	@Override
	public String getMessageName() {
		return MessageID.EventPong.name();
	}
	
	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append("agentId=" + getAgentId());
		sb.append(", message=" + getMessage());
		sb.append(", creationTime=" + CommonUtils.formatDateTime(getCreationTime()));
		sb.append("]");
		return sb.toString();
	}
}
