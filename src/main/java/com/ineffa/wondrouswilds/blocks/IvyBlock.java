package com.ineffa.wondrouswilds.blocks;

import com.ineffa.wondrouswilds.registry.WondrousWildsItems;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class IvyBlock extends MultifaceGrowthBlock implements Fertilizable {

    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public IvyBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) return Fluids.WATER.getStill(false);

        return super.getFluidState(state);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.getStack().isOf(WondrousWildsItems.IVY) || super.canReplace(state, context);
    }

    private final LichenGrower grower = new LichenGrower(this);

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return Direction.stream().anyMatch(direction -> this.grower.canGrow(state, world, pos, direction.getOpposite()));
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.grower.grow(state, world, pos, random);
    }

    @Override
    public LichenGrower getGrower() {
        return this.grower;
    }
}
