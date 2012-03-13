package com.aimprosoft.i18n.common.cache.impl;

import com.aimprosoft.i18n.common.cache.CacheService;
import org.springframework.stereotype.Service;

/**
 * This class implement Not Operable cache service
 *
 * @see com.aimprosoft.i18n.common.cache.CacheService
 */
@Service("nopCacheService")
public class NopCacheService implements CacheService {

    /**
     * Clear cache of service
     *
     * @param serviceName - cached service name
     */
    public void clearService(String serviceName) {
        //do nothing
    }

    /**
     * Clear all cache
     */
    public void clearCache() {
        //do nothing
    }

    /**
     * Put new cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @param object - object to cache
     */
    public void putResult(String serviceName, String methodName, Object[] params, Object object) {
        //do nothing
    }

    /**
     * Gets cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @return always returns null
     */
    public Object getResult(String serviceName, String methodName, Object[] params) {
        //do nothing
        return null;
    }
}
