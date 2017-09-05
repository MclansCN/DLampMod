package com.mc6m.mod.dlampmod.tools;

import com.google.common.base.Objects;
import net.minecraft.util.MathHelper;

public class Vec3i implements Comparable {
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
        this.x = p_i46007_1_;
        this.y = p_i46007_2_;
        this.z = p_i46007_3_;
    }

    public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
        this(MathHelper.floor_double(p_i46008_1_), MathHelper.floor_double(p_i46008_3_), MathHelper.floor_double(p_i46008_5_));
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        } else {
            Vec3i var2 = (Vec3i) p_equals_1_;
            if (this.getX() != var2.getX()) {
                return false;
            } else if (this.getY() != var2.getY()) {
                return false;
            } else {
                return this.getZ() == var2.getZ();
            }
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(Vec3i p_compareTo_1_) {
        if (this.getY() == p_compareTo_1_.getY()) {
            return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
        } else {
            return this.getY() - p_compareTo_1_.getY();
        }
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
        return new Vec3i(this.getY() * p_crossProduct_1_.getZ() - this.getZ() * p_crossProduct_1_.getY(), this.getZ() * p_crossProduct_1_.getX() - this.getX() * p_crossProduct_1_.getZ(), this.getX() * p_crossProduct_1_.getY() - this.getY() * p_crossProduct_1_.getX());
    }

    public double distanceSq(double p_distanceSq_1_, double p_distanceSq_3_, double p_distanceSq_5_) {
        double var7 = (double) this.getX() - p_distanceSq_1_;
        double var9 = (double) this.getY() - p_distanceSq_3_;
        double var11 = (double) this.getZ() - p_distanceSq_5_;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double distanceSqToCenter(double p_distanceSqToCenter_1_, double p_distanceSqToCenter_3_, double p_distanceSqToCenter_5_) {
        double var7 = (double) this.getX() + 0.5D - p_distanceSqToCenter_1_;
        double var9 = (double) this.getY() + 0.5D - p_distanceSqToCenter_3_;
        double var11 = (double) this.getZ() + 0.5D - p_distanceSqToCenter_5_;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public double distanceSq(Vec3i p_distanceSq_1_) {
        return this.distanceSq((double) p_distanceSq_1_.getX(), (double) p_distanceSq_1_.getY(), (double) p_distanceSq_1_.getZ());
    }

    public String toString() {
        return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public int compareTo(Object p_compareTo_1_) {
        return this.compareTo((Vec3i) p_compareTo_1_);
    }
}
