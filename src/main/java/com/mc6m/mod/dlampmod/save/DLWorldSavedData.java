package com.mc6m.mod.dlampmod.save;

import com.mc6m.mod.dlampmod.DLampMOD;
import com.mc6m.mod.dlampmod.tools.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yupeg.LV on 17/8/1.
 */
public class DLWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = DLampMOD.MOD_ID + "_Data";
    private Map<String, BlockPos> did2pos = new HashMap();
    private Map<BlockPos, String> pos2did = new HashMap();
    private ConcurrentHashMap<String, Map> setting = new ConcurrentHashMap();

    public DLWorldSavedData() {
        super(DATA_NAME);
    }

    public DLWorldSavedData(String identifier) {
        super(identifier);
    }

    public void add(String did, BlockPos pos) {
        did2pos.put(did, pos);
        pos2did.put(pos, did);
        if (setting.get(did) == null) {
            Map map = new HashMap();
            map.put("isMobTarget", true);
            map.put("isDynamicLight", false);
            map.put("isHealthWarning", true);
            map.put("isDamageWarning", true);
            map.put("isPickupNotice", true);
            map.put("isPickupEXPNotice", true);
            map.put("isFishing", true);
            map.put("color", "#FFFFFF");
            setting.put(did, map);
        }
        markDirty();
    }

    public void remove(BlockPos pos) {
        if (pos2did.get(pos) != null) {
            did2pos.remove(pos2did.get(pos));
            pos2did.remove(pos);
            markDirty();
        }
    }

    public void addSetting(String did, Map map) {
        setting.put(did, map);
        markDirty();
    }

    public void removeSetting(String did) {
        if (setting.get(did) != null) {
            setting.remove(did);
        }
    }

    public Map getSetting(String did) {
        return setting.get(did);
    }

    public Map<String, BlockPos> getDid2Pos() {
        return did2pos;
    }

    public Map<BlockPos, String> getPos2did() {
        return pos2did;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        did2pos.clear();
        pos2did.clear();
        System.out.println("++++++++++++++++++++++++++++++++++++++++  Read-NBT-Start");
        Set<String> set = nbt.func_150296_c();
        for (String did : set) {
            NBTTagList list = (NBTTagList) nbt.getTag(did);
            if (list == null) {
                list = new NBTTagList();
            }
            for (int i = list.tagCount() - 1; i >= 0; --i) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                double x = compound.getDouble("x");
                double y = compound.getDouble("y");
                double z = compound.getDouble("z");
                BlockPos bp = new BlockPos(x, y, z);
                Map map = new HashMap();
                map.put("isMobTarget", compound.getBoolean("isMobTarget"));
                map.put("isDynamicLight", compound.getBoolean("isDynamicLight"));
                map.put("isHealthWarning", compound.getBoolean("isHealthWarning"));
                map.put("isDamageWarning", compound.getBoolean("isDamageWarning"));
                map.put("isPickupNotice", compound.getBoolean("isPickupNotice"));
                map.put("isPickupEXPNotice", compound.getBoolean("isPickupEXPNotice"));
                map.put("isFishing", compound.getBoolean("isFishing"));
                map.put("color", compound.getString("color"));
                setting.put(did, map);
                did2pos.put(did, bp);
                pos2did.put(bp, did);
            }
        }
        System.out.println(did2pos);
        System.out.println("++++++++++++++++++++++++++++++++++++++++  Read-NBT-End");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++  Write-NBT-Start");
        for (String did : did2pos.keySet()) {
            NBTTagList list = new NBTTagList();
            NBTTagCompound compound = new NBTTagCompound();
            compound.setDouble("x", did2pos.get(did).getX());
            compound.setDouble("y", did2pos.get(did).getY());
            compound.setDouble("z", did2pos.get(did).getZ());
//            if (setting.get(did) == null) {
//                Map map = new HashMap();
//                map.put("FishingNotice", true);
//                map.put("Feebleness", true);
//                map.put("Monster", true);
//                map.put("color", "#FFFFFF");
//                setting.put(did, map);
//            }
            Map map = setting.get(did);
            compound.setBoolean("isMobTarget", (Boolean) map.get("isMobTarget"));
            compound.setBoolean("isDynamicLight", (Boolean) map.get("isDynamicLight"));
            compound.setBoolean("isHealthWarning", (Boolean) map.get("isHealthWarning"));
            compound.setBoolean("isDamageWarning", (Boolean) map.get("isDamageWarning"));
            compound.setBoolean("isPickupNotice", (Boolean) map.get("isPickupNotice"));
            compound.setBoolean("isPickupEXPNotice", (Boolean) map.get("isPickupEXPNotice"));
            compound.setBoolean("isFishing", (Boolean) map.get("isFishing"));
            compound.setString("color", (String) map.get("color"));

            list.appendTag(compound);
            nbt.setTag(did, list);
        }
        System.out.println(did2pos);
        System.out.println("++++++++++++++++++++++++++++++++++++++++  Write-NBT-End");
    }

    public static DLWorldSavedData get(World world) {
//        MapStorage storage = world.getPerWorldStorage();
        MapStorage storage = world.perWorldStorage;
        DLWorldSavedData instance = (DLWorldSavedData) storage.loadData(DLWorldSavedData.class, DATA_NAME);
        if (instance == null) {
            instance = new DLWorldSavedData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

//    public static DLWorldSavedData get(World world) {
//        WorldSavedData data = world.getMapStorage().getOrLoadData(DLWorldSavedData.class, DATA_NAME);
//        if (data == null) {
//            data = new DLWorldSavedData(DATA_NAME);
//            world.getMapStorage().setData(DATA_NAME, data);
//        }
//        return (DLWorldSavedData) data;
//    }

}
