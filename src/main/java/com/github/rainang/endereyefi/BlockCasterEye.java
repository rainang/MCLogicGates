package com.github.rainang.endereyefi;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCasterEye extends BlockDiode {

	public BlockCasterEye() {
		super(Material.ground, true);
		setUnlocalizedName("caster_eye");
		setCreativeTab(EnderEyeFi.TAB_EYE);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public int getTickDelay(IBlockState state) {
		return 0;
	}

	@Override
	public boolean isEnderTransmitter() {
		return true;
	}

	@Override
	public boolean isEnderReceiver() {
		return true;
	}

	@Override
	public IBlockState getPoweredState(IBlockState state) {
		return state;
	}

	@Override
	public IBlockState getUnpoweredState(IBlockState state) {
		return state;
	}

	@Override
	public EnumFacing getInputSide(IBlockState state) {
		return null;
	}

	@Override
	public EnumFacing getOutputSide(IBlockState state) {
		return null;
	}

	@Override
	protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
		for(EnumFacing facing : EnumFacing.values()) {
			for(int i = 0; i < 16; i++) {
				BlockPos offset = pos.offset(facing, i + 1);
				IBlockState offsetState = worldIn.getBlockState(offset);
				BlockDiode block = getAsDiode(offsetState);
				boolean flag1 = i == 0;
				boolean flag2 = block != null && block.isEnderReceiver();
				if(flag1 || flag2) {
					worldIn.notifyBlockOfStateChange(offset, this);
					worldIn.notifyNeighborsOfStateChange(offset, this);
				}
				if(flag2)
					break;
			}
		}
	}
}
