package com.lokamc.antistutter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ServerboundPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AntiStutter implements ClientModInitializer {

    /**
     * The handshake channel. A server opts in by registering it through {@code minecraft:register}; only then does
     * the client switch to linear interpolation and register it back, so anticheats know to model the client that
     * way. Fabric forgets the server's channels on disconnect and on every proxy server switch, so the mod stays
     * vanilla anywhere that hasn't opted in.
     */
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath("antistutter", "linear_3");
    private static final Logger LOGGER = LoggerFactory.getLogger("AntiStutter");

    public static boolean isEnabled() {
        return ClientPlayNetworking.canSend(CHANNEL);
    }

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.clientboundPlay().register(AdvertisePayload.TYPE, AdvertisePayload.CODEC);
        // Fabric fires this on the client thread. Velocity sends its channels before the login packet, so the player
        // (and Minecraft.getConnection()) doesn't exist yet; Fabric holds the registration until the client can send.
        ServerboundPlayChannelEvents.REGISTER.register((listener, sender, client, channels) -> {
            if (!channels.contains(CHANNEL)) return;

            LOGGER.info("Server opted in, using linear entity interpolation");
            ClientPlayNetworking.registerReceiver(AdvertisePayload.TYPE, (payload, context) -> {
            });
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
