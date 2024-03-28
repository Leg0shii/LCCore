package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TagOwnedDTO {
    private String uuid;
    private Date date;
}
