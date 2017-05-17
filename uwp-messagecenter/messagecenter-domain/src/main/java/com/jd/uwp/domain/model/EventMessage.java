package com.jd.uwp.domain.model;

public abstract class EventMessage implements BaseMessage {
	private static final long serialVersionUID = 5196172923510373165L;
	protected Integer referenceId;
	protected long creationTime;
	protected String agentId;
	protected String clientAddress;

	protected EventMessage() {
		super();
		creationTime = System.currentTimeMillis();
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
		result = prime * result + getMessageId();
		result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
		result = prime * result + ((clientAddress == null) ? 0 : clientAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventMessage other = (EventMessage) obj;
		if (creationTime != other.creationTime)
			return false;
		if (getMessageId() != other.getMessageId())
			return false;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		if (clientAddress == null) {
			if (other.clientAddress != null)
				return false;
		} else if (!clientAddress.equals(other.clientAddress))
			return false;
		return true;
	}
}
