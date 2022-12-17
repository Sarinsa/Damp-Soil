package com.sarinsa.dampsoil.common.tile;

import com.sarinsa.dampsoil.common.block.SprinklerBlock;
import com.sarinsa.dampsoil.common.core.DSTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class SprinklerTileEntity extends TileEntity implements ITickableTileEntity {

    private boolean sprinkling = false;

    public SprinklerTileEntity() {
        super(DSTileEntities.SPRINKLER.get());
    }

    @Override
    public void tick() {
        if (level == null)
            return;

        // Assume the state is always a Sprinkler, should never not be
        if (getBlockState().getValue(SprinklerBlock.SPRINKLING)) {
            BlockPos pos = getBlockPos();
            Random random = level.getRandom();

            if (random.nextDouble() < 0.15D) {
                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.WEATHER_RAIN,
                            SoundCategory.BLOCKS,
                            0.5F,
                            1.5F
                    );
                }
            }
            // Funnie splash particles
            splashParticles(level, pos);

            BlockPos randomPos = pos.offset(random.nextInt(5) - 2, random.nextInt(2) - 1, random.nextInt(5) - 2);

            if (level.getBlockState(randomPos).is(Blocks.FARMLAND)) {
                level.setBlock(randomPos, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7), 2);
            }
            // Todo: interactions with mobs (lets wait and see what happens on the Fabric repo, no personal plans)
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

    protected static void splashParticles(World world, BlockPos pos) {
        Random random = world.random;

        for (int i = 0; i < 6; i++) {
            double xSpeed = (random.nextFloat() - 0.5D) * 30;
            double zSpeed = (random.nextFloat() - 0.5D) * 30;

            world.addParticle(
                    ParticleTypes.SPLASH,
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 1,
                    (double) pos.getZ() + 0.5D,
                    xSpeed,
                    60.0D,
                    zSpeed);
        }
    }
}
