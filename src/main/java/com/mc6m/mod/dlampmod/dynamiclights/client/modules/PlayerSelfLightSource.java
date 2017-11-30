package com.mc6m.mod.dlampmod.dynamiclights.client.modules;

import com.mc6m.mod.dlampmod.DLampMOD;
import com.mc6m.mod.dlampmod.DLampVirtualDevice;
import com.mc6m.mod.dlampmod.dynamiclights.client.DynamicLights;
import com.mc6m.mod.dlampmod.dynamiclights.client.IDynamicLightSource;
import com.mc6m.mod.dlampmod.dynamiclights.client.ItemConfigHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Map.Entry;

/**
 * @author AtomicStryker
 * <p>
 * Offers Dynamic Light functionality to the Player Entity itself.
 * Handheld Items and Armor can give off Light through this Module.
 * <p>
 * With version 1.1.3 and later you can also use FMLIntercomms to use this
 * and have the player shine light. Like so:
 * <p>
 * FMLInterModComms.sendRuntimeMessage(sourceMod, "DynamicLights_thePlayer", "forceplayerlighton", "");
 * FMLInterModComms.sendRuntimeMessage(sourceMod, "DynamicLights_thePlayer", "forceplayerlightoff", "");
 * <p>
 * Note you have to track this yourself. Dynamic Lights will accept and obey, but not recover should you
 * get stuck in the on or off state inside your own code. It will not revert to off on its own.
 */
@Mod(modid = "DynamicLights_thePlayer", name = "Dynamic Lights Player Light", version = "1.1.3", dependencies = "required-after:DynamicLights")
public class PlayerSelfLightSource implements IDynamicLightSource {
    public EntityPlayer thePlayer;
    private World lastWorld;
    private int lightLevel;
    private boolean enabled;
    private Configuration config;

    public boolean fmlOverrideEnable;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        config = new Configuration(evt.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void load(FMLInitializationEvent evt) {
        lightLevel = 0;
        enabled = false;
        lastWorld = null;
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent evt) {
        config.load();

        Property itemsList = config.get(Configuration.CATEGORY_GENERAL, "LightItems", "torch,glowstone=12,glowstone_dust=10,lit_pumpkin,lava_bucket,redstone_torch=10,redstone=10,golden_helmet=14,golden_horse_armor=15");
        itemsList.setComment("Item IDs that shine light while held. Armor Items also work when worn. [ONLY ON YOURSELF]");
        ItemConfigHelper itemsMap = new ItemConfigHelper(itemsList.getString(), 15);

        Property notWaterProofList = config.get(Configuration.CATEGORY_GENERAL, "TurnedOffByWaterItems", "torch,lava_bucket");
        notWaterProofList.setComment("Item IDs that do not shine light when held in water, have to be present in LightItems.");
        ItemConfigHelper notWaterProofItems = new ItemConfigHelper(notWaterProofList.getString(), 1);

        config.save();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent tick) {
        if (lastWorld != FMLClientHandler.instance().getClient().theWorld || thePlayer != FMLClientHandler.instance().getClient().thePlayer) {
            thePlayer = FMLClientHandler.instance().getClient().thePlayer;
            if (thePlayer != null) {
                lastWorld = thePlayer.worldObj;
            } else {
                lastWorld = null;
            }
        }

        if (thePlayer != null && thePlayer.isEntityAlive() && !DynamicLights.globalLightsOff()) {
            boolean isOpen = false;
            for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
                if (e.getValue().isOnline() && e.getValue().getLEDOn() && e.getValue().dynamicLight()) {
                    isOpen = true;
                    break;
                }
            }
            if (isOpen) {
                enableLight();
                lightLevel = 15;
            } else {
                disableLight();
                lightLevel = 0;
            }
        }
    }


    private void enableLight() {
        DynamicLights.addLightSource(this);
        enabled = true;
    }

    private void disableLight() {
        if (!fmlOverrideEnable) {
            DynamicLights.removeLightSource(this);
            enabled = false;
        }
    }

    @Override
    public Entity getAttachmentEntity() {
        return thePlayer;
    }

    @Override
    public int getLightLevel() {
        return lightLevel;
    }

}
