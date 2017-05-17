package com.jd.uwp.common.utils;


import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.StringWriter;


public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    private static final JsonFactory jsonFactory = new JsonFactory();

    public static <T> T fromJson(String jsonAsString, Class<T> pojoClass)
            throws Exception {
        return (T) objectMapper.readValue(jsonAsString, pojoClass);
    }

    public static String toJson(Object pojo, boolean prettyPrint) {
        StringWriter sw = new StringWriter();
        try{
	        JsonGenerator jg = jsonFactory.createJsonGenerator(sw);
	        if (prettyPrint) {
	            jg.useDefaultPrettyPrinter();
	        }
	        objectMapper.writeValue(jg, pojo);
        }catch(IOException ex){
        	throw new JsonConvertException(ex);
        }
        return sw.toString();
    }


    public static String toJson(Object pojo){
        return toJson(pojo, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fromJson(String jsonContent, TypeReference typeReference){
    	T rtnRst = null;
        try {
        	rtnRst = (T) objectMapper.readValue(jsonContent,typeReference);
		} catch (JsonParseException ex) {
			throw new JsonConvertException(ex);
		} catch (JsonMappingException ex) {
			throw new JsonConvertException(ex);
		} catch (IOException ex) {
			throw new JsonConvertException(ex);
		}
		return rtnRst;
    }
}