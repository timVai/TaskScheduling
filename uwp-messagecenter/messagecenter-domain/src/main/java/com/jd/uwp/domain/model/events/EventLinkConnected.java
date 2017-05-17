package com.jd.uwp.domain.model.events;

import com.jd.uwp.domain.model.EventMessage;
import com.jd.uwp.domain.model.MessageID;
import com.jd.uwp.domain.model.utils.CommonUtils;

public class EventLinkConnected extends EventMessage {
	private static final long serialVersionUID = -9194900604656639358L;
	private Integer channelId;
	private String host;
	private Integer port;

	public EventLinkConnected() {
		super();
	}

	public EventLinkConnected(Integer channelId, String host, Integer port) {
		super();
		this.channelId = channelId;
		this.host = host;
		this.port = port;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public int getMessageId() {
		return MessageID.EventLinkConnected.intValue();
	}

	@Override
	public String getMessageName() {
		return MessageID.EventLinkConnected.name();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append("channelId=" + getChannelId());
		sb.append(", agentId=" + getAgentId());
		sb.append(", host=" + getHost());
		sb.append(", port=" + getPort());
		sb.append(", creationTime=" + CommonUtils.formatDateTime(getCreationTime()));
		sb.append("]");
		return sb.toString();
	}
}
