package com.github.rainang.endereyefi;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCasterEye extends BlockDiode {

	public BlockCasterEye() {
		super(Material.ground, true, 3);
		setUnlocalizedName("caster_eye");
		setCreativeTab(EnderEyeFi.TAB_EYE);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos, state);
	}

	@Override
	protected boolean shouldBeActive(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public IBlockState getActiveState(IBlockState state) {
		return state;
	}

	@Override
	public IBlockState getPassiveState(IBlockState state) {
		return state;
	}

	@Override
	public EnumFacing getInput(IBlockState state) {
		return null;
	}

	@Override
	public EnumFacing getOutput(IBlockState state) {
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
