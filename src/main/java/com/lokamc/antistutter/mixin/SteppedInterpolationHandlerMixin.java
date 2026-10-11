package com.lokamc.antistutter.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LinearInterpolationHandler;
import net.minecraft.world.entity.SteppedInterpolationHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Restores the pre-26.3 client behaviour (MC-311974): living entities lerp linearly toward each received
 * position over 3 ticks instead of replaying the server's stepped path.
 */
@Mixin(SteppedInterpolationHandler.class)
abstract class SteppedInterpolationHandlerMixin {

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void antistutter$restoreLinear(final Entity entity, final CallbackInfoReturnable<InterpolationHandler> cir) {
        if (entity.level().isClientSide()) {
            cir.setReturnValue(LinearInterpolationHandler.create(entity, LinearInterpolationHandler.DEFAULT_INTERPOLATION_STEPS));
        }
    }
}
