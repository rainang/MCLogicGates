package com.github.rainang.endereyefi;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRepeater extends BlockRedstoneRepeater {

	public final int type;
	public final int out;

	protected BlockRepeater(boolean powered, int type, int out) {
		super(powered);
		this.type = type;
		this.out = out;
		setLightLevel(powered ? 0.25f : 0);
		if(!powered && out == 1)
			setCreativeTab(CreativeTabs.tabRedstone);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setUnlocalizedName((type == 0 ? "rr" : type == 1 ? "er" : type == 2 ? "re" : "ee") + "_repeater_" +
								   (out == 0 ? "l" : out == 2 ? "r" : "s") + (powered ? "_on" : ""));
	}

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit) {
			return false;
		} else {
			if(playerIn.isSneaking())
				worldIn.setBlockState(pos, EnderEyeFi.getRepeater(isRepeaterPowered ? 1 : 0, type, (out + 1)%3)
													 .getDefaultState().withProperty(FACING, state.getValue(FACING))
													 .withProperty(DELAY, state.getValue(DELAY)));
			else
				worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
			return true;
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(calculateInputStrength(worldIn, pos, state) > 0)
			worldIn.setBlockState(pos, getPoweredState(state));
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(canBlockStay(worldIn, pos))
			updateState(worldIn, pos, state);
		else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			notifyNeighbors(worldIn, pos, state);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(isRepeaterPowered)
			notifyNeighbors(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return isRepeaterPowered && !isEnderTransmitter() && side.getOpposite() == getOutputSide(state) ? 15 : 0;
	}

	@Override
	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		if(isEnderTransmitter() || isEnderReceiver())
			for(EnumFacing facing : EnumFacing.HORIZONTALS)
				for(int i = 0; i < 16; i++) {
					BlockPos offset = pos.offset(facing, i + 1);
					IBlockState offsetState = worldIn.getBlockState(offset);
					if(offsetState.getBlock() instanceof BlockRepeater &&
							((BlockRepeater)offsetState.getBlock()).isEnderReceiver()) {
						worldIn.notifyBlockOfStateChange(offset, this);
						worldIn.notifyNeighborsOfStateChange(offset, this);
						break;
					}
				}
		else {
			EnumFacing facing = getOutputSide(state);
			BlockPos output = pos.offset(facing);
			worldIn.notifyBlockOfStateChange(output, this);
			worldIn.notifyNeighborsOfStateExcept(output, this, facing.getOpposite());
		}
	}

	@Override
	protected IBlockState getPoweredState(IBlockState state) {
		Integer integer = (Integer)state.getValue(DELAY);
		Boolean bool = (Boolean)state.getValue(LOCKED);
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		return EnderEyeFi.getRepeater(1, type, out).getDefaultState().withProperty(FACING, enumfacing)
						 .withProperty(DELAY, integer).withProperty(LOCKED, bool);
	}

	@Override
	protected IBlockState getUnpoweredState(IBlockState state) {
		Integer integer = (Integer)state.getValue(DELAY);
		Boolean bool = (Boolean)state.getValue(LOCKED);
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		return EnderEyeFi.getRepeater(0, type, out).getDefaultState().withProperty(FACING, enumfacing)
						 .withProperty(DELAY, integer).withProperty(LOCKED, bool);
	}

	@Override
	protected boolean canPowerSide(Block blockIn) {
		return (isRedstoneRepeaterBlockID(blockIn) && type%2 == 0) || (blockIn instanceof BlockRepeater && type%2 ==
				1);
	}

	@Override
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
						if(br.isEnderTransmitter() && br.getOutputSide(sideState) == side.getOpposite() &&
								br.isRepeaterPowered)
							return 15;
						if(br.isEnderReceiver())
							break;
					}
				}
			else {
				sideState = worldIn.getBlockState(pos.offset(side));
				sideBlock = sideState.getBlock();

				if(sideBlock instanceof BlockRepeater) {
					BlockRepeater br = (BlockRepeater)sideBlock;
					if(br.isRepeaterPowered && !br.isEnderTransmitter() &&
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

	@Override
	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing facing = (EnumFacing)state.getValue(FACING);

		if(!isEnderReceiver()) {
			IBlockState inputState = worldIn.getBlockState(pos.offset(facing));
			Block inputBLock = inputState.getBlock();
			if(inputBLock instanceof BlockRepeater) {
				BlockRepeater input = (BlockRepeater)inputBLock;
				return !input.isEnderTransmitter() && input.getOutputSide(inputState) == facing.getOpposite() &&
							   input.isRepeaterPowered ? 15 : 0;
			}
			return super.calculateInputStrength(worldIn, pos, state);
		} else {
			for(int i = 0; i < 16; i++) {
				BlockPos offset = pos.offset(facing, i + 1);
				IBlockState inputState = worldIn.getBlockState(offset);
				if(inputState.getBlock() instanceof BlockRepeater) {
					BlockRepeater input = (BlockRepeater)inputState.getBlock();
					if(input.getOutputSide(inputState) == facing.getOpposite()) {
						if(input.isEnderTransmitter() && input.isRepeaterPowered)
							return 15;
					} else if(input.isEnderReceiver()) {
						break;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(EnderEyeFi.getRepeater(0, type, out));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(EnderEyeFi.getRepeater(0, type, out));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(this.isRepeaterPowered) {
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
			double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
			float f = -5.0F;

			EnumParticleTypes particle = type < 2 ? EnumParticleTypes.REDSTONE : EnumParticleTypes.PORTAL;
			if(rand.nextBoolean()) {
				particle = type%2 == 0 ? EnumParticleTypes.REDSTONE : EnumParticleTypes.PORTAL;
				f = (float)((Integer)state.getValue(DELAY)*2 - 1);
			}

			f /= 16.0F;
			double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
			double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
			worldIn.spawnParticle(particle, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}
	}

	public EnumFacing getOutputSide(IBlockState state) {
		EnumFacing facing = (EnumFacing)state.getValue(FACING);
		return out == 0 ? facing.rotateY() : out == 1 ? facing.getOpposite() : out == 2 ? facing.rotateYCCW() : facing;
	}

	public EnumFacing[] getSides(IBlockState state) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		EnumFacing[] sides = new EnumFacing[2];
		int i = 0;
		for(EnumFacing f : EnumFacing.HORIZONTALS)
			if(!f.equals(enumfacing) && !f.equals(getOutputSide(state)))
				sides[i++] = f;
		return sides;
	}

	public boolean isEnderReceiver() {
		return type%2 == 1;
	}

	public boolean isEnderTransmitter() {
		return type >= 2;
	}
}
