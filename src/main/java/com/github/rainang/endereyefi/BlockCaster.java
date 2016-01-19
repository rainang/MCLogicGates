package com.github.rainang.endereyefi;

import com.google.common.base.Predicate;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCaster extends BlockDiode {

	public static final PropertyDirection IN_POS = PropertyDirection.create("in", new Predicate() {
		@Override
		public boolean apply(@Nullable Object o) {
			return o != null && ((EnumFacing)o).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE;
		}
	});

	public static final PropertyDirection IN_NEG = PropertyDirection.create("in", new Predicate() {
		@Override
		public boolean apply(@Nullable Object o) {
			return o != null && ((EnumFacing)o).getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
		}
	});

	public static final PropertyDirection OUT = PropertyDirection.create("out");

	public BlockCaster(int type, boolean isActive) {
		super(Material.ground, isActive, type);
		boolean neg = isNegative();

		if(!neg && !isActive)
			setCreativeTab(EnderEyeFi.TAB_EYE);
		setLightLevel(isActive ? 0.25f : 0);
		EnumFacing facing = neg ? EnumFacing.DOWN : EnumFacing.UP;
		setDefaultState(getBlockState().getBaseState()
				.withProperty(getInProperty(), facing)
				.withProperty(OUT, facing.getOpposite()));
		setResistance(10.0F);

		String name = "";
		name += type == 0 ? "caster_rr" : type == 1 ? "caster_er" : type == 2 ? "caster_re" : "caster_ee";
		name += neg ? "_neg" : "";
		name += isActive ? "_on" : "";
		setUnlocalizedName(name);

		setBlockBounds(0.0F, 0.0F, 0.0F, 1, 1, 1);
	}

	public static class BlockCasterNeg extends BlockCaster {

		public BlockCasterNeg(int type, boolean isActive) {
			super(type, isActive);
		}

		@Override
		public PropertyDirection getInProperty() {
			return IN_NEG;
		}
	}

	public boolean isOnThisAxisDirection(EnumFacing facing) {
		return (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) == isNegative();
	}

	public boolean isNegative() {
		return this instanceof BlockCasterNeg;
	}

	public PropertyDirection getInProperty() {
		return IN_POS;
	}

	/**
	 * Gets block state with the given input and output.
	 * <p/>
	 * Note: This does not allow input and output to be the same. In the event that the given parameters are the
	 * same, the output is set to opposite the input.
	 *
	 * @param in
	 * 		the new input side
	 * @param out
	 * 		the new output side
	 *
	 * @return the new block state
	 */
	protected IBlockState getStateWithIO(EnumFacing in, EnumFacing out) {
		if(in == out)
			out = in.getOpposite();
		BlockCaster block = this;
		if(!isOnThisAxisDirection(in))
			block = EnderBlocks.getEnderDiode(type, !isNegative(), isActive);
		return block.getDefaultState().withProperty(block.getInProperty(), in).withProperty(OUT, out);
	}

	/* Block override */

	/** Sets the block's input and/or output on activation */
	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;
		EnumFacing in = getInput(state);
		EnumFacing out = getOutput(state);

		if(playerIn.isSneaking()) {
			if(in == side) // move output opposite input
				out = side.getOpposite();
			else if(out == side) // move input opposite output
				in = side.getOpposite();
			else // move input
				in = side;
		} else if(in == side || out == side) { // invert io
			EnumFacing facing = in;
			in = out;
			out = facing;
		} else // move output
			out = side;

		state = getStateWithIO(in, out);

		// check for power and change to appropriate state
		boolean shouldPower = getAsDiode(state).shouldBeActive(worldIn, pos, state);
		if(shouldPower && !isActive)
			state = getActiveState(state);
		else if(!shouldPower && isActive)
			state = getPassiveState(state);

		// set and notify
		worldIn.setBlockState(pos, state);
		notifyNeighbors(worldIn, pos, state);
		return true;
	}

	/** Retrieves the block's state with input and output based on player behaviour and block hit */
	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		IBlockState hitState = worldIn.getBlockState(pos.offset(facing.getOpposite()));
		EnumFacing in = EnumFacing.SOUTH;
		EnumFacing out = EnumFacing.NORTH;

		boolean flag = false;
		if(hitState.getBlock() instanceof BlockDiode) {
			BlockDiode hitBlock = (BlockDiode)hitState.getBlock();

			// try to connect to input
			if(hitBlock.getInput(hitState) == facing && hitBlock.canReceiveSignalFrom(this)) {
				in = placer.getHorizontalFacing().getOpposite();
				out = facing.getOpposite();
				flag = true;
			} else // try to connect to output
				if(hitBlock.getOutput(hitState) == facing && canReceiveSignalFrom(hitBlock)) {
					in = facing.getOpposite();
					out = placer.getHorizontalFacing().getOpposite();
					flag = true;
				}
		}

		if(!flag)
			if(!placer.isSneaking()) { // place based on player facing
				in = placer.getHorizontalFacing().getOpposite();
				out = placer.getHorizontalFacing();
			} else { // connect input to block face
				in = facing.getOpposite();
				out = placer.getHorizontalFacing().getOpposite();
			}

		return getStateWithIO(in, out);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, getInProperty(), OUT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if(meta == 0)
			return getDefaultState().withProperty(getInProperty(), EnumFacing.UP).withProperty(OUT, EnumFacing.DOWN);
		EnumFacing.Axis axis = EnumFacing.Axis.values()[((meta - 1)/5 + 1)%3];
		EnumFacing socket = null;
		int i = (meta - 1)%5;
		for(EnumFacing f : EnumFacing.VALUES)
			if(f.getAxis() == axis && isOnThisAxisDirection(f)) {
				socket = f;
				if(socket.ordinal() <= i)
					i++;
				break;
			}

		return getDefaultState().withProperty(getInProperty(), socket).withProperty(OUT, EnumFacing.values()[i]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing socket1 = (EnumFacing)state.getValue(getInProperty());
		EnumFacing socket2 = (EnumFacing)state.getValue(OUT);
		return (socket1.getAxis().ordinal() + 2)%3*5 + socket2.ordinal() +
				(socket1.ordinal() < socket2.ordinal() ? 0 : 1);
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(EnderBlocks.getEnderDiode(type, false, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(EnderBlocks.getEnderDiode(type, false, false));
	}

	/* BlockDiode impl */

	@Override
	public IBlockState getActiveState(IBlockState state) {
		return getAsDiode(state).isActive ? state : EnderBlocks.getEnderDiode(type, isNegative(), true)
				.getDefaultState()
				.withProperty(getInProperty(), state.getValue(getInProperty()))
				.withProperty(OUT, state.getValue(OUT));
	}

	@Override
	public IBlockState getPassiveState(IBlockState state) {
		return !getAsDiode(state).isActive ? state : EnderBlocks.getEnderDiode(type, isNegative(), false)
				.getDefaultState()
				.withProperty(getInProperty(), state.getValue(getInProperty()))
				.withProperty(OUT, state.getValue(OUT));
	}

	@Override
	public EnumFacing getInput(IBlockState state) {
		return (EnumFacing)state.getValue(getInProperty());
	}

	@Override
	public EnumFacing getOutput(IBlockState state) {
		return (EnumFacing)state.getValue(OUT);
	}
}
