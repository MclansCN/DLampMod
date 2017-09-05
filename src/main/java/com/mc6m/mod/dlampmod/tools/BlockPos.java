package com.mc6m.mod.dlampmod.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockPos extends Vec3i {
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS;
    private static final int NUM_Y_BITS;
    private static final int Y_SHIFT;
    private static final int X_SHIFT;
    private static final long X_MASK;
    private static final long Y_MASK;
    private static final long Z_MASK;

    public BlockPos(int p_i46030_1_, int p_i46030_2_, int p_i46030_3_) {
        super(p_i46030_1_, p_i46030_2_, p_i46030_3_);
    }

    public BlockPos(double p_i46031_1_, double p_i46031_3_, double p_i46031_5_) {
        super(p_i46031_1_, p_i46031_3_, p_i46031_5_);
    }

    public BlockPos(Entity p_i46032_1_) {
        this(p_i46032_1_.posX, p_i46032_1_.posY, p_i46032_1_.posZ);
    }

    public BlockPos(Vec3 p_i46033_1_) {
        this(p_i46033_1_.xCoord, p_i46033_1_.yCoord, p_i46033_1_.zCoord);
    }

    public BlockPos(Vec3i p_i46034_1_) {
        this(p_i46034_1_.getX(), p_i46034_1_.getY(), p_i46034_1_.getZ());
    }

    public BlockPos add(double p_add_1_, double p_add_3_, double p_add_5_) {
        return new BlockPos((double) this.getX() + p_add_1_, (double) this.getY() + p_add_3_, (double) this.getZ() + p_add_5_);
    }

    public BlockPos add(int p_add_1_, int p_add_2_, int p_add_3_) {
        return new BlockPos(this.getX() + p_add_1_, this.getY() + p_add_2_, this.getZ() + p_add_3_);
    }

    public BlockPos add(Vec3i p_add_1_) {
        return new BlockPos(this.getX() + p_add_1_.getX(), this.getY() + p_add_1_.getY(), this.getZ() + p_add_1_.getZ());
    }

    public BlockPos multiply(int p_multiply_1_) {
        return new BlockPos(this.getX() * p_multiply_1_, this.getY() * p_multiply_1_, this.getZ() * p_multiply_1_);
    }

    public BlockPos up() {
        return this.up(1);
    }

    @SideOnly(Side.CLIENT)
    public BlockPos subtract(Vec3i p_subtract_1_) {
        return new BlockPos(this.getX() - p_subtract_1_.getX(), this.getY() - p_subtract_1_.getY(), this.getZ() - p_subtract_1_.getZ());
    }

    public BlockPos up(int p_up_1_) {
        return this.offset(EnumFacing.UP, p_up_1_);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int p_down_1_) {
        return this.offset(EnumFacing.DOWN, p_down_1_);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int p_north_1_) {
        return this.offset(EnumFacing.NORTH, p_north_1_);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int p_south_1_) {
        return this.offset(EnumFacing.SOUTH, p_south_1_);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int p_west_1_) {
        return this.offset(EnumFacing.WEST, p_west_1_);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int p_east_1_) {
        return this.offset(EnumFacing.EAST, p_east_1_);
    }

    public BlockPos offset(EnumFacing p_offset_1_) {
        return this.offset(p_offset_1_, 1);
    }

    public BlockPos offset(EnumFacing p_offset_1_, int p_offset_2_) {
        return new BlockPos(this.getX() + p_offset_1_.getFrontOffsetX() * p_offset_2_, this.getY() + p_offset_1_.getFrontOffsetY() * p_offset_2_, this.getZ() + p_offset_1_.getFrontOffsetZ() * p_offset_2_);
    }

    public BlockPos crossProductBP(Vec3i p_crossProductBP_1_) {
        return new BlockPos(this.getY() * p_crossProductBP_1_.getZ() - this.getZ() * p_crossProductBP_1_.getY(), this.getZ() * p_crossProductBP_1_.getX() - this.getX() * p_crossProductBP_1_.getZ(), this.getX() * p_crossProductBP_1_.getY() - this.getY() * p_crossProductBP_1_.getX());
    }

    public long toLong() {
        return ((long) this.getX() & X_MASK) << X_SHIFT | ((long) this.getY() & Y_MASK) << Y_SHIFT | ((long) this.getZ() & Z_MASK) << 0;
    }

    public static BlockPos fromLong(long p_fromLong_0_) {
        int var2 = (int) (p_fromLong_0_ << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int var3 = (int) (p_fromLong_0_ << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int var4 = (int) (p_fromLong_0_ << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(var2, var3, var4);
    }

    public Vec3i crossProduct(Vec3i p_crossProduct_1_) {
        return this.crossProductBP(p_crossProduct_1_);
    }

    static {
        NUM_Z_BITS = NUM_X_BITS;
        NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
        Y_SHIFT = 0 + NUM_Z_BITS;
        X_SHIFT = Y_SHIFT + NUM_Y_BITS;
        X_MASK = (1L << NUM_X_BITS) - 1L;
        Y_MASK = (1L << NUM_Y_BITS) - 1L;
        Z_MASK = (1L << NUM_Z_BITS) - 1L;
    }

    public static final class MutableBlockPos extends BlockPos {
        public int x;
        public int y;
        public int z;

        private MutableBlockPos(int p_i46024_1_, int p_i46024_2_, int p_i46024_3_) {
            super(0, 0, 0);
            this.x = p_i46024_1_;
            this.y = p_i46024_2_;
            this.z = p_i46024_3_;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public Vec3i crossProduct(Vec3i p_crossProduct_1_) {
            return super.crossProductBP(p_crossProduct_1_);
        }

        MutableBlockPos(int p_i46025_1_, int p_i46025_2_, int p_i46025_3_, Object p_i46025_4_) {
            this(p_i46025_1_, p_i46025_2_, p_i46025_3_);
        }
    }
}
