package com.mc6m.mod.dlampmod;

import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.save.SetColorType;
import com.mc6m.mod.dlampmod.tools.BlockPos;
import com.mc6m.mod.dlampmod.tools.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class DLampBlock extends Block {

    private final boolean isOn;


    public DLampBlock(boolean isOn) {
        super(Material.ground);
        // TODO Auto-generated constructor stub
        this.isOn = isOn;

        if (isOn) {
            this.setLightLevel(1.0F);
        }
        setHardness(1.5f);
        setResistance(10.0f);
        setHarvestLevel("pickaxe", 0);
//        setSoundType(SoundType.GLASS);
        setStepSound(new SoundType("grass", 1.0F, 1.0F));
    }

//    public void onBlockClicke(World par1World, int par2, int par3, int par4, EntityPlayer p5EP) {
//        if (Item.getIdFromItem(p5EP.inventory.getCurrentItem().getItem()) == 1
//                && Item.getIdFromItem(p5EP.inventory.getCurrentItem().getItem()) != 0
//                && p5EP != null
//                && par2 * par3 != 0
//                && par2 * par4 != 0) {
//
//            par1World.setBlockState(new BlockPos(par2, par3, par4), Block.getStateById(4));
//        }
//        System.out.println("debug tick.");
//    }

    public void onBlockAdded(World worldIn, int x, int y, int z) {
        setBlockColor(worldIn, new BlockPos(x, y, z));
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block blockIn) {
        setBlockColor(worldIn, new BlockPos(x, y, z));
//        if (!worldIn.isRemote && worldIn.getSeed() != 0) {
//            if (isOn && !worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
//                worldIn.scheduleBlockUpdate(x, y, z, this, 4);
//                setColor(worldIn, new BlockPos(x, y, z), false);
//            } else if (!this.isOn && worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
//                worldIn.setBlock(x, y, z, DLampMOD.lit_dBlock, 0, 2);
//                setColor(worldIn, new BlockPos(x, y, z), true);
//            }
//        }
    }

    public void updateTick(World worldIn, int x, int y, int z, Random rand) {
        setBlockColor(worldIn, new BlockPos(x, y, z));
    }

    private void setBlockColor(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote && worldIn.getSeed() != 0) {
            if (this.isOn && !worldIn.isBlockIndirectlyGettingPowered(pos.getX(), pos.getY(), pos.getZ())) {
                worldIn.setBlock(pos.getX(), pos.getY(), pos.getZ(), DLampMOD.dBlock, 0, 2);
                setColor(worldIn, pos, false);
            } else if (!this.isOn && worldIn.isBlockIndirectlyGettingPowered(pos.getX(), pos.getY(), pos.getZ())) {
                worldIn.setBlock(pos.getX(), pos.getY(), pos.getZ(), DLampMOD.lit_dBlock, 0, 2);
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
            dlvd.setDefault(r, g, b, SetColorType.FINAL_TRUE);
        }
    }
}
