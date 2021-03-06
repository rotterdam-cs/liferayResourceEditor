/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author V.Koshelenko
 */
public class StrutsResourceBundle extends ResourceBundle {

    private Logger _logger = Logger.getLogger(getClass());

    public StrutsResourceBundle(String portletName, Locale locale) {
        _portletName = portletName;
        _locale = locale;
    }

    @Override
    public Enumeration<String> getKeys() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return _locale;
    }

    @Override
    protected Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if ((key.equals(JavaConstants.JAVAX_PORTLET_DESCRIPTION) ||
                key.equals(JavaConstants.JAVAX_PORTLET_KEYWORDS) ||
                key.equals(JavaConstants.JAVAX_PORTLET_LONG_TITLE) ||
                key.equals(JavaConstants.JAVAX_PORTLET_SHORT_TITLE) ||
                key.equals(JavaConstants.JAVAX_PORTLET_TITLE))) {

            key = key.concat(StringPool.PERIOD).concat(_portletName);
        }
        String value = StringUtils.EMPTY;
        try {
            value = ResourceBundleUtil.getString(_locale, key);
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
        if (StringUtils.isBlank(value))
            LanguageUtil.get(_locale, key);
        if ((value == null) && ResourceBundleThreadLocal.isReplace()) {
            value = ResourceBundleUtil.NULL_VALUE;
        }
        return value;
    }

    private String _portletName;
    private Locale _locale;

}