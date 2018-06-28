package com.qiuxu.common.util;
 
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
 
/**
 * Created by 264929 on 2016/1/5.
 */
public class SpringContextUtil implements ApplicationContextAware {
 
    private static ApplicationContext applicationContext;
 
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }
 
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
 
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }
 
    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) applicationContext.getBean(name, requiredType);
    }
 
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }
 
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }
 
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }
 
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }
}