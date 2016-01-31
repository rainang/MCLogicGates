package com.github.rainang.logicgates.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemDiode1In extends ItemBlock {

	public ItemDiode1In(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();
		return "tile." + (i < 8 ? "" : "ender_") + super.getUnlocalizedName().substring(5);
	}
}