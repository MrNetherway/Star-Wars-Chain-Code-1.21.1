package net.netherway.starwarschaincode.client;

import net.minecraft.world.phys.Vec3;
import net.netherway.starwarschaincode.item.client.WeaponItemRenderer;

public class MuzzlePosition {

    public static Vec3 getOffset() {

        return WeaponItemRenderer.getMuzzlePosition();

    }
}