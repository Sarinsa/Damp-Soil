package com.sarinsa.dampsoil.common.block;

import com.sarinsa.dampsoil.common.compat.glitchfiend.SereneSeasonsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings( "deprecation" )
public class FrozenFarmBlock extends Block {
    
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    
    private static final VoxelShape SHAPE = Block.box( 0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D );
    
    
    public FrozenFarmBlock() {
        super( BlockBehaviour.Properties.copy( Blocks.FARMLAND )
                .mapColor( MapColor.TERRACOTTA_WHITE )
                .sound( SoundType.STONE )
                .strength( 1.0F ) );
        
        registerDefaultState( stateDefinition.any().setValue( MOISTURE, 0 ) );
    }
    
    @Override
    public void randomTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource ) {
        if( (level.getBrightness( LightLayer.BLOCK, pos ) > 11 - state.getLightBlock( level, pos )) || !SereneSeasonsHelper.isWinter( level ) ) {
            level.setBlockAndUpdate( pos, Blocks.FARMLAND.defaultBlockState().setValue( FarmBlock.MOISTURE, state.getValue( MOISTURE ) ) );
        }
    }
    
    @Override
    public boolean useShapeForLightOcclusion( BlockState state ) {
        return true;
    }
    
    @Override
    public boolean isPathfindable( BlockState state, BlockGetter world, BlockPos pos, PathComputationType computationType ) {
        return false;
    }
    
    @Override
    public VoxelShape getShape( BlockState state, BlockGetter world, BlockPos pos, CollisionContext context ) {
        return SHAPE;
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( MOISTURE );
    }
}
