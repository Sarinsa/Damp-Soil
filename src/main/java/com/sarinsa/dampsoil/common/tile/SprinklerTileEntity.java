package com.sarinsa.dampsoil.common.tile;

import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.config.DSCommonConfig;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.core.registry.DSTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class SprinklerTileEntity extends TileEntity implements ITickableTileEntity {

    private boolean sprinkling = false;
    private int radius;

    private int timeNextSync = 10;
    protected boolean needSync = false;

    private final FluidTank waterTank = new FluidTank(2000, (fluidStack) -> fluidStack.getFluid().is(FluidTags.WATER)) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            needSync = true;
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandlerCap = LazyOptional.of(() -> waterTank);


    public SprinklerTileEntity() {
        super(DSTileEntities.SPRINKLER.get());
    }

    @Override
    public void onLoad() {
        if (level != null && getBlockState().getBlock() instanceof SprinklerBlock) {
            radius = ((SprinklerBlock) getBlockState().getBlock()).getRadius();
        }
    }

    @Override
    public void tick() {
        // ABORT MISSION
        if (level == null)
            return;

        // Are we sprinklin'? :^)
        if (getBlockState().getValue(SprinklerBlock.SPRINKLING)) {

            boolean requirePiping = DSCommonConfig.COMMON.requirePiping.get();

            // Are we configured to need water pipes? If so, check for that and do what needs to be done
            if (requirePiping) {
                if (waterTank.getFluid().getFluid().is(FluidTags.WATER) && waterTank.getFluid().getAmount() > 0) {
                    waterTank.getFluid().setAmount(waterTank.getFluid().getAmount() - 1);

                    if (--timeNextSync <= 0) {
                        if (needSync) {
                            sendUpdatePacket();
                            needSync = false;
                        }
                        timeNextSync = 10;
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
                            getBlockPos(),
                            SoundEvents.WEATHER_RAIN,
                            SoundCategory.BLOCKS,
                            0.5f,
                            1.5f
                    );
                }
            }
            if (level.isClientSide) {
                // Funnie splash particles
                splashParticles(level, getBlockPos());
            }
            final int loopCount = (radius + 1) / 2;

            for (int i = 0; i < loopCount; ++i) {
                int yOffset = 1;
                if (getBlockState().getValue(SprinklerBlock.FACING) == Direction.DOWN) yOffset = 4;

                BlockPos randomOffsetPos = getBlockPos().offset(random.nextInt(1 + 2 * radius) - radius, random.nextInt(3) - yOffset, random.nextInt(1 + 2 * radius) - radius);
                // moisten farmland
                if (level.getBlockState(randomOffsetPos).is(Blocks.FARMLAND)) {
                    level.setBlock(randomOffsetPos, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7), 2);
                }
                // extinguish fires
                if (level.getBlockState(randomOffsetPos).is(BlockTags.FIRE)) {
                    level.removeBlock(randomOffsetPos, false);
                }
            }
            // entity interactions
            BlockPos pos = getBlockPos();
            AxisAlignedBB range = new AxisAlignedBB(pos.offset(-radius, -1, -radius), pos.offset(radius, 2, radius));

            if (getBlockState().getValue(SprinklerBlock.FACING) == Direction.DOWN)
                range = range.move(0, -3, 0);

            // iterates through entities
            for (Entity entity: level.getEntitiesOfClass(Entity.class, range, entity -> true)) {
                // hurt water-vulnerable mobs
                if (entity instanceof LivingEntity && (((LivingEntity) entity).isSensitiveToWater() || entity instanceof BeeEntity)) {
                    entity.hurt(DamageSource.DROWN, 1.0F);
                }
                // extinguish entities
                if (entity.getRemainingFireTicks() > 0) {
                    entity.clearFire();
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT = super.save(compoundNBT);

        waterTank.writeToNBT(compoundNBT);
        compoundNBT.putBoolean("Sprinkling", sprinkling);

        return compoundNBT;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        waterTank.readFromNBT(compoundNBT);

        if (compoundNBT.contains("Sprinkling", Constants.NBT.TAG_BYTE)) {
            sprinkling = compoundNBT.getBoolean("Sprinkling");
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket packet) {
        super.onDataPacket(networkManager, packet);
        load(getBlockState(), packet.getTag());
    }

    /**
     * Sends the sprinkler update packet to clients
     */
    private void sendUpdatePacket() {
        SUpdateTileEntityPacket updatePacket = getUpdatePacket();

        if (updatePacket != null && level != null && !level.isClientSide) {
            ServerWorld serverWorld = (ServerWorld) level;
            serverWorld.getChunkSource().chunkMap.getPlayers(new ChunkPos(getBlockPos()), false).forEach(player -> player.connection.send(updatePacket));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        LazyOptional<T> result = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(capability, fluidHandlerCap);

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

    protected void splashParticles(World world, BlockPos pos) {
        double speedMul = 30.0D * radius / 2.0D;
        Random random = world.random;

        for (int i = 0; i < 6; ++i) {
            double xSpeed = (double)random.nextFloat() - 0.5D;
            double zSpeed = (double)random.nextFloat() - 0.5D;
            double ySpeed = -1.0D;

            if (world.getBlockState(pos).getValue(SprinklerBlock.FACING) == Direction.UP)
                ySpeed = 1.0D;

            double yOffset = ySpeed < 0.0D ? -0.001D : 1.0D;

            world.addParticle(DSParticles.SPRINKLER_SPLASH.get(),
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + yOffset,
                    (double)pos.getZ() + 0.5D,
                    xSpeed * speedMul,
                    ySpeed * 60.0D,
                    zSpeed * speedMul);
        }
    }
}
