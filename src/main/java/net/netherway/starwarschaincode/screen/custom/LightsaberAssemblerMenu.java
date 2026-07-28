package net.netherway.starwarschaincode.screen.custom;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.netherway.starwarschaincode.component.ModDataComponents;
import net.netherway.starwarschaincode.component.WeaponAttachmentData;
import net.netherway.starwarschaincode.item.HiltPartType;
import net.netherway.starwarschaincode.item.custom.HiltPartItem;
import net.netherway.starwarschaincode.item.custom.KyberCrystalItem;
import net.netherway.starwarschaincode.item.custom.LightsaberItem;
import net.netherway.starwarschaincode.screen.ModMenuTypes;
import net.netherway.starwarschaincode.screen.custom.slot.LightsaberResultSlot;
import org.jetbrains.annotations.Nullable;

public class LightsaberAssemblerMenu extends AbstractContainerMenu {

    private boolean editing = false;
    private final SimpleContainer input = new SimpleContainer(4);
    private final SimpleContainer result = new SimpleContainer(1);

    public LightsaberAssemblerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory);
    }

    public LightsaberAssemblerMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.LIGHTSABER_ASSEMBLER_MENU.get(), containerId);
        input.addListener((container) -> slotsChanged(container));
        result.addListener((container) -> broadcastChanges());

        this.addSlot(new LightsaberResultSlot(this, result,0,79,20));

        this.addSlot(new Slot(input, 0, 54, 45){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof HiltPartItem part &&
                        part.getType() == HiltPartType.PART_1;
            }
        });
        this.addSlot(new Slot(input, 1, 103, 45){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof HiltPartItem part &&
                        part.getType() == HiltPartType.PART_2;
            }
        });
        this.addSlot(new Slot(input, 2, 79, 45){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof HiltPartItem part &&
                        part.getType() == HiltPartType.PART_3;
            }
        });
        this.addSlot(new Slot(input, 3, 89, 45){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof KyberCrystalItem;
            }
        });
        // slots virão aqui

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    public boolean hasAnyPart() {
        for (int i = 0; i < input.getContainerSize(); i++) {
            if (!input.getItem(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        updateResult();
    }

    private void loadLightsaber(ItemStack saber) {
        loadPart(saber, ModDataComponents.HILT_PART_1.get(), 0);
        loadPart(saber, ModDataComponents.HILT_PART_2.get(), 1);
        loadPart(saber, ModDataComponents.HILT_PART_3.get(), 2);
        loadPart(saber, ModDataComponents.KYBER_CRYSTAL.get(), 3);
    }

    public void beginEditing(ItemStack saber){

        editing = true;

        loadLightsaber(saber);

        broadcastChanges();
    }

    private void loadPart(ItemStack saber,
                          DataComponentType<WeaponAttachmentData> component,
                          int slot) {

        WeaponAttachmentData data = saber.get(component);

        if (data == null)
            return;

        ItemStack stack = BuiltInRegistries.ITEM
                .get(data.itemId())
                .getDefaultInstance();

        input.setItem(slot, stack);
    }

    public void consumeParts(){

        for(int i = 0; i < 4; i++){
            input.removeItem(i,1);
        }

        result.setItem(0, ItemStack.EMPTY);

        broadcastChanges();
    }

    private void updateResult() {

        ItemStack part1 = input.getItem(0);
        ItemStack part2 = input.getItem(1);
        ItemStack part3 = input.getItem(2);
        ItemStack part4 = input.getItem(3);


        if (!part1.isEmpty()
                && !part2.isEmpty()
                && !part3.isEmpty()
                && !part4.isEmpty()) {


            ItemStack saber = new ItemStack(net.netherway.starwarschaincode.item.ModItems.LIGHTSABER.get());


            saber.set(
                    ModDataComponents.HILT_PART_1.get(),
                    new WeaponAttachmentData(
                            BuiltInRegistries.ITEM.getKey(part1.getItem())
                    )
            );


            saber.set(
                    ModDataComponents.HILT_PART_2.get(),
                    new WeaponAttachmentData(
                            BuiltInRegistries.ITEM.getKey(part2.getItem())
                    )
            );


            saber.set(
                    ModDataComponents.HILT_PART_3.get(),
                    new WeaponAttachmentData(
                            BuiltInRegistries.ITEM.getKey(part3.getItem())
                    )
            );

            saber.set(
                    ModDataComponents.KYBER_CRYSTAL.get(),
                    new WeaponAttachmentData(
                            BuiltInRegistries.ITEM.getKey(part4.getItem())
                    )
            );


            result.setItem(0, saber);
            broadcastChanges();


        } else {

            result.setItem(0, ItemStack.EMPTY);
            broadcastChanges();

        }
    }

    @Override
    public void removed(Player player){

        super.removed(player);


        if(player.level().isClientSide())
            return;


        for(int i=0;i<4;i++){

            ItemStack stack = input.getItem(i);

            if(!stack.isEmpty()){

                player.getInventory().placeItemBackInInventory(stack);

                input.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
