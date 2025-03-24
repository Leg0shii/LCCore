package de.legoshi.lccore.menu.help;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CommandHelpDTO {
    private String command;
    private String description;
    private String aliases;
}
