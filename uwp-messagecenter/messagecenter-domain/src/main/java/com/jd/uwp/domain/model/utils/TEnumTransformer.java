package com.jd.uwp.domain.model.utils;


import com.jd.uwp.domain.model.TEnum;
import flexjson.transformer.AbstractTransformer;

public class TEnumTransformer extends AbstractTransformer {

	@Override
	public void transform(Object object) {
		getContext().write(String.valueOf(((TEnum) object).intValue()));
	}
}