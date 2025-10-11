package net.treset.discmanextras.rpc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.dedicated.management.OutgoingRpcMethod;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.RpcRequestParameter;
import net.minecraft.server.dedicated.management.UriUtil;
import net.minecraft.server.dedicated.management.schema.RpcSchema;
import net.minecraft.server.dedicated.management.schema.RpcSchemaEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.treset.discmanextras.DiscmanManagementServer;
import net.treset.discmanextras.accessors.OutgoingRpcMethodBuilderAccessor;

public record RpcDeath(RpcPlayer player, RpcText message) {
    public static Codec<RpcDeath> CODEC;
    public static RpcSchemaEntry SCHEMA;
    public static RegistryEntry.Reference<? extends OutgoingRpcMethod<RpcDeath, ?>> RPC_METHOD;

    public static void register() {
        SCHEMA = new RpcSchemaEntry("death",
                UriUtil.createSchemasUri("death"),
                RpcSchema.ofObject()
                        .withProperty("player", RpcSchema.PLAYER.ref())
                        .withProperty("message", RpcText.SCHEMA.ref())
        );

        CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        RpcPlayer.CODEC.fieldOf("player").forGetter(RpcDeath::player),
                        RpcText.CODEC.fieldOf("message").forGetter(RpcDeath::message)
                ).apply(instance, RpcDeath::new)
        );

        RPC_METHOD = ((OutgoingRpcMethodBuilderAccessor<? extends OutgoingRpcMethod<RpcDeath, ?>>)(
                OutgoingRpcMethod
                        .createNotificationBuilder(RpcDeath.CODEC)
                        .requestParameter(new RpcRequestParameter("death", SCHEMA.schema()))
                        .description("Player died")
                )
        ).buildAndRegisterDiscman("notification/death");
    }

    public static RpcDeath of(ServerPlayerEntity player, Text message) {
        return new RpcDeath(
                RpcPlayer.of(player),
                RpcText.of(message)
        );
    }

    public static void handle(ServerPlayerEntity player, Text message) {
        DiscmanManagementServer.notifyAll(
                RPC_METHOD,
                RpcDeath.of(player, message)
        );
    }
}
