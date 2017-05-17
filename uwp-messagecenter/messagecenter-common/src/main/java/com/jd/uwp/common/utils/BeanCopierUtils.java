package com.jd.uwp.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanfengshi on 2017/2/23.
 */
public class BeanCopierUtils {
    public final static Map<String,net.sf.cglib.beans.BeanCopier> beanCopierMap = new HashMap<String,net.sf.cglib.beans.BeanCopier>();

    public static void copyProperties(Object source, Object target){
        String beanKey =  generateKey(source.getClass(), target.getClass());
        net.sf.cglib.beans.BeanCopier copier =  null;
        if(!beanCopierMap.containsKey(beanKey)){
            copier = net.sf.cglib.beans.BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.put(beanKey, copier);
        }else{
            copier = beanCopierMap.get(beanKey);
        }
        copier.copy(source, target, null);
    }
    private static String generateKey(Class<?> class1,Class<?>class2){
        return class1.toString() + class2.toString();
    }
}
