package net.darkhax.botanypots.block;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.IBindRenderLayer;
import net.darkhax.bookshelf.api.block.InventoryBlock;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBotanyPot extends InventoryBlock implements SimpleWaterloggedBlock, IBindRenderLayer {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 8, 14);
    private static final Properties DEFAULT_PROPERTIES = Block.Properties.of(Material.CLAY, MaterialColor.COLOR_ORANGE).strength(1.25F, 4.2F).noOcclusion();

    private final boolean hasInventory;

    public BlockBotanyPot(boolean hasInventory) {
        
        this(DEFAULT_PROPERTIES, hasInventory);
    }

    public BlockBotanyPot(Block.Properties properties, boolean hasInventory) {

        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
        this.hasInventory = hasInventory;
    }

    public boolean hasInventory() {

        return this.hasInventory;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {

        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        final BlockPos placedPos = context.getClickedPos();
        final FluidState fluidState = context.getLevel().getFluidState(placedPos);
        return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {

        return level.getBlockEntity(pos) instanceof BlockEntityBotanyPot pot ? pot.getLightLevel() : 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {

        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {

        return world.getBlockEntity(pos) instanceof BlockEntityBotanyPot pot ? pot.getComparatorLevel() : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new BlockEntityBotanyPot(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (world.getBlockEntity(pos) instanceof BlockEntityBotanyPot potEntity) {

            if (player instanceof ServerPlayer serverPlayer) {

                if (!player.isCrouching() && !potEntity.isHopper() && potEntity.doneGrowing && potEntity.getCropInfo() != null) {

                    for (ItemStack drop : BotanyPotHelper.generateDrop(world.random, potEntity.getCropInfo())) {

                        popResource(world, pos, drop);
                    }

                    potEntity.resetGrowth();
                    return InteractionResult.CONSUME;
                }

                Services.INVENTORY_HELPER.openMenu(serverPlayer, potEntity, buf -> Serializers.BLOCK_POS.toByteBuf(buf, pos));
                return InteractionResult.CONSUME;
            }

            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, hitResult);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldLevel, BlockState state, BlockEntityType<T> blockEntityType) {

        return createTickerHelper(blockEntityType, BlockEntityBotanyPot.POT_TYPE.get(), BlockEntityBotanyPot::tickPot);
    }

    @Override
    public RenderType getRenderLayerToBind() {

        return RenderType.cutout();
    }
}