package com.jd.uwp.domain.model;

import java.io.Serializable;

public interface Message extends Serializable {
	long getCreationTime();

	int getMessageId();
	
	String getMessageName();
}
