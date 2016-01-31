package com.github.rainang.logicgates.block;

import com.github.rainang.logicgates.Gate;
import com.github.rainang.logicgates.Signal;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockDiode1In extends BlockDiode {

	public static final PropertyInteger INPUT  = PropertyInteger.create("input", 0, 1);
	public static final PropertyEnum    SIGNAL = PropertyEnum.create("signal", Signal.class);

	protected final int type;

	public BlockDiode1In(Gate gate, int type) {
		super(gate);
		this.type = type;
		setDefaultState(blockState.getBaseState()
				.withProperty(OUT, EnumFacing.NORTH)
				.withProperty(getInputProperty(), 0)
				.withProperty(SIGNAL, Signal.REDSTONE));
	}

	@Override
	public Signal getSignal(IBlockState state) {
		return (Signal)state.getValue(SIGNAL);
	}

	@Override
	public PropertyInteger getInputProperty() {
		return INPUT;
	}

	@Override
	public EnumFacing getInput(IBlockState state, int index) {
		EnumFacing out = getOutput(state);
		return type == 0 ? out.getOpposite() : type == 1 ? out.rotateYCCW() : out.rotateY();
	}

	@Override
	public List<EnumFacing> getInputs(IBlockState state) {
		return Collections.singletonList(getInput(state, 0));
	}

	@Override
	public int getInputState(IBlockState state) {
		return state.getValue(INPUT);
	}

	@Override
	public int getInputCount() {
		return 1;
	}

	/* BLOCK OVERRIDE */

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, OUT, getInputProperty(), SIGNAL);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(OUT, EnumFacing.getHorizontal(meta))
				.withProperty(getInputProperty(), meta&4>>2)
				.withProperty(SIGNAL, Signal.values()[meta>>3]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0|state.getValue(OUT).getHorizontalIndex();
		i |= state.getValue(getInputProperty())<<2;
		i |= getSignal(state).ordinal()<<3;
		return i;
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 8));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state) < 8 ? 0 : 8;
	}
}
