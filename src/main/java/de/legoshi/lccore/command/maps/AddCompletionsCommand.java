package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCompletionsCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.addcompletions")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        if(!player.getUniqueId().toString().equals("57344e2d-488a-428c-8537-0f22dfca2ec7")) {
            sender.sendMessage(Utils.chat("&cThis command is one time use only. Only Dkayee can use it as a precaution."));
            return true;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("linkcraft.arcade", "arcade");
        hashMap.put("linkcraft.deluxe", "deluxe");
        hashMap.put("linkcraft.elements", "elements");
        hashMap.put("linkcraft.fencecorridor", "fencecorridor");
        hashMap.put("linkcraft.geometrics", "geometrics");
        hashMap.put("linkcraft.hell", "hell");
        hashMap.put("linkcraft.hell2", "hell2");
        hashMap.put("linkcraft.oldv", "oldv");
        hashMap.put("linkcraft.oldvi", "oldvi");
        hashMap.put("linkcraft.sans", "sans");
        hashMap.put("deluxetags.tag.ancient", "temple");
        hashMap.put("linkcraft.tundra", "tundra");
        hashMap.put("linkcraft.unknown", "unknown");
        hashMap.put("linkcraft.unknown2", "unknown2");
        hashMap.put("linkcraft.village", "village");
        hashMap.put("linkcraft.wetwood", "wetwood");
        hashMap.put("deluxetags.tag.tbnr", "tbnr");
        hashMap.put("linkcraft.school", "school");
        hashMap.put("deluxetags.tag.Atlantis", "atlantis");
        hashMap.put("linkcraft.test", "arcade2");
        hashMap.put("linkcraft.magicmountain", "magicmountain");
        hashMap.put("deluxetags.tag.nostalgia", "nostalgia");
        hashMap.put("deluxetags.tag.hogwart", "hogwarts");
        hashMap.put("deluxetags.tag.gray", "gray");
        hashMap.put("linkcraft.10jumps", "10jumps");
        hashMap.put("deluxetags.tag.hors", "gcxv");
        hashMap.put("deluxetags.tag.extra", "extra");
        hashMap.put("deluxetags.tag.français", "eiffel");
        hashMap.put("deluxetags.tag.evilxiv_victory", "evilxiv");
        hashMap.put("linkcraft.islands", "islands");
        hashMap.put("deluxetags.tag.lab-finish", "strangelab");
        hashMap.put("linkcraft.village2", "village2");
        hashMap.put("linkcraft.center", "center");
        hashMap.put("deluxetags.tag.arcade3_victory", "arcade3");
        hashMap.put("linkcraft.frogcorridor", "frogcorridor");
        hashMap.put("deluxetags.tag.zircon", "city");
        hashMap.put("deluxetags.tag.Prophecy", "prophecy");
        hashMap.put("deluxetags.tag.Prehistoric", "prehistoric");
        hashMap.put("deluxetags.tag.Witcher", "beauclair");
        hashMap.put("deluxetags.tag.CR", "christmasroom");
        hashMap.put("deluxetags.tag.Osiris", "osiris");
        hashMap.put("deluxetags.tag.kindgcxvvictory", "kindgcxv");
        hashMap.put("deluxetags.tag.station_beat", "station");
        hashMap.put("deluxetags.tag.hell3_victory", "hell3");
        hashMap.put("deluxetags.tag.pandorasbox_victorytag", "pandorasbox");
        hashMap.put("linkcraft.minibedroom", "minibedroom");
        hashMap.put("deluxetags.tag.geometrics2", "geometrics2");
        hashMap.put("deluxetags.tag.menagerie_victory", "menagerie");
        hashMap.put("linkcraft.underground", "underground");
        hashMap.put("linkcraft.center2", "center2");
        hashMap.put("linkcraft.abandonedvillage", "abandonedvillage");
        hashMap.put("linkcraft.mansion", "mansion");
        hashMap.put("linkcraft.lava", "lava");
        hashMap.put("linkcraft.spacestation", "spacestation");
        hashMap.put("linkcraft.frostflame_victory", "frostflame");
        hashMap.put("linkcraft.demonland", "demonland");
        hashMap.put("deluxetags.tag.museum_victory", "museum");
        hashMap.put("linkcraft.shroomlight", "shroomlight");
        hashMap.put("deluxetags.tag.sureiya_victory", "sureiya");
        hashMap.put("deluxetags.tag.deviltown_victory", "deviltown");
        hashMap.put("deluxetags.tag.strangelab2_victoryred", "strangelab2");
        hashMap.put("deluxetags.tag.strangelab2_victoryblue", "strangelab2");
        hashMap.put("deluxetags.tag.sidewayskitchen_victory", "sidewayskitchen");
        hashMap.put("deluxetags.tag.rune_victory", "rune");
        hashMap.put("deluxetags.tag.rewind_victory", "rewind");
        hashMap.put("deluxetags.tag.exotic", "menagerie2");
        hashMap.put("deluxetags.tag.playroom_win", "playroom");
        hashMap.put("deluxetags.tag.eh_win", "endurancehall");
        hashMap.put("deluxetags.tag.mars_complete", "mars");
        hashMap.put("linkcraft.facility", "facility");
        hashMap.put("deluxetags.tag.5floors-complete", "5floors");
        hashMap.put("deluxetags.tag.pharaohtemple_complete", "pharaohstemple");
        hashMap.put("deluxetags.tag.geo3_complete", "geometrics3");
        //hashMap.put("deluxetags.tag.skydomain_complete", "skydomain");
        hashMap.put("deluxetags.tag.tianxia_complete", "tianxia");
        hashMap.put("deluxetags.tag.farwest_complete", "farwest");
        hashMap.put("deluxetags.tag.weedking", "suspiciousbarn");
        hashMap.put("deluxetags.tag.classroom_win", "classroom");
        hashMap.put("deluxetags.tag.mario_win", "mario");
        hashMap.put("deluxetags.tag.reiteration_win", "reiteration");
        hashMap.put("deluxetags.tag.aquaticcorridor_win", "aquaticcorridor");
        hashMap.put("deluxetags.tag.smile_win", "smile");
        hashMap.put("deluxetags.tag.house_finish", "house");
        hashMap.put("linkcraft.shroomlight2", "shroomlight2");
        hashMap.put("deluxetags.tag.underground2_end", "underground2");
        hashMap.put("deluxetags.tag.pizzeria_end", "pizzeria");
        hashMap.put("deluxetags.tag.minigerie_end", "minigerie");
        hashMap.put("deluxetags.tag.village3_end", "village3");
        hashMap.put("deluxetags.tag.castle_end", "castle");
        hashMap.put("deluxetags.tag.cosmos_end", "cosmos");
        hashMap.put("deluxetags.tag.farm_end", "farm");
        hashMap.put("deluxetags.tag.forest_end", "forest");
        hashMap.put("deluxetags.tag.frozenlab_end", "frozenlab");
        hashMap.put("deluxetags.tag.ipad_end", "ipad");
        hashMap.put("deluxetags.tag.mall_end", "mall");
        hashMap.put("deluxetags.tag.mountain_end", "mountain");
        hashMap.put("deluxetags.tag.stinna_end", "stinna");
        hashMap.put("deluxetags.tag.underwater_end", "underwater");
        //hashMap.put("deluxetags.tag.chinesenether_end", "orientalnether");
        hashMap.put("deluxetags.tag.frozencave_end", "frozencave");
        hashMap.put("deluxetags.tag.natural_end", "natural");
        hashMap.put("deluxetags.tag.epk_xii_victor", "oceantemple");
        hashMap.put("deluxetags.tag.steampunk_victor", "steampunk");
        hashMap.put("deluxetags.tag.illusion_victor", "illusion");
        //hashMap.put("deluxetags.tag.colorcaves_end", "colorcaves");
        hashMap.put("deluxetags.tag.clashofclans_end", "clashofclans");
        hashMap.put("deluxetags.tag.terraqueous_end", "terraqueous");
        hashMap.put("deluxetags.tag.breakingbad_end", "breakingbad");
        hashMap.put("deluxetags.tag.spacecity_end", "spacecity");
        hashMap.put("deluxetags.tag.hospital_end", "hospital");
        hashMap.put("linkcraft.cliffside", "cliffside");
        hashMap.put("linkcraft.magiclibrary", "magiclibrary");
        hashMap.put("deluxetags.tag.ascension_win", "ascent");
        hashMap.put("deluxetags.tag.pisa_end", "pisa");
        hashMap.put("deluxetags.tag.venice_end", "venice");
        hashMap.put("deluxetags.tag.hotel_end", "hotel");
        hashMap.put("deluxetags.tag.minireactor_end", "minireactor");
        hashMap.put("deluxetags.tag.house2_end", "house2");
        hashMap.put("deluxetags.tag.space_win", "space");
        hashMap.put("deluxetags.tag.roc_win", "roc");
        hashMap.put("deluxetags.tag.elvenation_win", "elvenation");
        hashMap.put("deluxetags.tag.kpbox_win", "kindpandorasbox");
        hashMap.put("deluxetags.tag.abyss_win", "abyss");
        hashMap.put("deluxetags.tag.tianxia2_end", "tianxia2");
        hashMap.put("deluxetags.tag.lunar_win", "lunar");
        hashMap.put("deluxetags.tag.deserticreliefs_win", "deserticreliefs");
        hashMap.put("deluxetags.tag.farewell_end", "farewell");
        hashMap.put("deluxetags.tag.oddbathroom_win", "oddbathroom");
        hashMap.put("deluxetags.tag.zabka_win", "zabka");
        hashMap.put("deluxetags.tag.factoryry_end", "factoyry");
        hashMap.put("deluxetags.tag.brimstone_win", "brimstone");
        hashMap.put("deluxetags.tag.pagodas_win", "minipagodas");
        hashMap.put("deluxetags.tag.deepsea_win", "deepsea");
        hashMap.put("deluxetags.tag.nethercastle_win", "nethercastle");
        hashMap.put("deluxetags.tag.planets_win", "planets");

        LuckPerms api = Linkcraft.getInstance().luckPerms;
        api.getUserManager().getUniqueUsers().thenAcceptAsync((list) -> {
            list.forEach((uuid) -> {
                api.getUserManager().loadUser(uuid).thenAcceptAsync((user ->  {
                    for(Map.Entry<String, String> entry : hashMap.entrySet()) {
                        if(user.getCachedData().getPermissionData().checkPermission(entry.getKey()).asBoolean()) {
                            String map = entry.getValue();
                            if(!MapManager.mapExists(map)) {
                                sender.sendMessage("Error invalid map name: " + map);
                                continue;
                            }

                            ConfigAccessor playerConfigAccessor = Utils.getPlayerConfig(uuid.toString());
                            FileConfiguration playerConfig = playerConfigAccessor.getConfig();
                            ConfigurationSection mapsSection = playerConfig.getConfigurationSection("maps");
                            if(mapsSection == null) {
                                playerConfig.createSection("maps");
                                mapsSection = playerConfig.getConfigurationSection("maps");
                            }

                            ConfigurationSection mapSection = mapsSection.getConfigurationSection(entry.getValue());

                            if(mapSection == null) {
                                mapSection = mapsSection.createSection(map);
                                mapSection.set("firstcompletion", "N/A");
                                mapSection.set("completions", 1);
                                mapSection.set("lastcompletion", "N/A");
                            } else {
                                sender.sendMessage("This player already had a completion of the map " + map + " this should not be possible!");
                            }

                            playerConfigAccessor.saveConfig();
                        }
                    }
                }));
            });
        });

        return true;
    }
}
