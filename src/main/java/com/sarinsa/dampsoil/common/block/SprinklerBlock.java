package com.sarinsa.dampsoil.common.block;

import com.sarinsa.dampsoil.common.block.entity.SprinklerBlockEntity;
import com.sarinsa.dampsoil.common.core.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SprinklerBlock extends Block implements EntityBlock {
    
    public static final BooleanProperty SPRINKLING = BooleanProperty.create( "sprinkling" );
    public static final BooleanProperty ACTIVATED = BooleanProperty.create( "activated" );
    public static final DirectionProperty FACING = DirectionProperty.create( "facing", Direction.Plane.VERTICAL );
    
    private final Supplier<Integer> radius;
    
    
    public SprinklerBlock( Supplier<Integer> radiusSupplier ) {
        super( BlockBehaviour.Properties.of()
                .strength( 2.0F )
                .sound( SoundType.METAL )
                .requiresCorrectToolForDrops() );
        radius = radiusSupplier;
        
        registerDefaultState( stateDefinition.any()
                .setValue( SPRINKLING, false )
                .setValue( ACTIVATED, false )
                .setValue( FACING, Direction.UP ) );
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new SprinklerBlockEntity( pos, state );
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState blockState, BlockEntityType<T> type ) {
        if( level.isClientSide )
            return ( lvl, state, pos, blockEntity ) -> SprinklerBlockEntity.clientTick( lvl, state, pos, (SprinklerBlockEntity) blockEntity );
        return ( lvl, state, pos, blockEntity ) -> SprinklerBlockEntity.serverTick( (ServerLevel) lvl, state, pos, (SprinklerBlockEntity) blockEntity );
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockPlaceContext context ) {
        return defaultBlockState().setValue( FACING, context.getNearestLookingVerticalDirection().getOpposite() );
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void neighborChanged( BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean notify ) {
        if( !level.isClientSide ) {
            // obstruction check
            boolean obstructed = false;
            
            if( state.getValue( FACING ) == Direction.UP ) {
                if( !level.getBlockState( pos.above() ).isAir() )
                    obstructed = true;
            }
            else {
                if( !level.getBlockState( pos.below() ).isAir() )
                    obstructed = true;
            }
            
            if( obstructed ) {
                level.setBlock( pos, state.setValue( SPRINKLING, false ).setValue( ACTIVATED, false ), Block.UPDATE_CLIENTS );
                return;
            }
            
            int activationTime = Config.IRRIGATION.SPRINKLERS.activeDuration.get();
            
            // activation
            if( level.hasNeighborSignal( pos ) ) {
                if( !state.getValue( ACTIVATED ) ) {
                    level.setBlock( pos, state.setValue( SPRINKLING, true ).setValue( ACTIVATED, true ), Block.UPDATE_CLIENTS );
                    
                    if( activationTime > 0 ) {
                        level.scheduleTick( pos, this, activationTime );
                    }
                    else {
                        level.setBlock( pos, state.setValue( SPRINKLING, true ).setValue( ACTIVATED, true ), Block.UPDATE_CLIENTS );
                    }
                }
            }
            else {
                if( activationTime > 0 ) {
                    level.setBlock( pos, state.setValue( SPRINKLING, state.getValue( SPRINKLING ) ).setValue( ACTIVATED, false ), Block.UPDATE_CLIENTS );
                }
                else {
                    level.setBlock( pos, state.setValue( SPRINKLING, false ).setValue( ACTIVATED, false ), Block.UPDATE_CLIENTS );
                }
            }
        }
    }
    
    public final Supplier<Integer> getRadius() {
        return radius;
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void tick( BlockState state, ServerLevel world, BlockPos pos, RandomSource random ) {
        world.setBlock( pos, state.setValue( SPRINKLING, false ), Block.UPDATE_CLIENTS );
    }
    
    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> stateBuilder ) {
        stateBuilder.add( SPRINKLING, ACTIVATED, FACING );
    }
}
