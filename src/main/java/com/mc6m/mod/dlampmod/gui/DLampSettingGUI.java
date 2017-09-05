package com.mc6m.mod.dlampmod.gui;

import cn.zhhl.DLUtil.Device;
import com.mc6m.mod.dlampmod.DLampMOD;
import com.mc6m.mod.dlampmod.save.DLWorldSavedData;
import com.mc6m.mod.dlampmod.tools.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

public class DLampSettingGUI extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiLabel title;
    private GuiButton btnClose;
    private GuiButton mobTargetBtn;
    private GuiButton damageWarningBtn;
    private GuiButton healthWarningBtn;
    private GuiButton dynamicLightBtn;
    private GuiButton pickupNoticeBtn;
    private GuiButton pickupEXPNoticeBtn;
    private GuiButton fishingBtn;
    private GuiTextField setColorText;
    private BlockPos pos;
    private Map settingMap;
    private DLWorldSavedData dlwsd;
    private boolean initOK = false;
    private Device device;

    public DLampSettingGUI(GuiScreen parent, PlayerInteractEvent event, World world, Device device) {
        parentScreen = parent; //记下是哪个界面打开了它,以便以后返回那个界面
        //在这里初始化与界面无关的数据,始化一次的数据.
        this.pos = new BlockPos(event.x, event.y, event.z);
        this.dlwsd = DLWorldSavedData.get(world);
        this.settingMap = dlwsd.getSetting(dlwsd.getPos2did().get(pos));
        this.device = device;
    }

    public void initGui() {
        //每当界面被打开时调用
        //这里部署控件
        this.buttonList.add(btnClose = new GuiButton(0, (int) (width * 0.5) - 40, (int) (height * 0.85), 80, 20, "保存"));
        int i = 20;
        this.buttonList.add(mobTargetBtn = new GuiButton(this.buttonList.size(), this.width / 2 - 150, (int) (this.height * 0.1 + i), 140, 20, "发现怪物警告：" + getBtnName("isMobTarget")));
        this.buttonList.add(dynamicLightBtn = new GuiButton(this.buttonList.size(), this.width / 2 + 10, (int) (this.height * 0.1 + i), 140, 20, "动态光源：" + getBtnName("isDynamicLight")));
        i += 30;
        this.buttonList.add(damageWarningBtn = new GuiButton(this.buttonList.size(), this.width / 2 - 150, (int) (this.height * 0.1 + i), 140, 20, "被攻击警告：" + getBtnName("isDamageWarning")));
        this.buttonList.add(pickupNoticeBtn = new GuiButton(this.buttonList.size(), this.width / 2 + 10, (int) (this.height * 0.1 + i), 140, 20, "拾取物品通知：" + getBtnName("isPickupNotice")));
        i += 30;
        this.buttonList.add(healthWarningBtn = new GuiButton(this.buttonList.size(), this.width / 2 - 150, (int) (this.height * 0.1 + i), 140, 20, "低血量警告：" + getBtnName("isHealthWarning")));
        this.buttonList.add(pickupEXPNoticeBtn = new GuiButton(this.buttonList.size(), this.width / 2 + 10, (int) (this.height * 0.1 + i), 140, 20, "拾取经验通知：" + getBtnName("isPickupEXPNotice")));
        i += 30;
        this.buttonList.add(fishingBtn = new GuiButton(this.buttonList.size(), this.width / 2 - 150, (int) (this.height * 0.1 + i), 140, 20, "钓鱼通知：" + getBtnName("isFishing")));

        i += 15;
        this.setColorText = new GuiTextField(fontRendererObj, this.width / 2 + 10, (int) (this.height * 0.1 + i), 140, 20);
        this.setColorText.setMaxStringLength(7);
        this.setColorText.setFocused(true);
        this.setColorText.setText((String) settingMap.get("color"));
        this.setColorText.setTextColor(0x00FF00);
        initOK = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false); //关闭键盘连续输入
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == btnClose) { // 退出
            mc.displayGuiScreen(parentScreen);
        } else if (button == mobTargetBtn) { // 钓鱼
            cutSetting("isMobTarget");
            button.displayString = "发现怪物警告：" + getBtnName("isMobTarget");
        } else if (button == damageWarningBtn) {
            cutSetting("isDamageWarning");
            button.displayString = "被攻击警告：" + getBtnName("isDamageWarning");
        } else if (button == healthWarningBtn) {
            cutSetting("isHealthWarning");
            button.displayString = "低血量警告：" + getBtnName("isHealthWarning");
        } else if (button == dynamicLightBtn) {
            cutSetting("isDynamicLight");
            button.displayString = "动态光源：" + getBtnName("isDynamicLight");
        } else if (button == pickupNoticeBtn) {
            cutSetting("isPickupNotice");
            button.displayString = "拾取物品通知：" + getBtnName("isPickupNotice");
        } else if (button == pickupEXPNoticeBtn) {
            cutSetting("isPickupEXPNotice");
            button.displayString = "拾取经验通知：" + getBtnName("isPickupEXPNotice");
        } else if (button == fishingBtn) {
            cutSetting("isFishing");
            button.displayString = "钓鱼通知：" + getBtnName("isFishing");
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        if (initOK) {
            drawDefaultBackground();
            drawRect((int) (width * 0.1), (int) (height * 0.1), (int) (width * 0.9), (int) (height * 0.8), 0x80FFFFFF);

            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会被控件(即按键)盖住.
            super.drawScreen(par1, par2, par3);

            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会盖在控件(即按键)之上.
            if (this.setColorText != null) {
                this.setColorText.drawTextBox();
                Minecraft.getMinecraft().fontRenderer.drawString("§l矿灯设置", this.width / 2 - 20, 10, 0xffffff);
                if (this.device != null) {
                    Minecraft.getMinecraft().fontRenderer.drawString("(设备在线)", this.width / 2 - 20, 30, 0x00ff00);
                } else {
                    Minecraft.getMinecraft().fontRenderer.drawString("(设备离线)", this.width / 2 - 20, 30, 0xff0000);
                }
                Minecraft.getMinecraft().fontRenderer.drawString("§0设置颜色(格式为#000000，值为0-f)", this.width / 2 + 10, (int) (this.height * 0.1 + 115), 0xffffff);

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.renderEngine.bindTexture(new ResourceLocation("dlampmod:logo.png"));
                this.drawTexturedModalRect(this.width / 2 - 150, (int) (this.height * 0.1 + 140), 0, 0, 75, 25);
            } else {
                System.out.println("setColorText 初始化失败");
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null)
                this.mc.setIngameFocus();
        }
        this.setColorText.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        if (initOK) {
            this.setColorText.updateCursorCounter();
            // 验证颜色格式是否正确
            String str = this.setColorText.getText();
            if (str.length() == 7) {
                String regEx = "^#([0-9a-fA-F]{6})$";
                if (str.matches(regEx)) {
                    this.setColorText.setTextColor(0x00FF00);
                    if (!((String) settingMap.get("color")).equalsIgnoreCase(str)) {
                        settingMap.put("color", str);
                        DLampMOD.virtualdevicemap.get(dlwsd.getPos2did().get(pos)).setSetting(settingMap);
                    }
                }
            } else {
                this.setColorText.setTextColor(0xFF0000);
            }
        }
    }

    // 根据 key 获取开关文字
    private String getBtnName(String name) {
        return (Boolean) settingMap.get(name) ? "开" : "关";
    }

    private void cutSetting(String name) {
        settingMap.put(name, (Boolean) settingMap.get(name) ? false : true);
//        dlwsd.addSetting(dlwsd.getPos2did().get(pos), settingMap);
        DLampMOD.virtualdevicemap.get(dlwsd.getPos2did().get(pos)).setSetting(settingMap);
    }

}