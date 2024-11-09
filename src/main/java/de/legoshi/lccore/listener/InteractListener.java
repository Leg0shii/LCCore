package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import team.unnamed.inject.Inject;

import java.util.Arrays;
import java.util.List;

public class InteractListener implements Listener {
    @Inject private PracticeManager practiceManager;
    @Inject private PlayerManager playerManager;
    @Inject private CheckpointManager checkpointManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            onRightClick(e);
        }

        if(e.getAction().equals(Action.PHYSICAL)) {
            onPressurePlateStep(e);
        }
    }

    private void onPressurePlateStep(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if(isPressurePlate(b)) {
            Block attachedBlock = b.getRelative(BlockFace.DOWN);
            if(!isBlockFaceSolid(attachedBlock, BlockFace.UP)) {
                e.setCancelled(true);
            }
        }
    }

    private void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack held = player.getItemInHand();
        Block block = e.getClickedBlock();

        if(block != null) {
            onBlockClick(e, block);
        }

        if(held != null && !held.getType().equals(Material.AIR)) {
            onItemUsage(e, held);
        }

        if(block != null && held != null && !held.getType().equals(Material.AIR)) {
            onItemUsageOnBlock(e, held, block);
        }
    }

    private void onBlockClick(PlayerInteractEvent e, Block b) {
        if(checkpointManager.isCheckpointSign(b)) {
            onCheckpointUsage(e, b);
        }

//        if((isPressurePlate(b) || isButton(b)) && isFloating(b)) {
//            MessageUtil.log("True", true);
//            e.setCancelled(true);
//        }

        if(isButton(b)) {
            Button button = (Button)b.getState().getData();
            BlockFace attachedFace = button.getAttachedFace();
            Block attachedBlock = b.getRelative(attachedFace);
            if(!isBlockFaceSolid(attachedBlock, attachedFace.getOppositeFace())) {
                e.setCancelled(true);
            }
        }
    }

    public boolean isBlockFaceSolid(Block block, BlockFace face) {
        Material material = block.getType();
        List<Material> solidMats = Arrays.asList(Material.STONE, Material.GRASS, Material.DIRT, Material.COBBLESTONE, Material.WOOD, Material.SAPLING, Material.BEDROCK,
                Material.SAND, Material.GRAVEL, Material.GOLD_ORE, Material.IRON_ORE, Material.COAL_ORE, Material.LOG, Material.SPONGE, Material.LAPIS_ORE, Material.LAPIS_BLOCK,
                Material.DISPENSER, Material.SANDSTONE, Material.NOTE_BLOCK, Material.WOOL, Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.DOUBLE_STEP, Material.BRICK,
                Material.BOOKSHELF, Material.MOSSY_COBBLESTONE, Material.OBSIDIAN, Material.MOB_SPAWNER, Material.DIAMOND_ORE, Material.DIAMOND_BLOCK, Material.WORKBENCH,
                Material.FURNACE, Material.BURNING_FURNACE, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE, Material.SNOW_BLOCK, Material.CLAY, Material.JUKEBOX,
                Material.NETHERRACK, Material.SOUL_SAND, Material.GLOWSTONE, Material.SMOOTH_BRICK, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.MELON_BLOCK,
                Material.ENDER_PORTAL_FRAME, Material.ENDER_STONE, Material.REDSTONE_LAMP_ON, Material.REDSTONE_LAMP_OFF, Material.WOOD_DOUBLE_STEP,
                Material.EMERALD_ORE, Material.EMERALD_BLOCK, Material.COMMAND, Material.REDSTONE_BLOCK, Material.QUARTZ_ORE, Material.QUARTZ_BLOCK, Material.STAINED_CLAY, Material.LOG_2,
                Material.SLIME_BLOCK, Material.BARRIER, Material.PRISMARINE, Material.HAY_BLOCK, Material.HARD_CLAY, Material.COAL_BLOCK, Material.RED_SANDSTONE, Material.DOUBLE_STONE_SLAB2);

        if(solidMats.contains(material)) {
            return true;
        }

        if(material.equals(Material.SOIL)) {
            return face.equals(BlockFace.NORTH) || face.equals(BlockFace.EAST) || face.equals(BlockFace.SOUTH) || face.equals(BlockFace.WEST);
        }

        if(material.equals(Material.HOPPER)) {
            return face.equals(BlockFace.UP);
        }

        if(material.equals(Material.STEP)) {
            Step step = (Step)block.getState().getData();
            if(step.isInverted()) {
                return face.equals(BlockFace.UP);
            } else {
                return face.equals(BlockFace.DOWN);
            }
        }

        if(material.equals(Material.WOOD_STEP)) {
            WoodenStep step = (WoodenStep)block.getState().getData();
            if(step.isInverted()) {
                return face.equals(BlockFace.UP);
            } else {
                return face.equals(BlockFace.DOWN);
            }
        }

        if(material.name().toUpperCase().contains("STAIRS")) {
            Stairs stairs = (Stairs)block.getState().getData();
            if(stairs.isInverted()) {
                return face.equals(BlockFace.UP);
            }

            // Below is how vanilla minecraft works but for some reason Spigot or MC server does not allow buttons any face but UP
//            if(stairs.isInverted()) {
//                return face.equals(BlockFace.UP) || face.equals(stairs.getAscendingDirection());
//            } else {
//                return face.equals(BlockFace.UP) || face.equals(stairs.getAscendingDirection());
//            }
        }

        return false;
    }

    private boolean isPressurePlate(Block b) {
        return b != null && (b.getType().equals(Material.WOOD_PLATE) || b.getType().equals(Material.STONE_PLATE) ||
                             b.getType().equals(Material.IRON_PLATE) || b.getType().equals(Material.GOLD_PLATE));
    }

    private boolean isButton(Block b) {
        return b != null && (b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON));
    }

    private boolean isFloatingButton(Block b) {
        if(b != null && (b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON))) {
            Button button = (Button)b;
            BlockFace face = button.getAttachedFace();
            Block attachedTo = b.getRelative(face);

        } else {
            return false;
        }
//        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
//        for(BlockFace face : faces) {
//            Block relative = b.getRelative(face);
//            if(relative == null) {
//                continue;
//            }
//            Material relativeMat = relative.getType();
//            if(relativeMat.isSolid())
//                PressurePlate
//            if(relative != null && !relative.getType().equals(Material.AIR)) {
//                return false;
//            }
//        }
//        return true;true
        return true;
    }


    private void onCheckpointUsage(PlayerInteractEvent e, Block b) {
        checkpointManager.useCheckpoint(e.getPlayer(), b);
    }

    private void onItemUsage(PlayerInteractEvent e, ItemStack held) {
        if(ItemUtil.hasNbtId(held, "practice")) {
            practiceAction(e);
        }
    }

    private void onItemUsageOnBlock(PlayerInteractEvent e, ItemStack held, Block b) {
        Player p = e.getPlayer();
        if(ItemUtil.hasNbtId(held, "lcwand")) {
            playerManager.setClipboardPos2(p, b.getLocation());
        }
    }

    private void practiceAction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        practiceManager.usePracticeItem(player);
        e.setCancelled(true);
    }
}
