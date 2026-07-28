package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.block.ModBlocks;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.component.WeaponAttachmentData;
import net.netherway.starwarschaincode.item.AttachmentItem;
import net.netherway.starwarschaincode.item.custom.WeaponItem;
import net.netherway.starwarschaincode.screen.ModMenuTypes;

public class WeaponWorkbenchMenu extends AbstractContainerMenu {

    private static final int WEAPON_SLOT = 0;
    private static final int SCOPE_SLOT = 1;
    private static final int STOCK_SLOT = 2;
    private static final int BARREL_SLOT = 3;

    private boolean loadingAttachments = false;
    private boolean weaponLoaded = false;

    private final Container container = new SimpleContainer(4) {
        @Override
        public void setChanged() {
            super.setChanged();
            WeaponWorkbenchMenu.this.slotsChanged(this);
        }
    };

    public WeaponWorkbenchMenu(int containerId, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(containerId, inv);
    }

    public WeaponWorkbenchMenu(int containerId, Inventory inv) {
        super(ModMenuTypes.WEAPON_WORKBENCH_MENU.get(), containerId);

        // slot da arma
        this.addSlot(new Slot(container, WEAPON_SLOT, 79, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof WeaponItem;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {

                mergeAttachment(stack, SCOPE_SLOT, ModDataComponents.SCOPE_ITEM.get());
                mergeAttachment(stack, STOCK_SLOT, ModDataComponents.STOCK_ITEM.get());
                mergeAttachment(stack, BARREL_SLOT, ModDataComponents.BARREL_ITEM.get());

                weaponLoaded = false;

                super.onTake(player, stack);
            }
        });

        // scope
        this.addSlot(new Slot(container, SCOPE_SLOT, 79, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !container.getItem(WEAPON_SLOT).isEmpty()
                        && stack.getItem() instanceof AttachmentItem att
                        && att.getAttachmentType() == AttachmentItem.AttachmentType.SCOPE;
            }


            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                ItemStack weapon = container.getItem(WEAPON_SLOT);

                if (!weapon.isEmpty()) {
                    weapon.remove(ModDataComponents.SCOPE_ITEM.get());
                }
            }
        });

        // stock (coronha)
        this.addSlot(new Slot(container, STOCK_SLOT, 54, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !container.getItem(WEAPON_SLOT).isEmpty()
                        && stack.getItem() instanceof AttachmentItem att
                        && att.getAttachmentType() == AttachmentItem.AttachmentType.STOCK;
            }


            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                ItemStack weapon = container.getItem(WEAPON_SLOT);

                if (!weapon.isEmpty()) {
                    weapon.remove(ModDataComponents.STOCK_ITEM.get());
                }
            }
        });

        // cano
        this.addSlot(new Slot(container, BARREL_SLOT, 103, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !container.getItem(WEAPON_SLOT).isEmpty()
                        && stack.getItem() instanceof AttachmentItem att
                        && att.getAttachmentType() == AttachmentItem.AttachmentType.BARREL;
            }


            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                ItemStack weapon = container.getItem(WEAPON_SLOT);

                if (!weapon.isEmpty()) {
                    weapon.remove(ModDataComponents.BARREL_ITEM.get());
                }
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    private void mergeAttachment(ItemStack weaponStack, int slotIndex,
                                 DataComponentType<WeaponAttachmentData> componentType) {
        ItemStack attachmentStack = container.getItem(slotIndex);

        if (!attachmentStack.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(attachmentStack.getItem());

            weaponStack.set(componentType, new WeaponAttachmentData(id));
            container.setItem(slotIndex, ItemStack.EMPTY);
        }
    }

    @Override
    public void slotsChanged(Container container) {

        super.slotsChanged(container);

        if (loadingAttachments)
            return;

        ItemStack weaponStack = container.getItem(WEAPON_SLOT);

        if (weaponStack.isEmpty()) {
            weaponLoaded = false;
            return;
        }


        // só carrega uma vez quando a arma entra
        if (!weaponLoaded) {

            loadingAttachments = true;

            loadAttachment(weaponStack, SCOPE_SLOT, ModDataComponents.SCOPE_ITEM.get());
            loadAttachment(weaponStack, STOCK_SLOT, ModDataComponents.STOCK_ITEM.get());
            loadAttachment(weaponStack, BARREL_SLOT, ModDataComponents.BARREL_ITEM.get());

            loadingAttachments = false;

            weaponLoaded = true;
        }
    }

    private void loadAttachment(ItemStack weaponStack, int slotIndex,
                                DataComponentType<WeaponAttachmentData> componentType) {


        WeaponAttachmentData data = weaponStack.get(componentType);

        if (data != null) {

            var item = BuiltInRegistries.ITEM.get(data.itemId());

            if (item != null) {
                container.setItem(slotIndex, new ItemStack(item));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // TODO shift-click, se quiser depois
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.NULL, player, ModBlocks.WEAPON_WORKBENCH.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        ItemStack weapon = container.getItem(WEAPON_SLOT);

        if (!weapon.isEmpty()) {
            mergeAttachment(weapon, SCOPE_SLOT, ModDataComponents.SCOPE_ITEM.get());
            mergeAttachment(weapon, STOCK_SLOT, ModDataComponents.STOCK_ITEM.get());
            mergeAttachment(weapon, BARREL_SLOT, ModDataComponents.BARREL_ITEM.get());
        }
        // fechou sem "fabricar" -> tudo volta pro inventário, nada persiste
        clearContainer(player, container);
    }
}