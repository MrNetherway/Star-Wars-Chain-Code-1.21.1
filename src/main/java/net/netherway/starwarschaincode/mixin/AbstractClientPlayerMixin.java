package net.netherway.starwarschaincode.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.netherway.starwarschaincode.race.Race;
import net.netherway.starwarschaincode.race.RaceAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void replaceRaceSkin(CallbackInfoReturnable<PlayerSkin> cir) {

        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;

        Race race = player.getData(RaceAttachments.PLAYER_RACE);

        if (race == Race.HUMAN)
            return;

        PlayerSkin original = cir.getReturnValue();

        if (original == null)
            return;

        cir.setReturnValue(new PlayerSkin(
                race.getSkinTexture(),
                original.textureUrl(),
                original.capeTexture(),
                original.elytraTexture(),
                original.model(),
                original.secure()
        ));
    }
}