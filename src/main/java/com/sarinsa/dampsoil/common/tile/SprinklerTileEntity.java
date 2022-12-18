package com.sarinsa.dampsoil.common.tile;

import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.registry.DSParticles;
import com.sarinsa.dampsoil.common.core.registry.DSTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class SprinklerTileEntity extends TileEntity implements ITickableTileEntity {

    private boolean sprinkling = false;
    private int radius;

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
        compoundNBT.putBoolean("Sprinkling", sprinkling);
        return compoundNBT;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);

        if (compoundNBT.contains("Sprinkling", Constants.NBT.TAG_BYTE)) {
            sprinkling = compoundNBT.getBoolean("Sprinkling");
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

            double yOffset = ySpeed < 0.0D ? 0.0D : 1.0D;

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
