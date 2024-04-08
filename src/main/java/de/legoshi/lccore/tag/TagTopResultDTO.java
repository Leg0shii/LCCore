package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagTopResultDTO {
    List<TagTopEntry> entries;
    int totalPages;
}
