package com.mc6m.mod.dlampmod;

import cn.zhhl.DLUtil.api.DimensionLamp;
import com.mc6m.mod.dlampmod.tools.Tools;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

@Mod(modid = DLampMOD.MOD_ID, name = DLampMOD.MOD_NAME, version = DLampMOD.MOD_VERSION, updateJSON = DLampMOD.updateJSON, acceptedMinecraftVersions = "[1.11.2]")
public class DLampMOD {
    public static final String MOD_ID = "dlampmod";
    public static final String MOD_NAME = "Dimesion Lamp";
    public static final String MOD_VERSION = "1.0.0";
    public static final String updateJSON = "http://dlamp.mc6m.com/ModUpdate-HighMCVersion.json";

    public static String dLampName = "dLamp";
    public static DLampBlock dBlock;
    public static DLampBlock lit_dBlock;
    public static DimensionLamp api = new DimensionLamp();
    public static boolean needUpdate = false;
    public static String newVersionHomepage = "";

    public static ConcurrentHashMap<String, DLampVirtualDevice> virtualdevicemap = new ConcurrentHashMap<String, DLampVirtualDevice>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        dBlock = new DLampBlock(false);
        dBlock.setUnlocalizedName(DLampMOD.MOD_ID + ".dlamp");
        dBlock.setRegistryName(DLampMOD.MOD_ID, dLampName);
        dBlock.setCreativeTab(CreativeTabs.REDSTONE);

        lit_dBlock = new DLampBlock(true);
        lit_dBlock.setUnlocalizedName(DLampMOD.MOD_ID + ".lit_dlamp");
        lit_dBlock.setRegistryName(DLampMOD.MOD_ID, "lit_dlamp");

        GameRegistry.register(dBlock);
        GameRegistry.register(lit_dBlock);
        GameRegistry.register(new ItemBlock(dBlock).setRegistryName(DLampMOD.MOD_ID, dLampName));
        GameRegistry.addRecipe(new ItemStack(dBlock, 1), "#@#", "@X@", "#@#", '@', new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14), 'X', new ItemStack(Blocks.REDSTONE_TORCH), '#', new ItemStack(Items.REDSTONE));

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(dBlock), 0, new ModelResourceLocation(DLampMOD.MOD_ID + ":" + dLampName, "inventory"));
        // 更新
        try {
            String newVersionStr = Tools.loadURLJson(updateJSON);
            JSONObject newVersionJson = new JSONObject(newVersionStr);
            String newVersion = newVersionJson.getJSONObject("promos").getString("1.11.2-recommended");
            needUpdate = Tools.versionCompare(MOD_VERSION, newVersion);
            newVersionHomepage = newVersionJson.getString("homepage");
        } catch (Exception e) {
            System.out.println(e);
        }
        MinecraftForge.EVENT_BUS.register(new DLampLEDManager());
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
//		DynamicLights.instance.load(event);
    }

    @EventHandler
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
