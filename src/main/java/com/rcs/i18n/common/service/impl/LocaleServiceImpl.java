package com.rcs.i18n.common.service.impl;

import com.rcs.i18n.common.service.LocaleService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

@Service
public class LocaleServiceImpl implements LocaleService {

    @Override
    public Locale[] getAvailableLocales() {
        //main Liferay Language object
        Language language = LanguageUtil.getLanguage();

        //go through available locales
        return language.getAvailableLocales();
    }

    @Override
    public Locale[] getSortedLocales() {
        Locale[] locales = getAvailableLocales();
        Arrays.sort(locales, new Comparator<Locale>() {
            @Override
            public int compare(Locale l1, Locale l2) {
                return l1.toString().compareTo(l2.toString());
            }
        });
        return locales;
    }
}
