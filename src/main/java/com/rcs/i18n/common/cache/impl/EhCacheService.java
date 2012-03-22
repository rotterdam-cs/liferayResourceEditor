package com.rcs.i18n.common.cache.impl;

import com.rcs.i18n.common.cache.CacheService;
import com.liferay.portal.kernel.util.StringPool;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ehCacheService")
public class EhCacheService implements CacheService {

    /**
     * Cache manager object
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * Logger object
     */
    private final Logger _logger = Logger.getLogger(getClass());

    /**
     * Method field
     */
    private static final String METHOD = "Method";

    /**
     * Gets cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @return result if exists or null otherwise
     */
    public synchronized Object getResult(String serviceName, String methodName, Object[] params) {
        Cache cache = getCache(serviceName);

        String key = createKey(methodName, params);

        if (_logger.isDebugEnabled()){
            _logger.debug("Get result with key " + key);
        }

        Element element = cache.get(key);

        return element == null ? null : element.getObjectValue();
    }

    /**
     * Put new cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @param object - object to cache
     */
    public synchronized void putResult(String serviceName, String methodName, Object[] params, Object object) {
/*
        if (object == null){
            return;
        }
*/

        Cache cache = getCache(serviceName);

        String key = createKey(methodName, params);

        if (_logger.isDebugEnabled()){
            _logger.debug("Put result with key " + key);
        }

        Element element = new Element(key, object);

        cache.put(element);
    }

    /**
     * Clear cache of service
     *
     * @param serviceName - cached service name
     */
    public synchronized void clearService(String serviceName) {
        Cache cache = getCache(serviceName);
        cache.removeAll();
    }

    /**
     * Clear all cache
     */
    public synchronized void clearCache() {
        _logger.debug("Start clearing all cache");
        try {
            cacheManager.clearAll();
        } catch (Exception e) {
            _logger.error("Could not clear all cache cause - " + e.getMessage(), e);
        }
        _logger.debug("Stop clearing all cache");
    }

    /**
     * Returns cache instance for service name
     *
     * @param serviceName - service name for cache instance
     * @return cache instance for service name
     */
    private Cache getCache(String serviceName) {
        if (!cacheManager.cacheExists(serviceName)){
            cacheManager.addCache(serviceName);
        }
        return cacheManager.getCache(serviceName);
    }

    /**
     * Creates string representation of cache arguments
     *
     * @param methodName - cache method name
     * @param params - cache arguments
     * @return string representation of cache arguments
     */
    public String createKey(String methodName, Object[] params) {
        StringBuffer key = new StringBuffer();

        key
                .append(METHOD)
                .append(StringPool.COLON)
                .append(methodName);

        if (params != null && params.length != 0){
            for (Object param : params) {
                _createKey(key, param);
            }
        }

        return key.toString();
    }

    /**
     * Creates recursively string representation of cache arguments
     *
     * @param key - current key state
     * @param param - current cache argument
     */
    private void _createKey(StringBuffer key, Object param) {
        if (param == null){
            //do nothing
        }
        else if (param instanceof List){
            for (Object p: (List)param)
                _createKey(key, p);
        }
        else if (param.getClass().isArray()){
            for (Object p: (Object[])param){
                _createKey(key, p);
            }
        } else {
            key
                    .append(StringPool.PIPE)
                    .append(param);
        }
    }
}