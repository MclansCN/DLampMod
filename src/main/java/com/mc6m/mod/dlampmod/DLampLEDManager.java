package com.mc6m.mod.dlampmod;

import com.mc6m.mod.dlampmod.gui.DLampBindingGUI;
import com.mc6m.mod.dlampmod.gui.DLampSettingGUI;
import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.save.SetColorType;
import com.mc6m.mod.dlampmod.tools.Tools;
import com.mclans.dlamplib.api.DLampAPI;
import com.mclans.dlamplib.api.Device;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public void onPlayerInteract(final PlayerInteractEvent.RightClickBlock event) {
        String blockname = event.getWorld().getBlockState(event.getPos()).getBlock().getRegistryName().toString();
        if (event.getHand().equals(EnumHand.OFF_HAND)
                && event.getSide().equals(Side.CLIENT)
                && (blockname.equalsIgnoreCase(DLampMOD.dBlock.getRegistryName().toString()) || blockname.equalsIgnoreCase(DLampMOD.lit_dBlock.getRegistryName().toString()))) {
            event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
            Timer timerswing = new Timer();
            timerswing.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (dlwsd.getPos2did().containsKey(event.getPos())) {
                        DLampSettingGUI dlsgui = new DLampSettingGUI(Minecraft.getMinecraft().currentScreen, event.getPos(), world, (Device) DLampAPI.getDeviceList().get(dlwsd.getPos2did().get(event.getPos())));
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
        if (event.getState() != null) {
            // 如果是大佬灯方块
            if (event.getState().toString().equalsIgnoreCase("dlampmod:dLamp") || event.getState().toString().equalsIgnoreCase("dlampmod:lit_dLamp")) {
                // 查找表里是否有该位置的数据,如果有则删除对应的绑定
                DLWorldSavedData dlwsd = DLWorldSavedData.get(world);
                BlockPos pos = event.getPos();
                String did = dlwsd.getPos2did().get(pos);
                if (did != null) {
                    dlwsd.removeSetting(did);
                    dlwsd.remove(pos);
                    DLampMOD.virtualdevicemap.remove(did);
                    event.getPlayer().addChatMessage(new TextComponentString("§f【§3次元矿灯§f】§c数据已被清理，若要继续使用其功能，请重新绑定"));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /**方块放置*/
    public void onBreakEvent(BlockEvent.PlaceEvent event) {
        if (event.getState() != null) {
            // 如果是大佬灯方块
            if (event.getState().toString().equalsIgnoreCase("dlampmod:dLamp") || event.getState().toString().equalsIgnoreCase("dlampmod:lit_dLamp")) {
                event.getPlayer().addChatMessage(new TextComponentString("§e 右击§f【§3次元矿灯§f】§e方块绑定现实中的设备。"));
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLoadWorldEvent(WorldEvent.Load event) {
        World world = event.getWorld();
        if (this.world == null || world.getSeed() != 0) {
            this.world = event.getWorld();
            dlwsd = DLWorldSavedData.get(world);
            Map<BlockPos, String> pos2did = dlwsd.getPos2did();
            DLampMOD.virtualdevicemap.clear();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (Map.Entry<BlockPos, String> entry : pos2did.entrySet()) {
                        BlockPos pos = entry.getKey();
                        String did = entry.getValue();
                        IBlockState block = world.getBlockState(pos);
//                        Device device = DLampMOD.api.getDevice(did);
                        Device device = (Device) DLampAPI.getDeviceList().get(did);
                        boolean ishave = false;
                        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
                            if (e.getValue().getDid().equals(did)) {
                                ishave = true;
                            }
                        }
                        if (device != null && !ishave) {
                            if (block.toString().equalsIgnoreCase("dlampmod:dLamp")) {
                                DLampVirtualDevice dlvd = new DLampVirtualDevice(world, pos, device, false);
                                dlvd.colorClear();
                                DLampMOD.virtualdevicemap.put(did, dlvd);
                            } else if (block.toString().equalsIgnoreCase("dlampmod:lit_dLamp")) {
                                DLampVirtualDevice dlvd = new DLampVirtualDevice(world, pos, device, true);
                                DLampMOD.virtualdevicemap.put(did, dlvd);

                                // 设置默认颜色
                                Map settingMap = dlwsd.getSetting(dlwsd.getPos2did().get(pos));
                                if (settingMap != null) {
                                    String colorStr = (String) settingMap.get("color");
                                    int r = Tools.scale16To10(colorStr.substring(1, 3));
                                    int g = Tools.scale16To10(colorStr.substring(3, 5));
                                    int b = Tools.scale16To10(colorStr.substring(5, 7));
                                    dlvd.setDefault(r, g, b, SetColorType.FINAL_TRUE);
                                }
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
//                            Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("§f【§b次元矿灯MOD§f】§c有更新，请到 " + DLampMOD.newVersionHomepage + " 下载更新！"));
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("§f【§b次元矿灯MOD§f】§c有更新，请点击§e此处§c下载更新！").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DLampMOD.newVersionHomepage))));
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
        if (event.getTarget() != null
                && event.getTarget() instanceof EntityPlayer
                && localplayer.equals(event.getTarget())) {

            if (!isTarget) {
                isTarget = true;
                multiHeartbeat(255, 0, 0, 500, SetColorType.IS_MOB_TARGET);
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        isTarget = false;
                        multiReset();
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
                        multiReset();
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
        if (event.getEntityLiving() instanceof EntityPlayer) {
            multiBlink(255, 0, 0, 500, SetColorType.IS_DAMAGE_WARNING);
            float percent = (event.getEntityLiving().getHealth() - event.getAmount()) / event.getEntityLiving().getMaxHealth();
            bloodWarn(percent);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 低血量*/
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            float percent = event.getEntityLiving().getHealth() / event.getEntityLiving().getMaxHealth();
            bloodWarn(percent);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 重生 */
    public void onLivingSpawn(PlayerSetSpawnEvent event) {
        multiReset();
    }

    private void bloodWarn(float percent) {
        if (percent <= 0.33) {
            if (1 != healthlevel) {
                healthlevel = 1;
                multiBLN(255, 0, 0, SetColorType.IS_HEALTH_WARNING);
            }
        } else if (percent <= 0.67) {
            if (2 != healthlevel) {
                healthlevel = 2;
                multiBLN(255, 255, 0, SetColorType.IS_HEALTH_WARNING);
            }

        } else {
            if (3 != healthlevel) {
                healthlevel = 3;
                multiReset();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** GG */
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            multiRGB(255, 255, 255, SetColorType.FINAL_TRUE);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 获得经验 */
    public void onPlayerPickupXp(PlayerPickupXpEvent event) {
        if (event.getEntityPlayer() != null) {
            multiBlink(0, 0, 255, 100, SetColorType.IS_PICKUP_EXP_NPTICE);
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 获得物品 */
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (event.getEntityPlayer() != null) {
            multiBlink(255, 255, 0, 100, SetColorType.IS_PICKUP_NOTICE);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    /** 钓鱼 */
    public void onClientTick(ClientTickEvent event) {
        EntityPlayer localplayer = Minecraft.getMinecraft().thePlayer;
        if (localplayer == null) return;
        if (localplayer.fishEntity == null) return;
        if (localplayer.fishEntity.motionX == 0.0D && localplayer.fishEntity.motionZ == 0.0D && localplayer.fishEntity.motionY < -0.02D) {
            multiBlink(0, 255, 0, 500, SetColorType.IS_FINSHING);
        }
    }

    // 闪一下
    private void multiBlink(int r, int g, int b, int millisec, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorBlink(r, g, b, sct);
            }
        }
    }

    // 设置颜色
    private void multiRGB(int r, int g, int b, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorSetRGB(r, g, b, sct);
            }
        }
    }

    // 呼吸
    private void multiBLN(int r, int g, int b, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorSetBLN(r, g, b, sct);
            }
        }
    }

    // 心跳
    private void multiHeartbeat(int r, int g, int b, int off2on_interval, SetColorType sct) {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorSetMonoHeartbeat(r, g, b, off2on_interval, sct);
            }
        }
    }

    // 恢复默认
    private void multiReset() {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorReset();
            }
        }
    }

    // 清理数据
    private void multiClear() {
        for (Entry<String, DLampVirtualDevice> e : DLampMOD.virtualdevicemap.entrySet()) {
            if (e.getValue().isOnline()) {
                e.getValue().colorClear();
            }
        }
    }

}
