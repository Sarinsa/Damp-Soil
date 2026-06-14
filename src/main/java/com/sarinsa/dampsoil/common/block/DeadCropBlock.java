package com.sarinsa.dampsoil.common.block;


import com.sarinsa.dampsoil.common.tag.DampSoilTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DeadCropBlock extends DeadBushBlock {
    
    public DeadCropBlock() {
        super( BlockBehaviour.Properties.copy( Blocks.DEAD_BUSH ) );
    }
    
    @Override
    protected boolean mayPlaceOn( BlockState state, BlockGetter world, BlockPos pos ) {
        return state.is( DampSoilTags.Blocks.DEAD_CROP_MAY_PLACE_ON );
    }
}
