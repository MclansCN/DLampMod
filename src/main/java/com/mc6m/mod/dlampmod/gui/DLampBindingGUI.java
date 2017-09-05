package com.mc6m.mod.dlampmod.gui;

import com.mc6m.mod.dlampmod.DLampMOD;
import com.mc6m.mod.dlampmod.DLampVirtualDevice;
import com.mc6m.mod.dlampmod.tools.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import sun.awt.GlobalCursorManager;

import java.util.concurrent.ConcurrentHashMap;

public class DLampBindingGUI extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton btnClose;
    private BlockPos pos;
    private ConcurrentHashMap<Integer, String> devicebuttonmap = new ConcurrentHashMap<Integer, String>();
    private World world;
    private EntityPlayer player;
    private boolean initOK = false;

    public DLampBindingGUI(GuiScreen parent, PlayerInteractEvent event, World world) {
        parentScreen = parent; //记下是哪个界面打开了它,以便以后返回那个界面
        //在这里初始化与界面无关的数据,或者是只需初始化一次的数据.
        this.pos = new BlockPos(event.x, event.y, event.z);
        this.player = event.entityPlayer;
        this.world = world;
    }

    public void initGui() {
        //每当界面被打开时调用
        //这里部署控件
        this.buttonList.add(btnClose = new GuiButton(0, (int) (width * 0.5) - 40, (int) (height * 0.85), 80, 20, "关闭"));

        int i = 20;
        for (Object dido : DLampMOD.api.getDeviceList()) {
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
                String mac = DLampMOD.api.getDevice(did).getMac();
                btnname = "次元矿灯(" + mac.substring(mac.length() - 6) + ")";
                this.buttonList.add(new GuiButton(this.buttonList.size(), (int) (this.width * 0.15), (int) (this.height * 0.1 + i), 140, 20, btnname));
            }

            i += 30;
        }
        initOK = true;
    }

    //    public void displayQR(BufferedImage image) {
//    	GuiLabel lb;
//    	this.labelList.add(lb = new GuiLabel(fontRendererObj, 1, this.width / 2 - 20, this.height / 2 + 40, 300, 20, 0xFFFFFF));
//    	ResourceLocation resourceLocation = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("preivew", new DynamicTexture(image));
//    	updateLight();
//    	lb.addLine("微信扫码选择要绑定的次元矿灯。。。");
//    	Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
//    	drawModalRectWithCustomSizedTexture((int)(width*0.5)-256, (int) (height*0.5 -256),0,0,512,512, 128, 128);
//    }
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
//        IBlockState block = world.getBlockState(pos);
        String blockName = world.getBlock(pos.getX(), pos.getY(), pos.getZ()).getUnlocalizedName();
        if (blockName.equalsIgnoreCase("tile.dlampmod.dlamp")) {
            DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, DLampMOD.api.getDevice(did), false));
        } else if (blockName.equalsIgnoreCase("tile.dlampmod.lit_dlamp")) {
            DLampMOD.virtualdevicemap.put(did, new DLampVirtualDevice(world, pos, DLampMOD.api.getDevice(did), true));
        }

        player.addChatMessage(new ChatComponentText("§f【§b次元矿灯§f】§a绑定成功，再次右键矿灯方块可进行设置"));
        mc.displayGuiScreen(parentScreen);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        if (initOK){
            drawDefaultBackground();
            drawRect((int) (width * 0.1), (int) (height * 0.1), (int) (width * 0.9), (int) (height * 0.8), 0x80FFFFFF);

            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会被控件(即按键)盖住.
            super.drawScreen(par1, par2, par3);
            //在这里绘制文本或纹理等非控件内容,这里绘制的东西会盖在控件(即按键)之上.
            Minecraft.getMinecraft().fontRenderer.drawString("§l局域网内的矿灯", this.width / 2 - 30, 10, 0xffffff);
            Minecraft.getMinecraft().fontRenderer.drawString("(请保持设备打开，如设备已经打开仍无发现请重启设备后再试)", this.width / 2 - 120, 30, 0xff0000);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(new ResourceLocation("dlampmod:logo.png"));
            this.drawTexturedModalRect(this.width / 2 - 150, (int) (this.height * 0.1 + 140), 0, 0, 75, 25);
        }
    }
}