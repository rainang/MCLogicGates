package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.Gate;
import com.github.rainang.logicgates.Signal;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockDiode extends Block {

	public static final PropertyDirection OUT = PropertyDirection.create("out", EnumFacing.Plane.HORIZONTAL);

	protected final Gate gate;

	public BlockDiode(Gate gate) {
		super(Material.circuits);
		this.gate = gate;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	public abstract BlockDiode getBaseBlock();

	public abstract Signal getSignal(IBlockState state);

	public abstract PropertyInteger getInputProperty();

	public abstract EnumFacing getInput(IBlockState state, int index);

	public abstract List<EnumFacing> getInputs(IBlockState state);

	public abstract int getInputState(IBlockState state);

	public abstract int getInputCount();

	public abstract IBlockState rotate(IBlockState state);

	public IBlockState setInputState(IBlockState state, int input) {
		return state.withProperty(getInputProperty(), input);
	}

	public EnumFacing getOutput(IBlockState state) {
		return state.getValue(OUT);
	}

	public boolean isActive(IBlockState state) {
		return gate.validate(getInputCount(), getInputState(state));
	}

	public int getPoweredState() {
		int state = 0;
		for(int i = 0; i < getInputCount(); i++)
			state |= 1<<i;
		return state;
	}

	public int validateInputState(World world, BlockPos pos, IBlockState state) {
		List<EnumFacing> inputs = getInputs(state);
		int inputState = 0;
		for(int i = 0; i < inputs.size(); i++)
			if(getPowerFromSide(world, pos, inputs.get(i), getSignal(state)) > 0)
				inputState |= 1<<i;
		inputState = Math.min(15, inputState);
		return getInputState(state) == inputState ? -1 : inputState;
	}

	public int getPowerFromSide(World world, BlockPos pos, EnumFacing side, Signal signal) {
		return signal == Signal.REDSTONE ? getRedstoneInput(world, pos, side) : getEnderInput(world, pos, side);
	}

	public int getRedstoneInput(World worldIn, BlockPos pos, EnumFacing side) {
		BlockPos sidePos = pos.offset(side);
		int i = worldIn.getRedstonePower(sidePos, side);

		if(i >= 15)
			return i;

		IBlockState sideState = worldIn.getBlockState(sidePos);
		int wire = sideState.getBlock() == Blocks.redstone_wire ? sideState.getValue(BlockRedstoneWire.POWER) : 0;
		return Math.max(i, wire);
	}

	public int getEnderInput(World world, BlockPos pos, EnumFacing side) {
		int i = getEnderNeighborDistanceFromSide(world, pos, side);
		if(i == 0)
			return i;
		IBlockState state = world.getBlockState(pos.offset(side, i));
		BlockDiode diode = (BlockDiode)state.getBlock();
		if(diode instanceof BlockDiodeConverter && diode.getSignal(state) == Signal.ENDER)
			return 0;
		return diode.isActive(state) && diode.getOutput(state).getOpposite() == side ? i : 0;
	}

	public int getEnderNeighborDistanceFromSide(World world, BlockPos pos, EnumFacing side) {
		for(int i = 1; i < Signal.ENDER.range; i++) {
			IBlockState sideState = world.getBlockState(pos.offset(side, i));
			if(sideState.getBlock() instanceof BlockDiode) {
				BlockDiode diode = (BlockDiode)sideState.getBlock();
				if(diode instanceof BlockDiodeConverter || diode.getSignal(sideState) == Signal.ENDER)
					return i;
			}
		}
		return 0;
	}

	public int[] getEnderNeighborDistances(World world, BlockPos pos) {
		int[] distances = new int[6];
		for(EnumFacing f : EnumFacing.values())
			distances[f.ordinal()] = getEnderNeighborDistanceFromSide(world, pos, f);
		return distances;
	}

	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		int validate = validateInputState(worldIn, pos, state);
		if(validate != -1 && !worldIn.isBlockTickPending(pos, this)) {
			byte b0 = (byte)(validate == getPoweredState() ? -2 : -1);
			worldIn.updateBlockTick(pos, this, /* delay */ 2, b0);
		}
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		if(getSignal(state) == Signal.REDSTONE)
			notifyRedstoneNeighbors(worldIn, pos, state);
		else
			notifyEnderNeighbors(worldIn, pos);
	}

	protected void notifyEnderNeighbors(World worldIn, BlockPos pos) {
		int[] distances = getEnderNeighborDistances(worldIn, pos);
		for(int i = 0; i < distances.length; i++) {
			if(distances[i] == 0)
				continue;
			BlockPos sidePos = pos.offset(EnumFacing.values()[i], distances[i]);
			worldIn.notifyBlockOfStateChange(sidePos, this);
			worldIn.notifyNeighborsOfStateChange(sidePos, this);
		}
	}

	protected void notifyRedstoneNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing out = getOutput(state);
		BlockPos sidePos = pos.offset(out);
		if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos),
				java.util.EnumSet.of(out)).isCanceled())
			return;
		worldIn.notifyBlockOfStateChange(sidePos, state.getBlock());
		worldIn.notifyNeighborsOfStateExcept(sidePos, state.getBlock(), out.getOpposite());
	}

	protected EnumParticleTypes getParticleType(IBlockState state) {
		return getSignal(state) == Signal.REDSTONE ? EnumParticleTypes.REDSTONE : EnumParticleTypes.PORTAL;
	}
	
	/* BLOCK OVERRIDE */

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;

		IBlockState newState = rotate(state);
		BlockDiode block = (BlockDiode)newState.getBlock();
		worldIn.setBlockState(pos, newState, 3);
		block.updateState(worldIn, pos, newState);

		return true;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		int validate = validateInputState(worldIn, pos, state);
		if(validate == -1)
			return;
		state = setInputState(state, validate);
		worldIn.setBlockState(pos, state);
		notifyNeighbors(worldIn, pos, state);
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
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return side.getAxis() != EnumFacing.Axis.Y;
	}

	@Override
	public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return getWeakPower(worldIn, pos, state, side);
	}

	@Override
	public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return getSignal(state) == Signal.REDSTONE && isActive(state) && getOutput(state).getOpposite() == side ? 15
																												: 0;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
			updateState(worldIn, pos, state);
		else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			for(EnumFacing facing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
		}
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return getStateFromMeta(meta).withProperty(OUT, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(
			World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(validateInputState(worldIn, pos, state) != -1)
			worldIn.scheduleUpdate(pos, this, 1);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyEnderNeighbors(worldIn, pos);
		notifyRedstoneNeighbors(worldIn, pos, state);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		if(isActive(state))
			for(EnumFacing facing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this);
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(getBaseBlock());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(getBaseBlock());
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(isActive(state)) {
			EnumParticleTypes particleType = getParticleType(state);
			EnumFacing enumfacing = getOutput(state);
			double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			float f = 5.0F;
			f /= 16.0F;
			double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
			double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
			worldIn.spawnParticle(particleType, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, OUT, getInputProperty());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(OUT, EnumFacing.getHorizontal(meta))
				.withProperty(getInputProperty(), meta>>2);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|state.getValue(OUT).getHorizontalIndex();
		i |= state.getValue(getInputProperty())<<2;
		return i;
	}
}
