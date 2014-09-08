package net.glowstone.block.blocktype;

import java.util.Arrays;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;

public class BlockRails extends BlockType {
    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        setNewRailFacing(block.getState(), 2);
    }

    protected int[] getSurroundingRails(GlowBlock block) {
        // NSWE | 0, -1, 1, 2 -> empty
        int blocks[] = { 2, 2, 2, 2 };

        GlowBlock relBlock = block.getRelative(BlockFace.NORTH);
        if (isRail(relBlock)) {
            blocks[0] = 0;
        } else if (isSolid(relBlock)) { // don't check for -1
            relBlock = relBlock.getRelative(BlockFace.UP); // 1
            if (isRail(relBlock)) {
                blocks[0] = 1;
            }
        } else {
            relBlock = relBlock.getRelative(BlockFace.DOWN);
            if (isRail(relBlock)) {
                blocks[0] = -1;
            }
        }

        relBlock = block.getRelative(BlockFace.SOUTH);
        if (isRail(relBlock)) {
            blocks[1] = 0;
        } else if (isSolid(relBlock)) { // don't check for -1
            relBlock = relBlock.getRelative(BlockFace.UP); // 1
            if (isRail(relBlock)) {
                blocks[1] = 1;
            }
        } else {
            relBlock = relBlock.getRelative(BlockFace.DOWN);
            if (isRail(relBlock)) {
                blocks[1] = -1;
            }
        }

        if ((blocks[0] != 2 && blocks[1] != 2)
                || (blocks[0] == 1 || blocks[1] == 1)) {
            return blocks; // no need to check further, either it's straight
                           // line or its slope, so you cannot rotate it
        }

        relBlock = block.getRelative(BlockFace.WEST);
        if (isRail(relBlock)) {
            blocks[2] = 0;
        } else if (isSolid(relBlock)) { // don't check for -1
            relBlock = relBlock.getRelative(BlockFace.UP); // 1
            if (isRail(relBlock)) {
                blocks[2] = 1;
            }
        } else {
            relBlock = relBlock.getRelative(BlockFace.DOWN);
            if (isRail(relBlock)) {
                blocks[2] = -1;
            }
        }

        relBlock = block.getRelative(BlockFace.EAST);
        if (isRail(relBlock)) {
            blocks[3] = 0;
        } else if (isSolid(relBlock)) { // don't check for -1
            relBlock = relBlock.getRelative(BlockFace.UP); // 1
            if (isRail(relBlock)) {
                blocks[3] = 1;
            }
        } else {
            relBlock = relBlock.getRelative(BlockFace.DOWN);
            if (isRail(relBlock)) {
                blocks[3] = -1;
            }
        }
        return blocks;
    }

    public void setNewRailFacing(GlowBlockState state, int reclimit) {
        if (reclimit < 1) {
            return; // stack overflow check
        }

        GlowBlock block = state.getBlock();
        boolean slope = false;

        // default to north
        BlockFace face = BlockFace.NORTH;

        int rails[] = getSurroundingRails(block);

        // checking
        // NORTH
        if (rails[0] == 0) {
            setNewRailFacing(block.getRelative(BlockFace.NORTH).getState(),
                    --reclimit);

            if (rails[1] == 0) {
                setNewRailFacing(block.getRelative(BlockFace.SOUTH).getState(),
                        reclimit);
            } else if (rails[1] == 1) {
                setNewRailFacing(block.getRelative(0, 1, 1).getState(),
                        reclimit);
                slope = true;
                face = BlockFace.SOUTH;
            } else if (rails[1] == -1) {
                setNewRailFacing(block.getRelative(0, -1, 1).getState(),
                        reclimit);
            } else if (rails[2] < 1) {
                face = BlockFace.SOUTH_EAST;
                setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                        reclimit);
            } else if (rails[3] < 1) {
                face = BlockFace.SOUTH_WEST;
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            }
        } else if (rails[0] == -1) {
            GlowBlock b = block.getRelative(0, -1, -1);

            setNewRailFacing(b.getState(), --reclimit);

            if (rails[1] == -1) {
                setNewRailFacing(block.getRelative(0, -1, 1).getState(),
                        reclimit);
            } else if (rails[1] == 1) {
                face = BlockFace.SOUTH;
                slope = true;
                setNewRailFacing(block.getRelative(0, 1, 1).getState(),
                        reclimit);
            } else if (rails[2] < 1) {
                face = BlockFace.SOUTH_EAST;
                setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                        reclimit);
            } else if (rails[3] < 1) {
                face = BlockFace.SOUTH_WEST;
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            }

        } else if (rails[0] == 1) {
            setNewRailFacing(block.getRelative(0, 1, -1).getState(), --reclimit);
            slope = true;
            if (rails[1] < 1) {
                setNewRailFacing(block.getRelative(0, rails[1], 1).getState(),
                        reclimit);
            }

            // SOUTH
        } else if (rails[1] == 0) {
            setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                    --reclimit);
            if (rails[2] == 0) {
                face = BlockFace.NORTH_EAST;
                setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                        reclimit);
            } else if (rails[3] == 0) {
                face = BlockFace.NORTH_WEST;
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            } else {
                face = BlockFace.SOUTH;
                setNewRailFacing(block.getRelative(BlockFace.SOUTH).getState(),
                        reclimit);
            }
        } else if (rails[1] == -1) {
            GlowBlock b = block.getRelative(0, -1, 1);
            setNewRailFacing(b.getState(), --reclimit);

            if (rails[2] < 1) {
                face = BlockFace.NORTH_EAST;
                setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                        reclimit);
            } else if (rails[3] < 1) {
                face = BlockFace.NORTH_WEST;
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            }
        } else if (rails[1] == 1) {
            face = BlockFace.SOUTH;
            slope = true;

            // WEST
        } else if (rails[2] == 0) {
            face = BlockFace.WEST;
            setNewRailFacing(block.getRelative(BlockFace.WEST).getState(),
                    --reclimit);

            if (rails[3] == 0) {
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            } else if (rails[3] == 1) {
                face = BlockFace.EAST;
                slope = true;
                setNewRailFacing(block.getRelative(1, 1, 0).getState(),
                        reclimit);
            } else if (rails[3] == -1) {
                setNewRailFacing(block.getRelative(1, -1, 0).getState(),
                        reclimit);
            }
        } else if (rails[2] == -1) {
            face = BlockFace.WEST;
            GlowBlock b = block.getRelative(-1, -1, 0);
            setNewRailFacing(b.getState(), --reclimit);
            if (rails[3] == 0) {
                setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                        reclimit);
            } else if (rails[3] == 1) {
                face = BlockFace.EAST;
                slope = true;
            } else if (rails[3] == -1) {
                setNewRailFacing(block.getRelative(1, -1, 0).getState(),
                        reclimit);
            }
        } else if (rails[2] == 1) {
            face = BlockFace.WEST;
            slope = true;
            setNewRailFacing(block.getRelative(-1, 1, 0).getState(), --reclimit);
            if (rails[3] < 1) {
                setNewRailFacing(block.getRelative(1, rails[3], 0).getState(),
                        reclimit);
            }
            // EAST
        } else if (rails[3] == 0) {
            face = BlockFace.EAST;
            setNewRailFacing(block.getRelative(BlockFace.EAST).getState(),
                    --reclimit);
        } else if (rails[3] == -1) {
            face = BlockFace.EAST;
            GlowBlock b = block.getRelative(1, -1, 0);
            setNewRailFacing(b.getState(), --reclimit);
            setNewRailFacing(block.getRelative(1, -1, 0).getState(), reclimit);
        } else if (rails[3] == 1) {
            face = BlockFace.EAST;
            slope = true;
            setNewRailFacing(block.getRelative(1, 1, 0).getState(), --reclimit);
        }

        setDirection(state, face, slope);
        state.update();
    }

    public void setDirection(GlowBlockState state, BlockFace face, boolean slope) {
        MaterialData data = state.getData();
        if (data instanceof Rails) {
            ((Rails) data).setDirection(face, slope);
            state.setData(data);
        } else {
            // complain?
            GlowServer.logger
                    .warning("Placing Rails: MaterialData was of wrong type");
        }
    }

    public void setDirection(GlowBlock block, BlockFace face, boolean slope) {
        setDirection(block.getState(), face, slope);
    }

    public boolean isRail(GlowBlock b) {
        Material m = b.getType();
        return m == Material.RAILS || m == Material.ACTIVATOR_RAIL
                || m == Material.POWERED_RAIL;
    }

    public boolean isSolid(GlowBlock b) {
        return !b.isEmpty() && !b.isLiquid();
    }
}