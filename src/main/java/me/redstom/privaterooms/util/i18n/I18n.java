package me.redstom.privaterooms.util.i18n;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.redstom.privaterooms.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class I18n {

    private final Map<Locale, Translator> translators = new HashMap<>();

    public Locale[] getLocales() {
        return translators.keySet().toArray(Locale[]::new);
    }

    @Autowired
    @SneakyThrows
    public void loaded(FileUtils fileUtils) {
        File languagesFolder = new File("languages");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        for (File file : languagesFolder.listFiles()) {
            log.info("Loading language file: {}", file.getName());
            register(file.getName().substring(0, file.getName().length() - 4));
        }
    }

    @SneakyThrows
    public void register(String locale) {
        Locale localeObject = Locale.forLanguageTag(locale);
        Translator translator = new Translator(localeObject);

        log.info("Registering language : {}", localeObject.getDisplayName());

        translators.put(localeObject, translator);
    }

    public Translator of(Locale locale) {
        return translators.getOrDefault(locale, translators.get(Locale.ENGLISH));
    }

}
