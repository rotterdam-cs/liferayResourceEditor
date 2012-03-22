package com.rcs.i18n.common.cache;


/**
 * This interface provide base resource caching
 *
 * @see com.rcs.i18n.common.cache.impl.EhCacheService
 * @see com.rcs.i18n.common.cache.impl.NopCacheService
 */
public interface CacheService {

    /**
     * Gets cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @return result if exists or null otherwise
     */
    Object getResult(String serviceName, String methodName, Object[] params);

    /**
     * Put new cached result
     *
     * @param serviceName - cached service name
     * @param methodName - cached method name
     * @param params - cache params
     * @param object - object to cache
     */
    void putResult(String serviceName, String methodName, Object[] params, Object object);

    /**
     * Clear cache of service
     *
     * @param serviceName - cached service name
     */
    void clearService(String serviceName);

    /**
     * Clear all cache
     */
    void clearCache();
}
