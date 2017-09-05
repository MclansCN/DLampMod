package com.mc6m.mod.dlampmod;

import cn.zhhl.DLUtil.Device;
import com.mc6m.mod.dlampmod.gui.DLampBindingGUI;
import com.mc6m.mod.dlampmod.gui.DLampSettingGUI;
import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.save.SetColorType;
import com.mc6m.mod.dlampmod.tools.BlockPos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;


public class DLampLEDManager {
    private World world;
    private boolean isTarget = false;
    private int healthlevel = 3;
    private Timer targettimer = new Timer();
    private DLWorldSavedData dlwsd;
    private Timer loadDeviceTimer;
    private boolean modUpdateInform = false;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 钓鱼 */
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer localplayer = Minecraft.getMinecraft().thePlayer;
        if (localplayer == null) return;
        if (localplayer.fishEntity == null) return;
        if ((localplayer.fishEntity != null) && (localplayer.fishEntity.motionX == 0.0D) && (localplayer.fishEntity.motionZ == 0.0D) && (localplayer.fishEntity.motionY < -0.02D)) {
            multiFlash(0, 255, 0, 500, SetColorType.IS_FINSHING);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPlayerInteract(final PlayerInteractEvent event) {
        String blockname = event.world.getBlock(event.x, event.y, event.z).getUnlocalizedName();
        if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
                && (blockname.equalsIgnoreCase("tile.dlampmod.dlamp") || blockname.equalsIgnoreCase("tile.dlampmod.lit_dlamp"))) {
            Timer timerswing = new Timer();
            timerswing.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    System.out.println(dlwsd.getPos2did());
                    if (dlwsd.getPos2did().containsKey(new BlockPos(event.x, event.y, event.z))) {
                        DLampSettingGUI dlsgui = new DLampSettingGUI(Minecraft.getMinecraft().currentScreen, event, world, DLampMOD.api.getDevice(dlwsd.getPos2did().get(new BlockPos(event.x, event.y, event.z))));
                        Minecraft.getMinecraft().displayGuiScreen(dlsgui);
                    } else {
                        DLampBindingGUI qrgui = new DLampBindingGUI(Minecraft.getMinecraft().currentScreen, event, world);
                        Minecraft.getMinecraft().displayGuiScreen(qrgui);
                    }
                }

            }, 300);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 方块被破坏*/
    public void onBreakEvent(BlockEvent.BreakEvent event) {
        String blockName = event.block.getUnlocalizedName();
        // 如果是大佬灯方块
        if (blockName.equalsIgnoreCase("tile.dlampmod.dlamp") || blockName.equalsIgnoreCase("tile.dlampmod.lit_dlamp")) {
            // 查找表里是否有该位置的数据,如果有则删除对应的绑定
            DLWorldSavedData dlwsd = DLWorldSavedData.get(world);
            BlockPos pos = new BlockPos(event.x, event.y, event.z);
            String did = dlwsd.getPos2did().get(pos);
            if (did != null) {
                dlwsd.removeSetting(did);
                dlwsd.remove(pos);
                DLampMOD.virtualdevicemap.remove(did);
                event.getPlayer().addChatMessage(new ChatComponentText("§f【§3次元矿灯§f】§c数据已被清理，若要继续使用其功能，请重新绑定"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 方块放置*/
    public void onBreakEvent(BlockEvent.PlaceEvent event) {
        String blockName = event.block.getUnlocalizedName();
        // 如果是大佬灯方块
        if (blockName.equalsIgnoreCase("tile.dlampmod.dlamp") || blockName.equalsIgnoreCase("tile.dlampmod.lit_dlamp")) {
            event.player.addChatMessage(new ChatComponentText("§e 右击§f【§3次元矿灯§f】§e方块绑定现实中的设备。"));
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLoadWorldEvent(WorldEvent.Load event) {
        World world = event.world;
        if (this.world == null || world.getSeed() != 0) {
            this.world = event.world;
            dlwsd = DLWorldSavedData.get(world);
            Map<BlockPos, String> pos2did = dlwsd.getPos2did();
            DLampMOD.virtualdevicemap.clear();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (Map.Entry<BlockPos, String> entry : pos2did.entrySet()) {
                        BlockPos pos = entry.getKey();
                        String did = entry.getValue();

                        String blockname2 = world.getBlock(pos.getX(), pos.getY(), pos.getZ()).getUnlocalizedName();
                        Device device = DLampMOD.api.getDevice(did);
                        if (device != null) {
                            if (blockname2.equalsIgnoreCase("tile.dlampmod.dlamp")) {
                                DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, device, false));
                            } else if (blockname2.equalsIgnoreCase("tile.dlampmod.lit_dlamp")) {
                                DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, device, true));
                            } else {
                                dlwsd.remove(pos);
                            }
                        }
                    }
                }
            };

            if (loadDeviceTimer != null) {
                loadDeviceTimer.cancel();
            }
            loadDeviceTimer = new Timer();
            loadDeviceTimer.scheduleAtFixedRate(task, 0, 1000);

            try {
                if (!modUpdateInform && DLampMOD.needUpdate) {
                    modUpdateInform = true;
                    Timer timerswing = new Timer();
                    timerswing.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§f【§b次元矿灯MOD§f】§c有更新，请到 " + DLampMOD.newVersionHomepage + " 下载更新！"));
                        }

                    }, 10000);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 被怪盯上*/
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        EntityPlayer localplayer = Minecraft.getMinecraft().thePlayer;
        if (event.target != null
                && event.target instanceof EntityPlayer
                && localplayer.equals(event.target)) {
            if (!isTarget) {
                isTarget = true;
                multiSetTempRGB(255, 0, 0, SetColorType.IS_MOB_TARGET);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        isTarget = false;
                        multiSetTempRGB(0, 0, 0, SetColorType.IS_MOB_TARGET);
                    }

                };

                targettimer.schedule(task, 500);
            } else {
                targettimer.cancel();
                targettimer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        isTarget = false;
                        multiSetTempRGB(0, 0, 0, SetColorType.IS_MOB_TARGET);
                    }

                };

                targettimer.schedule(task, 500);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 受伤*/
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            multiFlash(255, 255, 255, 100, SetColorType.IS_DAMAGE_WARNING);
            float percent = event.entityLiving.getHealth() / event.entityLiving.getMaxHealth();
            if (percent < 0.6 && percent > 0.2) {    // 2
                if (healthlevel != 2) {
                    healthlevel = 2;
                    for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
                        if (e.getValue().isOnline()) {
                            e.getValue().timedFlash(255, 128, 0, 400, SetColorType.IS_DAMAGE_WARNING);
                        }
                    }
                }
            } else if (percent < 0.2) {    // 1
                if (healthlevel != 1) {
                    healthlevel = 1;
                    for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
                        if (e.getValue().isOnline()) {
                            e.getValue().timedFlash(255, 0, 0, 300, SetColorType.IS_DAMAGE_WARNING);
                        }
                    }
                }
            } else                    // 3
            {
                if (healthlevel != 3) {
                    healthlevel = 3;
                    multiStopTimedFlash();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 低血量*/
    public void onLivingHeal(LivingHealEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            float percent = event.entityLiving.getHealth() / event.entityLiving.getMaxHealth();
            if (percent < 0.6 && percent > 0.2) {    // 2
                if (healthlevel != 2) {
                    healthlevel = 2;
                    multiTimedFlash(255, 128, 0, 400, SetColorType.IS_HEALTH_WARNING);
                }

            } else if (percent < 0.2) {    // 1
                if (healthlevel != 1) {
                    healthlevel = 1;
                    multiTimedFlash(255, 0, 0, 300, SetColorType.IS_HEALTH_WARNING);
                }
            } else                    // 3
            {
                if (healthlevel != 3) {
                    healthlevel = 3;
                    multiStopTimedFlash();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            multiReset();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 获得经验 */
    public void onPlayerPickupXp(PlayerPickupXpEvent event) {
        if (event.entityPlayer != null) {
            multiFlash(255, 255, 0, 100, SetColorType.IS_PICKUP_EXP_NPTICE);
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 获得物品 */
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (event.entityPlayer != null) {
            multiFlash(0, 255, 255, 100, SetColorType.IS_PICKUP_NOTICE);
        }
    }

    private void multiSetRGB(int r, int g, int b) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().setRGB(r, g, b);
            }
        }
    }

    private void multiFlash(int r, int g, int b, int millisec, SetColorType sct) {
        DLWorldSavedData dlwsd = DLWorldSavedData.get(world);
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().flash(r, g, b, millisec, sct);
            }
        }
    }

    private void multiTimedFlash(int r, int g, int b, int peroid, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().timedFlash(r, g, b, peroid, sct);
            }
        }
    }

    private void multiReset() {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().reset();
            }
        }
    }

    private void multiStopTimedFlash() {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().stopTimedFlash();
            }
        }
    }

    private void multiSetTempRGB(int r, int g, int b, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().setTempRGB(r, g, b, sct);
            }
        }
    }
}
