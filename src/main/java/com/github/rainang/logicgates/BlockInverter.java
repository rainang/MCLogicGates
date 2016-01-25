package com.github.rainang.logicgates;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInverter extends BlockRepeater {

	private final EnumSignal signal;

	protected BlockInverter(EnumSignal signal, boolean powered) {
		super(signal, signal, powered);
		setUnlocalizedName(signal.name().toLowerCase() + "_inverter" + (powered ? "_on" : ""));
		this.signal = signal;
	}

	public boolean isPowered(IBlockState state) {
		return !isPowered;
	}

	@Override
	public Block getPoweredBlock() {
		return LogicGates.inverters[1][getInputSignal().ordinal()];
	}

	@Override
	public Block getUnpoweredBlock() {
		return LogicGates.inverters[0][getInputSignal().ordinal()];
	}

	@Override
	public EnumSignal getInputSignal() {
		return signal;
	}

	@Override
	public EnumSignal getOutputSignal() {
		return signal;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F)*0.2D;
		float f = -5.0F;

		if(isPowered)
			f = (float)((Integer)state.getValue(DELAY)*2 - 1);

		f /= 16.0F;
		double d3 = (double)(f*(float)enumfacing.getFrontOffsetX());
		double d4 = (double)(f*(float)enumfacing.getFrontOffsetZ());
		worldIn.spawnParticle(signal.particleType, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
	}
}