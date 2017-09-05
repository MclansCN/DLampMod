package com.mc6m.mod.dlampmod;

import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.save.SetColorType;
import com.mc6m.mod.dlampmod.tools.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class DLampBlock extends Block {

    private final boolean isOn;

    public DLampBlock(boolean isOn) {
        super(Material.GROUND);
        // TODO Auto-generated constructor stub
        this.isOn = isOn;

        if (isOn) {
            this.setLightLevel(1.0F);
        }
        setHardness(1.5f);
        setResistance(10.0f);
        setHarvestLevel("pickaxe", 0);
        setSoundType(SoundType.GLASS);
    }

    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer p5EP) {
        if (Item.getIdFromItem(p5EP.inventory.getCurrentItem().getItem()) == 1
                && Item.getIdFromItem(p5EP.inventory.getCurrentItem().getItem()) != 0
                && p5EP != null
                && par2 * par3 != 0
                && par2 * par4 != 0) {
            par1World.setBlockState(new BlockPos(par2, par3, par4), Block.getStateById(4));

        }
        System.out.println("debug tick.");
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        setBlockColor(worldIn, pos);
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_neighborChanged_5_) {
        if (!worldIn.isRemote && worldIn.getSeed() != 0) {
            if (this.isOn && !worldIn.isBlockPowered(pos)) {
                worldIn.scheduleUpdate(pos, this, 4);
                setColor(worldIn, pos, false);
            } else if (!this.isOn && worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, DLampMOD.lit_dBlock.getDefaultState(), 2);
                setColor(worldIn, pos, true);
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        setBlockColor(worldIn, pos);
    }

    private void setBlockColor(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote && worldIn.getSeed() != 0) {
            if (this.isOn && !worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, DLampMOD.dBlock.getDefaultState(), 2);
                setColor(worldIn, pos, false);
            } else if (!this.isOn && worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, DLampMOD.lit_dBlock.getDefaultState(), 2);
                setColor(worldIn, pos, true);
            }
        }
    }

    private void setColor(World worldIn, BlockPos pos, boolean isOpen) {
        DLWorldSavedData dlwsd = DLWorldSavedData.get(worldIn);
        String did = dlwsd.getPos2did().get(pos);
        Map settingMap = null;
        DLampVirtualDevice dlvd = null;
        if (did != null) {
            settingMap = dlwsd.getSetting(dlwsd.getPos2did().get(pos));
            dlvd = DLampMOD.virtualdevicemap.get(did);
            if (dlvd != null) {
                dlvd.setLightOpen(isOpen);
            }
        }
        if (settingMap != null && dlvd != null) {
            String colorStr = (String) settingMap.get("color");
            int r = 0;
            int g = 0;
            int b = 0;
            if (isOpen) {
                r = Tools.scale16To10(colorStr.substring(1, 3));
                g = Tools.scale16To10(colorStr.substring(3, 5));
                b = Tools.scale16To10(colorStr.substring(5, 7));
            }
            dlvd.setTempRGB(r, g, b, SetColorType.FINAL_TRUE);
        }
    }
}
