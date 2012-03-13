package com.aimprosoft.i18n.common.service;

import java.util.Locale;

public interface LocaleService {

    Locale[] getAvailableLocales();

    Locale[] getSortedLocales();
}