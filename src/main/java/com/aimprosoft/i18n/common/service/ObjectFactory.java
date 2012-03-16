package com.aimprosoft.i18n.common.service;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class ObjectFactory implements ApplicationContextAware, InitializingBean{
    
    private static ApplicationContext _applicationContext;
    
    private Logger _logger = Logger.getLogger(getClass());
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ObjectFactory._applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (_applicationContext == null){
            _logger.fatal("It is impossible using ObjectFactory");
        }
    }

    public static <T> T getBean(Class<T> beanClass){
        return _applicationContext.getBean(beanClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName){
        return (T) _applicationContext.getBean(beanName);
    }
}
