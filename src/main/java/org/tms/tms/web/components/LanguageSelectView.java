package org.tms.tms.web.components;

import ch.carnet.kasparscherrer.LanguageSelect;
import lombok.Getter;

import java.util.Locale;

public class LanguageSelectView extends LanguageSelect {

    @Getter
    private LanguageSelect langSelect;

    public LanguageSelectView() {
        boolean useLanguageCookies = true;
        langSelect = new LanguageSelect(useLanguageCookies, new Locale("ru"), new Locale("en"));
        add(langSelect);
    }
}
