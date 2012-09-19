package com.rcs.i18n.hook.startup;

import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ObjectFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ObjectFactoryInitializer implements ApplicationContextAware, InitializingBean{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

        PortletBeanLocatorUtil.setBeanLocator("rcs", new BeanLocatorImpl(portalClassLoader, applicationContext));
    }

}
