package com.jd.uwp.domain.model.events;

import com.jd.uwp.domain.model.EventMessage;
import com.jd.uwp.domain.model.MessageID;
import com.jd.uwp.domain.model.utils.CommonUtils;

public class EventWelcome extends EventMessage {
	private static final long serialVersionUID = -579927220192957122L;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EventWelcome() {
		super();
	}

	@Override
	public int getMessageId() {
		return MessageID.EventWelcome.intValue();
	}
	
	@Override
	public String getMessageName() {
		return MessageID.EventWelcome.name();
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
