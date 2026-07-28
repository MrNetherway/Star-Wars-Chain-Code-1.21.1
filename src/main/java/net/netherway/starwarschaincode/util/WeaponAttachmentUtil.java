package net.netherway.starwarschaincode.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.component.WeaponAttachmentData;
import net.netherway.starwarschaincode.item.custom.BarrelAttachmentItem;
import net.netherway.starwarschaincode.item.custom.ScopeAttachmentItem;
import net.netherway.starwarschaincode.item.custom.StockAttachmentItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;

import org.jetbrains.annotations.Nullable;

public class WeaponAttachmentUtil {

    /**
     * Resolve o ScopeAttachmentItem instalado numa arma, ou null se não tiver scope.
     */
    @Nullable
    public static ScopeAttachmentItem getScope(ItemStack weaponStack) {
        return resolveAttachment(weaponStack, ModDataComponents.SCOPE_ITEM.get(), ScopeAttachmentItem.class);
    }

    /**
     * Resolve o StockAttachmentItem instalado numa arma, ou null se não tiver stock.
     */
    @Nullable
    public static StockAttachmentItem getStock(ItemStack weaponStack) {
        return resolveAttachment(weaponStack, ModDataComponents.STOCK_ITEM.get(), StockAttachmentItem.class);
    }

    /**
     * Resolve o BarrelAttachmentItem instalado numa arma, ou null se não tiver cano.
     */
    @Nullable
    public static BarrelAttachmentItem getBarrel(ItemStack weaponStack) {
        return resolveAttachment(weaponStack, ModDataComponents.BARREL_ITEM.get(), BarrelAttachmentItem.class);
    }

    /**
     * Damage final da arma já considerando o multiplicador do stock instalado (se tiver).
     * Se não tiver stock, retorna o damage base do WeaponItem sem alteração.
     */
    public static float getEffectiveDamage(ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof WeaponItem weapon)) {
            return 0f;
        }

        StockAttachmentItem stock = getStock(weaponStack);
        float multiplier = stock != null ? stock.getDamageMultiplier() : 1.0f;

        return weapon.getDamage() * multiplier;
    }

    /**
     * FireDistance final da arma já considerando o multiplicador do cano instalado (se tiver).
     * Se não tiver cano, retorna a fireDistance base do WeaponItem sem alteração.
     */
    public static float getEffectiveFireDistance(ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof WeaponItem weapon)) {
            return 0f;
        }

        BarrelAttachmentItem barrel = getBarrel(weaponStack);
        float multiplier = barrel != null ? barrel.getFireDistanceMultiplier() : 1.0f;

        return weapon.getFireDistance() * multiplier;
    }

    @Nullable
    private static <T> T resolveAttachment(ItemStack weaponStack,
                                           net.minecraft.core.component.DataComponentType<WeaponAttachmentData> componentType,
                                           Class<T> expectedType) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return null;
        }

        WeaponAttachmentData data = weaponStack.get(componentType);
        if (data == null) {
            return null;
        }

        Item item = BuiltInRegistries.ITEM.get(data.itemId());
        if (expectedType.isInstance(item)) {
            return expectedType.cast(item);
        }

        return null;
    }
}