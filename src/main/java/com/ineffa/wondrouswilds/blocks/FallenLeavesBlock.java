package com.ineffa.wondrouswilds.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class FallenLeavesBlock extends HorizontalFacingBlock {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public static final int MIN_DENSITY = 1;
    public static final int MAX_DENSITY = 5;

    public static final IntProperty DENSITY = IntProperty.of("density", MIN_DENSITY, MAX_DENSITY);

    public FallenLeavesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(DENSITY, MIN_DENSITY).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(DENSITY);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) return blockState.with(DENSITY, Math.min(MAX_DENSITY, blockState.get(DENSITY) + 1));

        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(DENSITY) < MAX_DENSITY) return true;

        return super.canReplace(state, context);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        /*return switch (state.get(DENSITY)) {
            default -> SINGLE_VIOLET_SHAPE;
            case 2 -> DOUBLE_VIOLET_SHAPE;
            case 3 -> TRIPLE_VIOLET_SHAPE;
            case 4 -> QUADRUPLE_VIOLET_SHAPE;
        };*/
        return SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) return Blocks.AIR.getDefaultState();

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return hasTopRim(world, pos.down());
    }

    @Override
    public float getMaxHorizontalModelOffset() {
        return 0.0F;
    }

    @Override
    public float getVerticalModelOffsetMultiplier() {
        return 0.001F;
    }
}
