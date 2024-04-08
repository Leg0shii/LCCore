package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagTopEntry {
    private int position;
    private String name;
    private int tagCount;
}
