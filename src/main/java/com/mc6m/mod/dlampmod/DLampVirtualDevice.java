package com.mc6m.mod.dlampmod;

import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.save.SetColorType;
import com.mclans.dlamplib.api.Device;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class DLampVirtualDevice {
    private String did;
    private String name;
    private Device device;
    private boolean isOnline = true;
    private boolean isMobTarget = true;  //怪物盯上
    private boolean isDynamicLight = false; // 动态光源
    private boolean isHealthWarning = true; // 低血量警告
    private boolean isDamageWarning = true;  // 被攻击警告
    private boolean isPickupNotice = true;  // 拾取物品通知
    private boolean isPickupEXPNotice = true; // 拾取经验通知
    private boolean isFishing = true; // 钓鱼通知
    private DLWorldSavedData dlwsd;
    private String color = "#ffffff";
    private boolean isLightOpen = false;
    private int defaultR = 0, defaultG = 0, defaultB = 0;

    public DLampVirtualDevice(World world, BlockPos pos, Device device, boolean isLightOpen) {
        String worldname = world.getSeed() + "";
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        this.isLightOpen = isLightOpen;
        String mac = device.getMac();
        this.name = "次元矿灯(" + mac.substring(mac.length() - 6) + ")";
        this.device = device;
        this.isOnline = true;
        this.dlwsd = DLWorldSavedData.get(world);
        this.did = device.getDid();
        // 如果数据中有此 did
        if (dlwsd.getDid2Pos().containsKey(did)) {
            Map map = dlwsd.getSetting(did);
            this.isMobTarget = (boolean) map.get("isMobTarget");
            this.isDynamicLight = (boolean) map.get("isDynamicLight");
            this.isHealthWarning = (boolean) map.get("isHealthWarning");
            this.isDamageWarning = (boolean) map.get("isDamageWarning");
            this.isPickupNotice = (boolean) map.get("isPickupNotice");
            this.isPickupEXPNotice = (boolean) map.get("isPickupEXPNotice");
            this.isFishing = (boolean) map.get("isFishing");
            this.color = (String) map.get("color");
        } else { // 没有则存储
            DLWorldSavedData.get(world).add(did, pos);
        }
    }

    private boolean isLightOpen() {
        return isLightOpen;
    }

    String getDid() {
        return did;
    }

    public void setLightOpen(boolean lightOpen) {
        isLightOpen = lightOpen;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public boolean dynamicLight() {
        return this.isDynamicLight;
    }

    public void setSetting(Map map) {
        this.isMobTarget = (boolean) map.get("isMobTarget");
        this.isDynamicLight = (boolean) map.get("isDynamicLight");
        this.isHealthWarning = (boolean) map.get("isHealthWarning");
        this.isDamageWarning = (boolean) map.get("isDamageWarning");
        this.isPickupNotice = (boolean) map.get("isPickupNotice");
        this.isPickupEXPNotice = (boolean) map.get("isPickupEXPNotice");
        this.isFishing = (boolean) map.get("isFishing");
        this.color = (String) map.get("color");
        dlwsd.addSetting(did, map);
    }

    public boolean getLEDOn() {
        return this.device.getDataPoint().isLamp_on();
    }

    public String getName() {
        return this.name;
    }

    // 设置默认颜色
    void setDefault(int r, int g, int b, SetColorType sct) {
        if (getIsOpen(sct)) {
            System.out.println("设置默认颜色:" + r + "," + g + "," + b);
            defaultR = r;
            defaultG = g;
            defaultB = b;
            colorSetRGB(r, g, b);
        }
    }

    // 设置颜色
    private void colorSetRGB(int r, int g, int b) {
        System.out.println("设置颜色:" + r + "," + g + "," + b);
        device.setRGB(r, g, b);
    }

    // 带权限的RGB
    void colorSetRGB(int r, int g, int b, SetColorType sct) {
        if (getIsOpen(sct)) {
            System.out.println("带权限的RGB:" + r + "," + g + "," + b);
            colorSetRGB(r, g, b);
        }
    }

    // 设置心跳
    void colorSetMonoHeartbeat(int r, int g, int b, int off2on_interval, SetColorType sct) {
        if (getIsOpen(sct)) {
            System.out.println("设置心跳:" + r + "," + g + "," + b);
            device.setMonoHeartbeat(new int[]{r, g, b}, off2on_interval);
        }
    }

    // 闪一下
    void colorBlink(int r, int g, int b, SetColorType sct) {
        if (getIsOpen(sct)) {
            System.out.println("闪一下:" + r + "," + g + "," + b);
            device.runBlink(new int[]{r, g, b}, 1, 500, 0);
//            device.setBlink(new int[]{r, g, b});
        }
    }

    // 呼吸
    void colorSetBLN(int r, int g, int b, SetColorType sct) {
        if (getIsOpen(sct)) {
            System.out.println("呼吸:" + r + "," + g + "," + b);
            device.setMonoBLN(new int[]{r, g, b}, 500, 500, 100);
        }
    }

    // 恢复默认
    void colorReset() {
        System.out.println("恢复默认");
        colorSetRGB(defaultR, defaultG, defaultB);
    }

    // 清除各种效果
    void colorClear() {
        System.out.println("清除各种效果");
        device.clearAll();
        defaultR = 0;
        defaultG = 0;
        defaultB = 0;
    }

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setTempRGB(int r, int g, int b, SetColorType sct) {
//        if (getIsOpen(sct)) {
//            if (sct == SetColorType.IS_MOB_TARGET && r == 0 && g == 0 && b == 0 && this.isLightOpen) {
//                r = Tools.scale16To10(this.color.substring(1, 3));
//                g = Tools.scale16To10(this.color.substring(3, 5));
//                b = Tools.scale16To10(this.color.substring(5, 7));
//            }
//            tempR = r;
//            tempG = g;
//            tempB = b;
//            setRGB(r, g, b);
//        }
//    }
//
//    public void connected() {
//        this.isOnline = true;
//
//    }
//
//    public void disconnected() {
//        this.isOnline = false;
//    }
//
//    public void setRGB(int r, int g, int b) {
//        device.setRGB(r, g, b);
//    }
//
//    public void flash(int r, int g, int b, int millisec, SetColorType sct) {
//        if (getIsOpen(sct)) {
//            if (flashing) {
//                timer.cancel();
//                timer = new Timer();
//            }
//            flashing = true;
//            device.setRGB(r, g, b);
//            TimerTask task = new TimerTask() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    device.setRGB(tempR, tempG, tempB);
//                    flashing = false;
//                }
//            };
//            timer.schedule(task, millisec);
//        }
//    }
//
//    public void timedFlash(final int r, final int g, final int b, int period, SetColorType sct) {
//        if (getIsOpen(sct)) {
//            stopTimedFlash();
//            timedflashing = true;
//            device.setRGB(0, 0, 0);
//            TimerTask task = new TimerTask() {
//                boolean on = false;
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    if (flashing) return;
//                    if (on) {
//
//                        device.setRGB(tempR, tempG, tempB);
//                        on = false;
//                    } else {
//                        device.setRGB(r, g, b);
//                        on = true;
//                    }
//                }
//
//            };
//            timedtimer.schedule(task, 0, period);
//        }
//    }
//
//    public void stopTimedFlash() {
//        timedflashing = false;
//        timedtimer.cancel();
//        setRGB(tempR, tempG, tempB);
//        timedtimer = new Timer();
//    }
//
//    public void reset() {
//        stopTimedFlash();
//        tempR = 0;
//        tempG = 0;
//        tempB = 0;
//        setRGB(0, 0, 0);
//    }

    private boolean getIsOpen(SetColorType sct) {
        boolean isOpen = false;
        switch (sct) {
            case IS_MOB_TARGET:
                isOpen = this.isMobTarget;
                break;
            case IS_PICKUP_EXP_NPTICE:
                isOpen = this.isPickupEXPNotice;
                break;
            case IS_PICKUP_NOTICE:
                isOpen = this.isPickupNotice;
                break;
            case IS_DAMAGE_WARNING:
                isOpen = this.isDamageWarning;
                break;
            case IS_DYNAMEIC_LIGHT:
                isOpen = this.isDynamicLight;
                break;
            case IS_HEALTH_WARNING:
                isOpen = this.isHealthWarning;
                break;
            case IS_FINSHING:
                isOpen = this.isFishing;
                break;
            case FINAL_TRUE:
                isOpen = true;
                break;
            case FINAL_FALSE:
                isOpen = false;
                break;
        }
        return isOpen;
    }
}
