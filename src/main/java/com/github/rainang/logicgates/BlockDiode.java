package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockDiode extends BlockDirectional implements IDiode {

	protected final boolean isPowered;

	protected BlockDiode(String name, boolean powered) {
		super(Material.circuits);
		isPowered = powered;
		if(!isPowered)
			setCreativeTab(name.contains("redstone") ? LogicGates.TAB_REDSTONE : LogicGates.TAB_ENDER);
		setUnlocalizedName(name + (powered ? "_on" : ""));
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	public boolean isPowered(IBlockState state) {
		return isPowered;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos) {
		return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
	}

	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		boolean flag = shouldBePowered(worldIn, pos, state);
		if((isPowered && !flag || !isPowered && flag) && !worldIn.isBlockTickPending(pos, this)) {
			byte b0 = (byte)(isPowered ? -2 : -1);
			worldIn.updateBlockTick(pos, this, getTickDelay(state), b0);
		}
	}

	protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
		return calculateInputStrength(worldIn, pos, state) > 0;
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		return getInputSignal().calculateInputStrength(worldIn, pos, getInput(state));
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		getInputSignal().notifyNeighbors(worldIn, pos, state, this);
		if(getInputSignal() != getOutputSignal())
			getOutputSignal().notifyNeighbors(worldIn, pos, state, this);
	}

	protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return 15;
	}

	protected int getTickDelay(IBlockState state) {
		return 2;
	}

	@Override
	public IBlockState getPoweredState(IBlockState unpoweredState) {
		IBlockState poweredState = getPoweredBlock().getDefaultState();
		for(Object o : unpoweredState.getProperties().keySet()) {
			IProperty p = (IProperty)o;
			poweredState = poweredState.withProperty(p, unpoweredState.getValue(p));
		}
		return poweredState;
	}

	@Override
	public IBlockState getUnpoweredState(IBlockState poweredState) {
		IBlockState unpoweredState = getUnpoweredBlock().getDefaultState();
		for(Object o : poweredState.getProperties().keySet()) {
			IProperty p = (IProperty)o;
			unpoweredState = unpoweredState.withProperty(p, poweredState.getValue(p));
		}
		return unpoweredState;
	}

	@Override
	public Block getBlock() {
		return this;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		boolean flag = shouldBePowered(worldIn, pos, state);
		if(isPowered && !flag)
			worldIn.setBlockState(pos, getUnpoweredState(state), 2);
		 else if(!isPowered) {
			worldIn.setBlockState(pos, getPoweredState(state), 2);
			if(!flag)
				worldIn.updateBlockTick(pos, getPoweredState(state).getBlock(), getTickDelay(state), -1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return side.getAxis() != EnumFacing.Axis.Y;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return isProvidingWeakPower(worldIn, pos, state, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return canProvidePower() && isPowered(state) && getOutput(state).getOpposite() == side ? getActiveSignal(
				worldIn, pos, state)                                                           : 0;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(canBlockStay(worldIn, pos)) {
			updateState(worldIn, pos, state);
		} else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			for(EnumFacing facing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
		}
	}

	@Override
	public boolean canProvidePower() {
		return getOutputSignal().canProvidePower;
	}

	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(
			World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(shouldBePowered(worldIn, pos, state))
			worldIn.scheduleUpdate(pos, this, 1);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		if(isPowered)
			for(EnumFacing facing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isAssociatedBlock(Block other) {
		return other == getPoweredState(getDefaultState()).getBlock() ||
				other == getUnpoweredState(getDefaultState()).getBlock();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(getUnpoweredBlock());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(getUnpoweredBlock());
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		notifyNeighbors(worldIn, pos, state);
	}
}
