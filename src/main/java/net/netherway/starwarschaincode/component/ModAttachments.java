package net.netherway.starwarschaincode.component;

import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.netherway.starwarschaincode.StarWarsChainCode;
import net.netherway.starwarschaincode.entity.ShipInventoryData;
import net.netherway.starwarschaincode.race.Race;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, StarWarsChainCode.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Race>> PLAYER_RACE =
            ATTACHMENT_TYPES.register("player_race", () ->
                    AttachmentType.builder(() -> Race.HUMAN)
                            .serialize(Race.CODEC)
                            .copyOnDeath()
                            .sync(ByteBufCodecs.fromCodec(Race.CODEC))
                            .build()
            );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ShipInventoryData>> SHIP_INVENTORY =
            ATTACHMENT_TYPES.register("ship_inventory", () ->
                    AttachmentType.builder(ShipInventoryData::new)
                            .serialize(ShipInventoryData.CODEC)
                            .build()
            );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}