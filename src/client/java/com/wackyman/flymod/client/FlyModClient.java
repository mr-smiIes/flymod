package com.wackyman.flymod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class FlyModClient implements ClientModInitializer {
    public static final String MOD_ID = "flymod";

    private static final double FLY_SPEED = 0.8D;

    private static final KeyMapping TOGGLE_FLY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.flymod.toggle",
                    InputConstants.KEY_G,
                    new KeyMapping.Category(
                            Identifier.fromNamespaceAndPath(MOD_ID, "main")
                    )
            )
    );

    private static boolean flyMode = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(FlyModClient::tick);
    }

    private static void tick(Minecraft client) {
        while (TOGGLE_FLY.consumeClick()) {
            flyMode = !flyMode;

            if (client.player != null) {
                if (flyMode) {
                    enableFlight(client.player);
                } else {
                    disableFlight(client.player);
                }
            }
        }

        if (!flyMode || client.player == null) {
            return;
        }

        LocalPlayer player = client.player;

        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;

        Vec3 movement = getFlightMovement(client, player);
        player.setDeltaMovement(movement);
        player.resetFallDistance();
    }

    private static void enableFlight(LocalPlayer player) {
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.getAbilities().setFlyingSpeed(0.05F);
        player.onUpdateAbilities();
        player.resetFallDistance();
    }

    private static void disableFlight(LocalPlayer player) {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
        player.resetFallDistance();
    }

    private static Vec3 getFlightMovement(Minecraft client, LocalPlayer player) {
        double forward = 0.0D;
        double strafe = 0.0D;
        double vertical = 0.0D;

        if (client.options.keyUp.isDown()) {
            forward += 1.0D;
        }
        if (client.options.keyDown.isDown()) {
            forward -= 1.0D;
        }
        if (client.options.keyLeft.isDown()) {
            strafe += 1.0D;
        }
        if (client.options.keyRight.isDown()) {
            strafe -= 1.0D;
        }
        if (client.options.keyJump.isDown()) {
            vertical += 1.0D;
        }
        if (client.options.keyShift.isDown()) {
            vertical -= 1.0D;
        }

        Vec3 horizontal = Vec3.ZERO;

        if (forward != 0.0D || strafe != 0.0D) {
            Vec3 look = player.getLookAngle();
            Vec3 forwardVector = new Vec3(look.x, 0.0D, look.z);

            if (forwardVector.lengthSqr() > 1.0E-7D) {
                forwardVector = forwardVector.normalize();

                Vec3 rightVector = new Vec3(
                        -forwardVector.z,
                        0.0D,
                        forwardVector.x
                );

                horizontal = forwardVector.scale(forward)
                        .add(rightVector.scale(-strafe));

                if (horizontal.lengthSqr() > 1.0D) {
                    horizontal = horizontal.normalize();
                }
            }
        }

        return new Vec3(
                horizontal.x * FLY_SPEED,
                vertical * FLY_SPEED,
                horizontal.z * FLY_SPEED
        );
    }

    public static boolean isFlyModeEnabled() {
        return flyMode;
    }
}
