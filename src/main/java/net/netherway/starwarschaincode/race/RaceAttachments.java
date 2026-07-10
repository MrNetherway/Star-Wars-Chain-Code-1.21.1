package net.netherway.starwarschaincode.race;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RaceAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "starwarschaincode");

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Race>> PLAYER_RACE =
            ATTACHMENT_TYPES.register("player_race", () ->
                    AttachmentType.builder(() -> Race.HUMAN)
                            .serialize(Race.CODEC)
                            .copyOnDeath()
                            .build()
            );
}