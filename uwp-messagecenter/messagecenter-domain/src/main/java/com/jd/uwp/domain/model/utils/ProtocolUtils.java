package com.jd.uwp.domain.model.utils;


import com.jd.uwp.domain.model.Message;
import com.jd.uwp.domain.model.TEnum;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public final class ProtocolUtils {
	private static Logger logger = LoggerFactory.getLogger(ProtocolUtils.class);
	private static AtomicInteger sequenceNumber = new AtomicInteger(1);
	private static JSONSerializer serializer = new JSONSerializer().exclude(
			"*.class").exclude("params").transform(new TEnumTransformer(), TEnum.class);

	private static JSONDeserializer deserializer = new JSONDeserializer().use(
			TEnum.class, new TEnumObjectFactory()).use(null,
			new MessageIDLocator());

	public static int nextReferenceId() {
		if (sequenceNumber.get() == Integer.MAX_VALUE) {
			sequenceNumber.lazySet(1);
		}
		return sequenceNumber.getAndIncrement();
	}

	public static String toJSON(final Message message) {
		String json = null;
		try {
			json = serializer.deepSerialize(message);
		} catch (Exception e) {
			logger.error("toJSON [" + message + "] raised error:", e);
		}
		return json;
	}

	public static Message fromJSON(final String json) {
		Message message = null;
		try {
			message = (Message) deserializer.deserialize(json);
		} catch (Exception e) {
			logger.error("fromJSON [" + json + "] raised error:", e);
		}
		return message;
	}

}
