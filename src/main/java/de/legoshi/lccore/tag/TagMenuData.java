package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TagMenuData {
    private List<TagDTO> ownedTags;
    private List<TagDTO> unownedTags;
}
