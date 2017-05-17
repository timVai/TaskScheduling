package com.jd.uwp.domain.model;

public class RequestAnswerMessage {
	private static final long serialVersionUID = 1L;
    protected Integer messageId;
	protected Long referenceId;

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
