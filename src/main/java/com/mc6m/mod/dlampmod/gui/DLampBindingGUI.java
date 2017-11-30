package com.mc6m.mod.dlampmod.gui;

import com.mc6m.mod.dlampmod.DLampMOD;
import com.mc6m.mod.dlampmod.DLampVirtualDevice;
import com.mclans.dlamplib.api.DLampAPI;
import com.mclans.dlamplib.api.Device;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ConcurrentHashMap;

public class DLampBindingGUI extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton btnClose;
    private BlockPos pos;
    private ConcurrentHashMap<Integer, String> devicebuttonmap = new ConcurrentHashMap<Integer, String>();
    private World world;
    private EntityPlayer player;
    private boolean initOK = false;

    public DLampBindingGUI(GuiScreen parent, PlayerInteractEvent.RightClickBlock event, World world) {
        parentScreen = parent; //记下是哪个界面打开了它,以便以后返回那个界面
        //在这里初始化与界面无关的数据,或者是只需初始化一次的数据.
        this.pos = event.getPos();
        this.player = event.getEntityPlayer();
        this.world = world;
    }

    public void initGui() {
        //每当界面被打开时调用
        //这里部署控件
        this.buttonList.add(btnClose = new GuiButton(0, (int) (width * 0.5) - 40, (int) (height * 0.85), 80, 20, "关闭"));
        this.labelList.add(new GuiLabel(fontRendererObj, 1, this.width / 2 - 30, (int) (this.height * 0.4 - 10), 300, 20, 0xFFFFFF));
        int i = 20;
        for (Object dido : DLampAPI.getDeviceList().keySet()) {
            String did = dido.toString();
            devicebuttonmap.put(this.buttonList.size(), did);
            String btnname;
            if (DLampMOD.virtualdevicemap.containsKey(did)) {
                DLampVirtualDevice device = DLampMOD.virtualdevicemap.get(did);
                btnname = "§e" + device.getName();
                GuiButton b;
                this.buttonList.add(b = new GuiButton(this.buttonList.size(), (int) (this.width * 0.15), (int) (this.height * 0.1 + i), 140, 20, btnname));
                b.enabled = false;
            } else {
                String mac = ((Device) DLampAPI.getDeviceList().get(did)).getMac();
                btnname = "次元矿灯(" + mac.substring(mac.length() - 6) + ")";
                this.buttonList.add(new GuiButton(this.buttonList.size(), (int) (this.width * 0.15), (int) (this.height * 0.1 + i), 140, 20, btnname));
            }

            i += 30;
        }
        initOK = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false); //关闭键盘连续输入
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == btnClose) {    //改成button.id==0也行
            mc.displayGuiScreen(parentScreen);
            return;
        }
        String did = devicebuttonmap.get(button.id);
        IBlockState block = world.getBlockState(pos);
        if (block.toString().equalsIgnoreCase("dlampmod:dLamp")) {
            DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, (Device) DLampAPI.getDeviceList().get(did), false));
        } else if (block.toString().equalsIgnoreCase("dlampmod:lit_dLamp")) {
            DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, (Device) DLampAPI.getDeviceList().get(did), true));
        }

        player.addChatMessage(new TextComponentString("§f【§b次元矿灯§f】§a绑定成功，再次右键矿灯方块可进行设置"));
        mc.displayGuiScreen(parentScreen);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        if (initOK) {
            drawDefaultBackground();
            drawRect((int) (width * 0.1), (int) (height * 0.1), (int) (width * 0.9), (int) (height * 0.8), 0x80FFFFFF);

            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会被控件(即按键)盖住.
            super.drawScreen(par1, par2, par3);
            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会盖在控件(即按键)之上.
            Minecraft.getMinecraft().fontRendererObj.drawString("§l局域网内的矿灯", this.width / 2 - 30, 10, 0xffffff);
            Minecraft.getMinecraft().fontRendererObj.drawString("(请保持设备打开，如设备已经打开仍无发现请重启设备后再试)", this.width / 2 - 120, 30, 0xff0000);

            GlStateManager.color(1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(new ResourceLocation("dlampmod:textures/logo.png"));
            this.setGuiSize(this.width, this.height);
            this.drawTexturedModalRect(this.width / 2 - 150, (int) (this.height * 0.1 + 140), 0, 0, 75, 25);

        }
    }
}