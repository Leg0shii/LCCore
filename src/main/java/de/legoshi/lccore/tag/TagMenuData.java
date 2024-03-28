package de.legoshi.lccore.tag;

import de.legoshi.lccore.database.models.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class TagMenuData {
    private List<TagDTO> ownedTags;
    private List<TagDTO> unownedTags;
}
