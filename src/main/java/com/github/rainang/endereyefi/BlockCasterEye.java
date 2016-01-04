package com.github.rainang.endereyefi;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCasterEye extends Block {

	public BlockCasterEye() {
		super(Material.rock);
		setUnlocalizedName("caster_eye");
		setCreativeTab(EnderEyeFi.TAB_EYE);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		notifyNeighbors(worldIn, pos);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		notifyNeighbors(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	protected void notifyNeighbors(World worldIn, BlockPos pos) {
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
	}
}
