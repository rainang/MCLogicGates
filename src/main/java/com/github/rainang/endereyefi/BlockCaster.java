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

	protected final int type;

	public BlockCaster(int type, boolean isPowered) {
		super(Material.ground, isPowered);
		this.type = type;
		boolean neg = isNegative();

		if(!neg && !isPowered)
			setCreativeTab(EnderEyeFi.TAB_EYE);
		setLightLevel(isPowered ? 0.25f : 0);
		EnumFacing facing = neg ? EnumFacing.DOWN : EnumFacing.UP;
		setDefaultState(getBlockState().getBaseState()
				.withProperty(getInProperty(), facing)
				.withProperty(OUT, facing.getOpposite()));
		setHardness(0.25F).setResistance(10.0F);

		String name = "";
		name += type == 0 ? "diode" : type == 1 ? "caster_ee" : type == 2 ? "caster_re" : "caster_er";
		name += neg ? "_neg" : "";
		name += isPowered ? "_on" : "";
		setUnlocalizedName(name);

		setBlockBounds(0.0F, 0.0F, 0.0F, 1, 1, 1);
	}

	public static class BlockCasterNeg extends BlockCaster {

		public BlockCasterNeg(int type, boolean isPowered) {
			super(type, isPowered);
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

	public BlockCaster getNegative() {
		return EnderBlocks.getEnderDiode(type, !(isNegative()), isPowered);
	}

	/* Block override */

	@Override
	public boolean onBlockActivated(
			World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(!playerIn.capabilities.allowEdit)
			return false;
		BlockDiode diode = (BlockDiode)state.getBlock();
		if(diode.getInputSide(state) == side || diode.getOutputSide(state) == side)
			return false;
		else if(playerIn.isSneaking()) {
			BlockCaster b = getInProperty().getAllowedValues().contains(side) ? this : getNegative();
			IBlockState bs = b.getDefaultState()
					.withProperty(b.getInProperty(), side)
					.withProperty(OUT, state.getValue(OUT));
			if(b.shouldBePowered(worldIn, pos, bs) != isPowered)
				bs = EnderBlocks.getEnderDiode(type, b.isNegative(), !isPowered)
						.getDefaultState()
						.withProperty(b.getInProperty(), side)
						.withProperty(OUT, state.getValue(OUT));
			worldIn.setBlockState(pos, bs);
		} else
			worldIn.setBlockState(pos, state.withProperty(OUT, side));
		notifyNeighbors(worldIn, pos, state);
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(
			World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		if(!placer.isSneaking())
			facing = placer.getHorizontalFacing();
		if(isOnThisAxisDirection(facing)) {
			BlockCaster block = getNegative();
			return block.getDefaultState()
					.withProperty(block.getInProperty(), facing.getOpposite())
					.withProperty(OUT, facing);
		}
		return getDefaultState().withProperty(getInProperty(), facing.getOpposite()).withProperty(OUT, facing);
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(EnderBlocks.getEnderDiode(type, false, false));
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return Item.getItemFromBlock(EnderBlocks.getEnderDiode(type, false, false));
	}

	/* BlockDiode impl */

	@Override
	public int getTickDelay(IBlockState state) {
		return 0;
	}

	@Override
	public boolean isEnderTransmitter() {
		return type != 3;
	}

	@Override
	public boolean isEnderReceiver() {
		return type%2 == 1;
	}

	@Override
	public IBlockState getPoweredState(IBlockState state) {
		return EnderBlocks.getEnderDiode(type, isNegative(), true)
				.getDefaultState()
				.withProperty(getInProperty(), state.getValue(getInProperty()))
				.withProperty(OUT, state.getValue(OUT));
	}

	@Override
	public IBlockState getUnpoweredState(IBlockState state) {
		return EnderBlocks.getEnderDiode(type, isNegative(), false)
				.getDefaultState()
				.withProperty(getInProperty(), state.getValue(getInProperty()))
				.withProperty(OUT, state.getValue(OUT));
	}

	@Override
	public EnumFacing getInputSide(IBlockState state) {
		return (EnumFacing)state.getValue(getInProperty());
	}

	@Override
	public EnumFacing getOutputSide(IBlockState state) {
		return (EnumFacing)state.getValue(OUT);
	}
}
