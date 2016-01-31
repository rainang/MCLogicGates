package com.github.rainang.logicgates.item;

import com.github.rainang.logicgates.Gate;
import com.github.rainang.logicgates.LogicGates;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGate extends Item {

	public ItemGate() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setUnlocalizedName("gate");
		setCreativeTab(LogicGates.TAB_GATES);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();
		return super.getUnlocalizedName() + "_" + Gate.values()[i].getName();
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		for(int i = 0; i < Gate.values().length; ++i)
			subItems.add(new ItemStack(itemIn, 1, i));
	}
}