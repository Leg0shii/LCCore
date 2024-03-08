package de.legoshi.lccore.service.command;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.Namespace;
import me.fixeddev.commandflow.translator.TranslationProvider;

import java.util.HashMap;
import java.util.Map;

public class LinkcraftTranslationProvider implements TranslationProvider {
    protected Map<String, String> translations = new HashMap();

    public LinkcraftTranslationProvider() {
        this.translations.put("command.no-permission", MessageUtil.compose(Message.NO_PERMISSION, true));
        this.translations.put("invalid.double", MessageUtil.compose(Message.INVALID_DOUBLE, true));
        this.translations.put("invalid.integer", MessageUtil.compose(Message.INVALID_INTEGER, true));
    }

    public String getTranslation(String key) {
        return this.translations.get(key);
    }

    public String getTranslation(Namespace namespace, String key) {
        return this.getTranslation(key);
    }
}
