package com.lokamc.antistutter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class AntiStutter implements ClientModInitializer {

    /**
     * Advertised to the server through {@code minecraft:register} so anticheats can model the client as
     * 3-tick linear interpolation. Velocity replays registered channels to each backend on server switch.
     */
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("antistutter", "linear_3");

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.clientboundPlay().register(AdvertisePayload.TYPE, AdvertisePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(AdvertisePayload.TYPE, (payload, context) -> {
        });
    }

    public record AdvertisePayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<AdvertisePayload> TYPE = new CustomPacketPayload.Type<>(CHANNEL);
        public static final StreamCodec<RegistryFriendlyByteBuf, AdvertisePayload> CODEC = StreamCodec.unit(new AdvertisePayload());

        @Override
        public CustomPacketPayload.Type<AdvertisePayload> type() {
            return TYPE;
        }
    }
}
