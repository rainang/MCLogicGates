package com.github.rainang.endereyefi;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRepeater extends BlockDiode {

	public static final PropertyDirection FACING = BlockDirectional.FACING;
	public static final PropertyBool      LOCKED = BlockRedstoneRepeater.LOCKED;
	public static final PropertyInteger   DELAY  = BlockRedstoneRepeater.DELAY;

	public final int type;
	public final int out;

	protected BlockRepeater(boolean powered, int type, int out) {
		super(Material.circuits, powered);
		this.type = type;
		this.out = out;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		setLightLevel(powered ? 0.25f : 0);
		if(!powered && out == 1)
			setCreativeTab(CreativeTabs.tabRedstone);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setUnlocalizedName("repeater_" + (type == 0 ? "rr" : type == 1 ? "er" : type == 2 ? "re" : "ee") +
				(out == 0 ? "l" : out == 2 ? "r" : "") +
				(powered ? "_on" : ""));
	}

	protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		int power = 0;
		boolean flag = isEnderReceiver();
		for(EnumFacing side : getSides(state)) {
			IBlockState sideState;
			Block sideBlock;
			if(flag)
				for(int i = 0; i < 16; i++) {
					sideState = worldIn.getBlockState(pos.offset(side, i + 1));
					sideBlock = sideState.getBlock();
					if(sideBlock instanceof BlockRepeater) {
						BlockRepeater br = (BlockRepeater)sideBlock;
						if(br.isEnderTransmitter() &&
								br.getOutputSide(sideState) == side.getOpposite() &&
								br.isPowered)
							return 15;
						if(br.isEnderReceiver())
							break;
					} else if(sideBlock instanceof BlockCasterEye)
						return 15;
				}
			else {
				sideState = worldIn.getBlockState(pos.offset(side));
				sideBlock = sideState.getBlock();

				if(sideBlock instanceof BlockRepeater) {
					BlockRepeater br = (BlockRepeater)sideBlock;
					if(br.isPowered && !br.isEnderTransmitter() &&
							br.getOutputSide(sideState) == side.getOpposite())
						return 15;
				} else if(BlockRedstoneRepeater.isRedstoneRepeaterBlockID(sideBlock) &&
						sideBlock != Blocks.unpowered_repeater &&
						!isEnderReceiver() && sideState.getValue(FACING) == side)
					power = worldIn.getStrongPower(pos.offset(side), side);
			}
		}

		return power;
	}

	public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return getPowerOnSides(worldIn, pos, state) > 0;
	}

	public EnumFacing[] getSides(IBlockState state) {
		EnumFacing enumfacing = getInputSide(state);
		EnumFacing[] sides = new EnumFacing[2];
		int i = 0;
		for(EnumFacing f : EnumFacing.HORIZONTALS)
			if(!f.equals(enumfacing) && !f.equals(getOutputSide(state)))
				sides[i++] = f;
		return sides;
	}

	/* Block override */

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit) {
			return false;
		} else {
			if(playerIn.isSneaking()) {
				Block b = EnderBlocks.REPEATERS[isPowered ? 1 : 0][type][(out + 1)%3];
				worldIn.setBlockState(pos, b.getDefaultState()
						.withProperty(FACING, state.getValue(FACING))
						.withProperty(DELAY, state.getValue(DELAY)));
			} else
				worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
			return true;
		}
	}

	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onNeighborBlockChange(
			World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
			super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
		else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			notifyNeighbors(worldIn, pos, state);
		}
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, FACING, DELAY, LOCKED);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(LOCKED, isLocked(worldIn, pos, state));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(meta))
				.withProperty(LOCKED, false)
				.withProperty(DELAY, 1 + (meta>>2));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		i |= (Integer)state.getValue(DELAY) - 1<<2;
		return i;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(EnderBlocks.REPEATERS[0][type][1]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(EnderBlocks.REPEATERS[0][type][1]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(isPowered) {
			EnumFacing enumfacing = getInputSide(state);
			double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2;
			double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2;
			double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2;
			float f = -5.0F;

			EnumParticleTypes particle = isEnderTransmitter() ? EnumParticleTypes.PORTAL : EnumParticleTypes.REDSTONE;
			if(rand.nextBoolean()) {
				particle = isEnderReceiver() ? EnumParticleTypes.PORTAL : EnumParticleTypes.REDSTONE;
				f = (float)((Integer)state.getValue(DELAY)*2 - 1);
			}

			f /= 16.0F;
			double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
			double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
			worldIn.spawnParticle(particle, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return side.getAxis() != EnumFacing.Axis.Y;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos);
	}

	/* BlockDiode impl */

	@Override
	public int getTickDelay(IBlockState state) {
		return (Integer)state.getValue(DELAY)*2;
	}

	@Override
	public boolean isEnderTransmitter() {
		return type >= 2;
	}

	@Override
	public boolean isEnderReceiver() {
		return type%2 == 1;
	}

	@Override
	public IBlockState getPoweredState(IBlockState state) {
		Integer integer = (Integer)state.getValue(DELAY);
		Boolean bool = (Boolean)state.getValue(LOCKED);
		EnumFacing enumfacing = getInputSide(state);
		return EnderBlocks.REPEATERS[1][type][out].getDefaultState()
				.withProperty(FACING, enumfacing)
				.withProperty(DELAY, integer)
				.withProperty(LOCKED, bool);
	}

	@Override
	public IBlockState getUnpoweredState(IBlockState state) {
		Integer integer = (Integer)state.getValue(DELAY);
		Boolean bool = (Boolean)state.getValue(LOCKED);
		EnumFacing enumfacing = getInputSide(state);
		return EnderBlocks.REPEATERS[0][type][out].getDefaultState()
				.withProperty(FACING, enumfacing)
				.withProperty(DELAY, integer)
				.withProperty(LOCKED, bool);
	}

	@Override
	public EnumFacing getInputSide(IBlockState state) {
		return (EnumFacing)state.getValue(FACING);
	}

	@Override
	public EnumFacing getOutputSide(IBlockState state) {
		EnumFacing facing = getInputSide(state);
		return out == 0 ? facing.rotateY() : out == 1 ? facing.getOpposite() : out == 2 ? facing.rotateYCCW() : facing;
	}
}
