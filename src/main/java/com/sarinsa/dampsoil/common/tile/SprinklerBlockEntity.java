package com.sarinsa.dampsoil.common.tile;

import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.core.registry.DSBlockEntities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class SprinklerBlockEntity extends BlockEntity {

    protected boolean sprinkling = false;
    private Supplier<Integer> radiusSupplier;

    protected int timeNextSync = 10;
    protected boolean needSync = false;

    private final FluidTank waterTank = new FluidTank(2000, (fluidStack) -> fluidStack.getFluid().is(FluidTags.WATER)) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            needSync = true;
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> waterTank);


    public SprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(DSBlockEntities.SPRINKLER.get(), pos, state);
    }

    public FluidTank getWaterTank() {
        return waterTank;
    }

    public int getRadius() {
        return radiusSupplier.get();
    }

    @Override
    public void onLoad() {
        if (level != null && getBlockState().getBlock() instanceof SprinklerBlock) {
            radiusSupplier = ((SprinklerBlock) getBlockState().getBlock()).getRadius();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SprinklerBlockEntity sprinkler) {
        // Are we sprinklin'? :^)
        if (state.getValue(SprinklerBlock.SPRINKLING)) {
            final int radius = sprinkler.getRadius();
            boolean requirePiping = DSCommonConfig.COMMON.requirePiping.get();

            // Are we configured to need water pipes? If so, check for that and do what needs to be done
            if (requirePiping) {
                FluidTank waterTank = sprinkler.getWaterTank();
                if (waterTank.getFluid().getFluid().is(FluidTags.WATER) && waterTank.getFluid().getAmount() >= radius) {
                    waterTank.getFluid().setAmount(waterTank.getFluid().getAmount() - radius);

                    if (--sprinkler.timeNextSync <= 0) {
                        if (sprinkler.needSync) {
                            sprinkler.notifyChanges();
                            sprinkler.needSync = false;
                        }
                        sprinkler.timeNextSync = 10;
                    }
                }
                else {
                    return;
                }
            }
            Random random = level.getRandom();

            // Play the sprinkly noise
            if (random.nextDouble() < 0.15D) {
                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.WEATHER_RAIN,
                            SoundSource.BLOCKS,
                            0.5F,
                            1.5F
                    );
                }
            }
            if (level.isClientSide) {
                // Funnie splash particles
                splashParticles(radius, level, pos);
            }
            final int loopCount = (int) ((radius + 1) / 1.5D);

            for (int i = 0; i < loopCount; ++i) {
                int yOffset = 1;
                if (state.getValue(SprinklerBlock.FACING) == Direction.DOWN) yOffset = 4;

                BlockPos randomOffsetPos = pos.offset(random.nextInt(1 + 2 * radius) - radius, random.nextInt(3) - yOffset, random.nextInt(1 + 2 * radius) - radius);
                // moisten farmland
                if (level.getBlockState(randomOffsetPos).is(Blocks.FARMLAND)) {
                    level.setBlock(randomOffsetPos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 2);
                }
                // extinguish fires
                if (level.getBlockState(randomOffsetPos).is(BlockTags.FIRE)) {
                    level.removeBlock(randomOffsetPos, false);
                }
            }
            // entity interactions
            if (DSCommonConfig.COMMON.mobInteractions.get()) {
                AABB range = new AABB(pos.offset(-radius, -1, -radius), pos.offset(radius, 2, radius));

                if (state.getValue(SprinklerBlock.FACING) == Direction.DOWN)
                    range = range.move(0, -3, 0);

                for (Entity entity : level.getEntitiesOfClass(Entity.class, range, entity -> true)) {
                    // hurt mobs sensitive to water
                    if (entity instanceof LivingEntity && (((LivingEntity) entity).isSensitiveToWater() || entity instanceof Bee)) {
                        entity.hurt(DamageSource.DROWN, 1.0F);
                    }
                    // extinguish entities
                    if (entity.getRemainingFireTicks() > 0) {
                        entity.clearFire();
                    }
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        waterTank.writeToNBT(compoundTag);
        compoundTag.putBoolean("Sprinkling", sprinkling);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        readSyncData(compoundTag);
    }

    private void readSyncData(CompoundTag syncTag) {
        waterTank.readFromNBT(syncTag);

        if (syncTag.contains("Sprinkling", Tag.TAG_BYTE)) {
            sprinkling = syncTag.getBoolean("Sprinkling");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = new CompoundTag();
        saveAdditional(updateTag);
        return updateTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        readSyncData(tag);
    }

    /**
     * Sends the sprinkler update packet to clients
     */
    protected void notifyChanges() {
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        LazyOptional<T> result = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(capability, fluidHandler);

        if (result.isPresent()) {
            Direction sprinklerDir = getBlockState().getValue(SprinklerBlock.FACING);

            if (facing == null)
                return result;

            return facing != sprinklerDir ? result : LazyOptional.empty();
        }
        else {
            return super.getCapability(capability, facing);
        }
    }

    protected static void splashParticles(int radius, Level level, BlockPos pos) {
        double speedMul = 30.0D * radius / 2.0D;
        Random random = level.random;
        int count = 6 * (radius / 2);

        for (int i = 0; i < count; ++i) {
            double xSpeed = (double)random.nextFloat() - 0.5D;
            double zSpeed = (double)random.nextFloat() - 0.5D;
            double ySpeed = -1.0D;

            if (level.getBlockState(pos).getValue(SprinklerBlock.FACING) == Direction.UP)
                ySpeed = 1.0D;

            double yOffset = ySpeed < 0.0D ? -0.001D : 1.0D;

            level.addParticle(DSParticles.SPRINKLER_SPLASH.get(),
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + yOffset,
                    (double)pos.getZ() + 0.5D,
                    xSpeed * speedMul,
                    ySpeed * 60.0D,
                    zSpeed * speedMul);
        }
    }
}
