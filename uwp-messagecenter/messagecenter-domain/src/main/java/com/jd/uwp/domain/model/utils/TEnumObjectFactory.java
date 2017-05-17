package com.jd.uwp.domain.model.utils;

import flexjson.JSONException;
import flexjson.JsonNumber;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class TEnumObjectFactory implements ObjectFactory {

	@SuppressWarnings("unchecked")
	@Override
	 public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
		 if( targetClass.isEnum() ) {
			try {
				if(value instanceof JsonNumber || value instanceof Integer) {
					Method method = targetClass.getMethod("valueOf", int.class);
					return method.invoke(null, ((JsonNumber) value).intValue());
				} else {
					Method method = targetClass.getMethod("valueOf", String.class);
					return method.invoke(null, value);
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException( String.format("%s:  Don't know how to convert %s to enumerated constant of %s", context.getCurrentPath(), value, targetType ) );
			} catch (SecurityException e) {
				throw new SecurityException( String.format("%s:  Don't know how to convert %s to enumerated constant of %s", context.getCurrentPath(), value, targetType ) );
			} catch (Exception e) {
				throw new RuntimeException( String.format("%s:  Don't know how to convert %s to enumerated constant of %s", context.getCurrentPath(), value, targetType ) );
			}
		} else {
			throw new JSONException( String.format("%s:  Don't know how to convert %s to enumerated constant of %s", context.getCurrentPath(), value, targetType ) );
		}
	}
}
