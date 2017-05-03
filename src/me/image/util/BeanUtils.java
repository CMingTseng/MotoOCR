package me.image.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanUtils {
    /**    
    
     * java反射bean的get方法    
     
     *     
     
     * @param objectClass    
     
     * @param fieldName    
     
     * @return    
     
     */       
      
    @SuppressWarnings("unchecked")       
      
    public static Method getGetMethod(Class objectClass, String fieldName) {       
      
        StringBuffer sb = new StringBuffer();       
      
        sb.append("get");       
      
        sb.append(fieldName.substring(0, 1).toUpperCase());       
      
        sb.append(fieldName.substring(1));       
      
        try {       
      
            return objectClass.getMethod(sb.toString());       
      
        } catch (Exception e) {       
      
        }       
      
        return null;       
      
    }       
      
           
      
    /**    
     
     * java反射bean的set方法    
     
     *     
     
     * @param objectClass    
     
     * @param fieldName    
     
     * @return    
     
     */       
      
    @SuppressWarnings("unchecked")       
      
    public static Method getSetMethod(Class objectClass, String fieldName) {       
      
        try {       
      
            Class[] parameterTypes = new Class[1];       
      
            Field field = objectClass.getDeclaredField(fieldName);       
      
            parameterTypes[0] = field.getType();       
      
            StringBuffer sb = new StringBuffer();       
      
            sb.append("set");       
      
            sb.append(fieldName.substring(0, 1).toUpperCase());       
      
            sb.append(fieldName.substring(1));       
            
            System.out.println("sb :"+sb);
      
            Method method = objectClass.getMethod(sb.toString(), parameterTypes);       
      
            return method;       
      
        } catch (Exception e) {       
      
            e.printStackTrace();       
      
        }       
      
        return null;       
      
    }       
      
           
      
    /**    
     
     * 執行set方法    
     
     *     
     
     * @param o執行對象
     
     * @param fieldName屬性    
     
     * @param value值    
     
     */       
      
    public static void invokeSet(Object o, String fieldName, Object value) {       
      
        Method method = getSetMethod(o.getClass(), fieldName);       
      
        try {       
        	String methodType = method.getReturnType().getName(); 
            method.invoke(o, new Object[] { value });       
      
        } catch (Exception e) {       
      
            e.printStackTrace();       
      
        }       
      
    }       
      
           
      
    /**    
     
     * 執行get方法    
     
     *     
     
     * @param o執行對象 
     
     * @param fieldName屬性    
     
     */       
      
    public static Object invokeGet(Object o, String fieldName) {       
      
        Method method = getGetMethod(o.getClass(), fieldName);       
      
        try {       
      
            return method.invoke(o, new Object[0]);       
      
        } catch (Exception e) {       
      
            e.printStackTrace();       
      
        }       
      
        return null;       
      
    }  
    
    /**    
    
     * 執行set方法    
     
     *     
     
     * @param object執行對象
     
     * @param fieldName属性    
     
     * @param value值    
     * 
     * 採用getMethod之回傳型態當放入get型態
     
     */       
      
    public static Object invokeSet(Object object, String beanProperty, String value) {  
        Object[] beanObject = beanMatch(object.getClass(), beanProperty);  
        Object[] cache = new Object[1];  
        Method getter = (Method) beanObject[0];  
        Method setter = (Method) beanObject[1];  
        try {  
            // 通過get取得類型
            String methodType = getter.getReturnType().getName();  
            if ((methodType.equalsIgnoreCase("long") || methodType.equalsIgnoreCase("java.lang.Long"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Long(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("int") || methodType.equalsIgnoreCase("java.lang.Integer"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Integer(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("short") || methodType.equalsIgnoreCase("java.lang.Short"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Short(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("float") || methodType.equalsIgnoreCase("java.lang.Float"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Float(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("double") || methodType.equalsIgnoreCase("java.lang.Double"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Double(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("boolean") || methodType.equalsIgnoreCase("java.lang.Boolean"))  
                    && !"".equals(value) && value != null) {  
                cache[0] = new Boolean(value);  
                return setter.invoke(object, cache);  
            } else if ((methodType.equalsIgnoreCase("java.lang.String")) && !"".equals(value) && value != null) {  
                cache[0] = value;  
                return setter.invoke(object, cache);  
            } else if (methodType.equalsIgnoreCase("java.io.InputStream")) {  
            } else if (methodType.equalsIgnoreCase("char")) {  
                cache[0] = (Character.valueOf(value.charAt(0)));  
                return setter.invoke(object, cache);  
            } 
    	
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        return null;
    	
    }
    
    private static Object[] beanMatch(Class clazz, String beanProperty) {  
        Object[] result = new Object[2];  
        char beanPropertyChars[] = beanProperty.toCharArray();  
        beanPropertyChars[0] = Character.toUpperCase(beanPropertyChars[0]);  
        String s = new String(beanPropertyChars);  
        String names[] = { ("set" + s).intern(), ("get" + s).intern(), ("is" + s).intern(), ("write" + s).intern(),  
                ("read" + s).intern() };  
        Method getter = null;  
        Method setter = null;  
        Method methods[] = clazz.getMethods();  
        for (int i = 0; i < methods.length; i++) {  
            Method method = methods[i];  
            if (!Modifier.isPublic(method.getModifiers()))  
                continue;  
            String methodName = method.getName().intern();  
            for (int j = 0; j < names.length; j++) {  
                String name = names[j];  
                if (!name.equals(methodName))  
                    continue;  
                if (methodName.startsWith("set") || methodName.startsWith("read"))  
                    setter = method;  
                else  
                    getter = method;  
            }  
        }  
        result[0] = getter;  
        result[1] = setter;  
        return result;  
    }  
    
}
