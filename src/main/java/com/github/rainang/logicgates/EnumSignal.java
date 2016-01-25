package com.github.rainang.logicgates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public enum EnumSignal implements IStringSerializable {
	REDSTONE(true, EnumParticleTypes.REDSTONE, new SignalCalculator() {

		@Override
		public int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side) {
			BlockPos offsetPos = pos.offset(side);
			int i = worldIn.getRedstonePower(offsetPos, side);

			if(i >= 15)
				return i;
			else {
				IBlockState offsetState = worldIn.getBlockState(offsetPos);
				return Math.max(i, offsetState.getBlock() == Blocks.redstone_wire ? (Integer)offsetState.getValue(
						BlockRedstoneWire.POWER)                                  : 0);
			}
		}

		@Override
		public int getPowerFromSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
			IBlockState offsetState = worldIn.getBlockState(pos.offset(side));
			Block offsetBlock = offsetState.getBlock();
			if(offsetBlock instanceof IDiode) {
				IDiode diode = (IDiode)worldIn.getBlockState(pos).getBlock();
				IDiode offsetDiode = (IDiode)offsetState.getBlock();
				if(offsetDiode.getOutputSignal() == diode.getInputSignal())
					return offsetDiode.isPowered(offsetState) &&
								   offsetDiode.getOutput(offsetState) == side.getOpposite() ? 15 : 0;
			}
			return offsetBlock == Blocks.redstone_wire ? (Integer)offsetState.getValue(BlockRedstoneWire.POWER)
													   : worldIn.getStrongPower(pos.offset(side), side);
		}

		@Override
		public BlockPos getNearestReceiver(IBlockAccess worldIn, BlockPos pos, IDiode diode) {
			EnumFacing side = diode.getOutput(worldIn.getBlockState(pos));
			BlockPos offsetPos = pos.offset(side);
			IBlockState offsetState = worldIn.getBlockState(offsetPos);
			Block offsetBlock = offsetState.getBlock();

			if(BlockRedstoneRepeater.isRedstoneRepeaterBlockID(offsetBlock) &&
					offsetState.getValue(BlockDirectional.FACING) != side)
				return offsetPos;

			if(offsetBlock instanceof IDiode) {
				IDiode offsetDiode = (IDiode)offsetBlock;
				if(diode.getBlock().isAssociatedBlock(offsetBlock) &&
						diode.getOutput(worldIn.getBlockState(pos)) != offsetDiode.getOutput(offsetState)
								.getOpposite())
					return offsetPos;
			}
			return null;
		}

		@Override
		public void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state, IDiode diode) {
			EnumFacing out = diode.getOutput(state);
			BlockPos offsetPos = pos.offset(out);
			if(net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos),
					java.util.EnumSet.of(out)).isCanceled())
				return;
			worldIn.notifyBlockOfStateChange(offsetPos, state.getBlock());
			worldIn.notifyNeighborsOfStateExcept(offsetPos, state.getBlock(), out.getOpposite());
		}
	}),
	ENDER(false, EnumParticleTypes.PORTAL, new SignalCalculator() {

		@Override
		public int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side) {
			int power = getPowerFromSide(worldIn, pos, side);
			if(power > 0)
				return power;
			power = getPowerFromSide(worldIn, pos, EnumFacing.UP);
			if(power > 0)
				return power;
			return getPowerFromSide(worldIn, pos, EnumFacing.DOWN);
		}

		@Override
		public int getPowerFromSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
			IDiode diode = (IDiode)worldIn.getBlockState(pos).getBlock();
			for(int i = 1; i < 16; i++) {
				IBlockState offsetState = worldIn.getBlockState(pos.offset(side, i));
				if(offsetState.getBlock() instanceof IDiode) {
					IDiode offsetDiode = (IDiode)offsetState.getBlock();
					if(offsetDiode.getOutputSignal() == diode.getInputSignal())
						return offsetDiode.isPowered(offsetState) &&

									   offsetDiode.getOutput(offsetState) == side.getOpposite() ? 15 : 0;
				}
			}
			return 0;
		}

		@Override
		public BlockPos getNearestReceiver(IBlockAccess worldIn, BlockPos pos, IDiode diode) {
			EnumFacing side = diode.getOutput(worldIn.getBlockState(pos));
			for(int i = 1; i < 16; i++) {
				BlockPos offsetPos = pos.offset(side, i);
				IBlockState offsetState = worldIn.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();
				if(offsetBlock instanceof IDiode) {
					IDiode offsetDiode = (IDiode)offsetBlock;
					if(diode.getBlock().isAssociatedBlock(offsetBlock) && diode.getOutput(worldIn.getBlockState(pos)
					) !=
							offsetDiode.getOutput(offsetState).getOpposite())
						return offsetPos;
					break;
				}
			}
			return null;
		}

		@Override
		public void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state, IDiode diode) {
			for(EnumFacing facing : EnumFacing.VALUES)
				for(int i = 1; i < 16; i++) {
					BlockPos offsetPos = pos.offset(facing, i);
					IBlockState offsetState = worldIn.getBlockState(offsetPos);
					if(offsetState.getBlock() instanceof IDiode) {
						IDiode offsetDiode = (IDiode)offsetState.getBlock();
						if(offsetDiode.getInputSignal() == ENDER) {
							worldIn.notifyBlockOfStateChange(offsetPos, diode.getBlock());
							worldIn.notifyNeighborsOfStateExcept(offsetPos, diode.getBlock(), facing);
							break;
						}
					}
				}
		}
	});

	public final boolean           canProvidePower;
	public final EnumParticleTypes particleType;

	private final SignalCalculator signalCalculator;

	EnumSignal(boolean canProvidePower, EnumParticleTypes particleType, SignalCalculator signalCalculator) {
		this.canProvidePower = canProvidePower;
		this.particleType = particleType;
		this.signalCalculator = signalCalculator;
	}

	public int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side) {
		return signalCalculator.calculateInputStrength(worldIn, pos, side);
	}

	public int getPowerFromSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return signalCalculator.getPowerFromSide(worldIn, pos, side);
	}

	public BlockPos getNearestReceiver(IBlockAccess worldIn, BlockPos pos, IDiode diode) {
		return signalCalculator.getNearestReceiver(worldIn, pos, diode);
	}

	public void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state, IDiode diode) {
		signalCalculator.notifyNeighbors(worldIn, pos, state, diode);
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	public interface SignalCalculator {

		void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state, IDiode diode);

		int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side);

		int getPowerFromSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side);

		BlockPos getNearestReceiver(IBlockAccess worldIn, BlockPos pos, IDiode diode);
	}
}
