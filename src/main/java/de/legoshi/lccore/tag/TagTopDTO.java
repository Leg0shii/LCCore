package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagTopDTO {
    List<TagTopEntry> entries;
    private int currentPage;
    private long totalPages;
    private long serverTagCount;
}
