package com.mc6m.mod.dlampmod;

import cn.zhhl.DLUtil.api.DimensionLamp;
import com.mc6m.mod.dlampmod.tools.Tools;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

@Mod(modid = DLampMOD.MOD_ID, name = DLampMOD.MOD_NAME, version = DLampMOD.MOD_VERSION, acceptedMinecraftVersions = "[1.7.10]")
public class DLampMOD {
    public static final String MOD_ID = "dlampmod";
    public static final String MOD_NAME = "Dimesion Lamp";
    public static final String MOD_VERSION = "1.0.0";
    public static final String updateJSON = "http://dlamp.mc6m.com/ModUpdate-LowMCVersion.json";

    public static String dLampName = "dLamp";
    public static DLampBlock dBlock;
    public static DLampBlock lit_dBlock;
    public static DimensionLamp api = new DimensionLamp();
    public static boolean needUpdate = false;
    public static String newVersionHomepage = "";

    public static ConcurrentHashMap<String, DLampVirtualDevice> virtualdevicemap = new ConcurrentHashMap<String, DLampVirtualDevice>();

    //    @Mod.EventHandler
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        dBlock = new DLampBlock(false);
        dBlock.setBlockName(DLampMOD.MOD_ID + ".dlamp");
        dBlock.setCreativeTab(CreativeTabs.tabRedstone);
        dBlock.setBlockTextureName("dl_off");
        lit_dBlock = new DLampBlock(true);
        lit_dBlock.setBlockName(DLampMOD.MOD_ID + ".lit_dlamp");
        lit_dBlock.setBlockTextureName("dl_on");
        GameRegistry.registerBlock(dBlock, dLampName);
        GameRegistry.registerBlock(lit_dBlock, "lit_dLamp");
        GameRegistry.addRecipe(new ItemStack(dBlock, 1), "#@#", "@X@", "#@#", '@', new ItemStack(Blocks.stained_glass_pane, 1, 14), 'X', new ItemStack(Blocks.redstone_torch), '#', new ItemStack(Items.redstone));

        // 更新
        try {
            String newVersionStr = Tools.loadURLJson(updateJSON);
            JSONObject newVersionJson = new JSONObject(newVersionStr);
            String newVersion = newVersionJson.getJSONObject("promos").getString("1.7.10-recommended");
            needUpdate = Tools.versionCompare(MOD_VERSION, newVersion);
            newVersionHomepage = newVersionJson.getString("homepage");
        } catch (Exception e) {
            System.out.println(e);
        }

        MinecraftForge.EVENT_BUS.register(new DLampLEDManager());
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
//		DynamicLights.instance.load(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

//	@SubscribeEvent
//	public void keyListener(KeyInputEvent event) {
//	    if (Keyboard.getEventKey() == Keyboard.KEY_K) //获取按下的按键并判断
//	        {
//	        Minecraft mc = Minecraft.getMinecraft();
//	        mc.displayGuiScreen(new DLampGUI(mc.currentScreen));
//	        }
//	}

}
