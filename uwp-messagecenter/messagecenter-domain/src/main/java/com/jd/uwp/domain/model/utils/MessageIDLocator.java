package com.jd.uwp.domain.model.utils;

import com.jd.uwp.domain.model.MessageID;
import flexjson.ClassLocator;
import flexjson.JsonNumber;
import flexjson.ObjectBinder;
import flexjson.Path;

import java.util.Map;

public class MessageIDLocator implements ClassLocator {

	@SuppressWarnings("unchecked")
	@Override
	public Class locate(ObjectBinder context, Path currentPath)
			throws ClassNotFoundException {
		if(context.getSource() instanceof Map){
			Map mapValue = (Map)context.getSource();
			Object obj = mapValue.get("messageId");
			Integer messageId = null;
			if(obj instanceof Integer)
				messageId = (Integer) mapValue.get("messageId");
			else if(obj instanceof JsonNumber)
				messageId = ((JsonNumber) mapValue.get("messageId")).toInteger();

			if (messageId != null) {
				String className = MessageID.getClazzName(messageId);
				return Class.forName(className);
			}else{
				return null;
			}

		}else{
			return null;
		}
		
		
	}

}
