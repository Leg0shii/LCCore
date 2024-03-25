package de.legoshi.lccore.util;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;

import java.util.Arrays;

public class CommandException extends Exception {
    public CommandException(Message err, String ... params) {
        super(MessageUtil.compose(err, false, Arrays.asList(params).toArray()));
    }
}
