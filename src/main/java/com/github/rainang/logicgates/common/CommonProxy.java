package com.github.rainang.logicgates.common;

import com.github.rainang.logicgates.block.BlockDiode;
import com.github.rainang.logicgates.item.ItemDiode1In;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.github.rainang.logicgates.LogicGates.*;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		registerItem(item_gate);

		for(BlockDiode diode : inverters)
			GameRegistry.registerBlock(diode, ItemDiode1In.class, diode.getUnlocalizedName().substring(5));
		for(BlockDiode diode : repeaters)
			GameRegistry.registerBlock(diode, ItemDiode1In.class, diode.getUnlocalizedName().substring(5));
		for(BlockDiode diode : converters)
			GameRegistry.registerBlock(diode, ItemDiode1In.class, diode.getUnlocalizedName().substring(5));

		for(BlockDiode diode : gates_and)
			registerBlock(diode);
		for(BlockDiode diode : gates_or)
			registerBlock(diode);
		for(BlockDiode diode : gates_xor)
			registerBlock(diode);
		for(BlockDiode diode : gates_nand)
			registerBlock(diode);
		for(BlockDiode diode : gates_nor)
			registerBlock(diode);
		for(BlockDiode diode : gates_xnor)
			registerBlock(diode);

		for(BlockDiode diode : gates3_and)
			registerBlock(diode);
		for(BlockDiode diode : gates3_or)
			registerBlock(diode);
		for(BlockDiode diode : gates3_xor)
			registerBlock(diode);
		for(BlockDiode diode : gates3_nand)
			registerBlock(diode);
		for(BlockDiode diode : gates3_nor)
			registerBlock(diode);
		for(BlockDiode diode : gates3_xnor)
			registerBlock(diode);

		for(BlockDiode diode : vertical_transmitters)
			registerBlock(diode);
		for(BlockDiode diode : vertical_receivers)
			registerBlock(diode);
	}

	public void init(FMLInitializationEvent event) {
		registerRecipes();
	}

	private static void registerBlock(Block block) {
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
	}

	private static void registerItem(Item item) {
		if(item.getHasSubtypes())
			GameRegistry.registerItem(item, item.getUnlocalizedName(new ItemStack(item, 1, 0)).substring(5));
		else
			GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
	}

	private static void registerRecipes() {
		Item wire = Items.redstone;
		Item p = Items.ender_pearl;

		ItemStack slab = new ItemStack(Blocks.stone_slab, 1, BlockStone.EnumType.STONE.getMetadata());

		ItemStack torch = new ItemStack(Blocks.redstone_torch);
		ItemStack invert = new ItemStack(inverters[0]);
		ItemStack transmitter = new ItemStack(vertical_transmitters[0]);
		ItemStack receiver = new ItemStack(vertical_receivers[0]);
		ItemStack repeat = new ItemStack(repeaters[0]);

		ItemStack buffer = new ItemStack(item_gate, 1, 0);
		ItemStack not = new ItemStack(item_gate, 1, 1);
		ItemStack and = new ItemStack(item_gate, 1, 2);
		ItemStack or = new ItemStack(item_gate, 1, 3);
		ItemStack xor = new ItemStack(item_gate, 1, 4);
		ItemStack nand = new ItemStack(item_gate, 1, 5);
		ItemStack nor = new ItemStack(item_gate, 1, 6);
		ItemStack xnor = new ItemStack(item_gate, 1, 7);

		/* GATES */

		GameRegistry.addShapedRecipe(buffer, "W", "W", "W", 'W', wire);
		GameRegistry.addShapedRecipe(not, "T", "W", "W", 'W', wire, 'T', torch);
		GameRegistry.addShapedRecipe(and, " N ", "NWN", 'N', not, 'W', wire);
		GameRegistry.addShapedRecipe(nand, " B ", "NWN", 'N', not, 'W', wire, 'B', buffer);
		GameRegistry.addShapedRecipe(or, " B ", "BWB", 'W', wire, 'B', buffer);
		GameRegistry.addShapedRecipe(nor, " N ", "BWB", 'W', wire, 'N', not, 'B', buffer);
		GameRegistry.addShapedRecipe(xor, "OBO", "NWN", "WAW", 'N', not, 'W', wire, 'O', nor, 'A', and, 'B', buffer);
		GameRegistry.addShapedRecipe(xnor, "ONO", "NWN", "WAW", 'N', not, 'W', wire, 'O', nor, 'A', and);

		GameRegistry.addShapelessRecipe(not, buffer, not);
		GameRegistry.addShapelessRecipe(buffer, not, not);
		GameRegistry.addShapelessRecipe(nand, and, not);
		GameRegistry.addShapelessRecipe(nor, or, not);
		GameRegistry.addShapelessRecipe(xnor, xor, not);
		GameRegistry.addShapelessRecipe(and, nand, not);
		GameRegistry.addShapelessRecipe(or, nor, not);
		GameRegistry.addShapelessRecipe(xor, xnor, not);

		/* DIODES */

		GameRegistry.addShapedRecipe(repeat, "SGS", "SWS", "SWS", 'G', buffer, 'S', slab, 'W', wire);
		GameRegistry.addShapedRecipe(invert, "SGS", "SWS", "SWS", 'G', not, 'S', slab, 'W', wire);
		GameRegistry.addShapedRecipe(transmitter, "SWS", "WGW", "SWS", 'G', buffer, 'S', slab, 'W', wire);
		GameRegistry.addShapedRecipe(receiver, "SGS", "SWS", "SSS", 'G', buffer, 'S', slab, 'W', wire);

		GameRegistry.addShapedRecipe(new ItemStack(gates_and[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', and);
		GameRegistry.addShapedRecipe(new ItemStack(gates_or[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer,
				'G', or);
		GameRegistry.addShapedRecipe(new ItemStack(gates_xor[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', xor);
		GameRegistry.addShapedRecipe(new ItemStack(gates_nand[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', nand);
		GameRegistry.addShapedRecipe(new ItemStack(gates_nor[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', nor);
		GameRegistry.addShapedRecipe(new ItemStack(gates_xnor[0]), "SBS", "WGW", "SSS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', xnor);

		GameRegistry.addShapedRecipe(new ItemStack(gates3_and[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', and);
		GameRegistry.addShapedRecipe(new ItemStack(gates3_or[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', or);
		GameRegistry.addShapedRecipe(new ItemStack(gates3_xor[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', xor);
		GameRegistry.addShapedRecipe(new ItemStack(gates3_nand[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', nand);
		GameRegistry.addShapedRecipe(new ItemStack(gates3_nor[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', nor);
		GameRegistry.addShapedRecipe(new ItemStack(gates3_xnor[0]), "SBS", "WGW", "SWS", 'S', slab, 'W', wire, 'B',
				buffer, 'G', xnor);

		/* ENDER */

		GameRegistry.addShapelessRecipe(new ItemStack(repeaters[0], 1, 8), repeat, p);
		GameRegistry.addShapelessRecipe(new ItemStack(inverters[0], 1, 8), invert, p);
		GameRegistry.addShapelessRecipe(new ItemStack(vertical_transmitters[0], 1, 8), transmitter, p);
		GameRegistry.addShapelessRecipe(new ItemStack(vertical_receivers[0], 1, 8), receiver, p);

		GameRegistry.addShapelessRecipe(new ItemStack(gates_and[4]), gates_and[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates_or[4]), gates_or[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates_xor[4]), gates_xor[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates_nand[4]), gates_nand[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates_nor[4]), gates_nor[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates_xnor[4]), gates_xnor[0], p);

		GameRegistry.addShapelessRecipe(new ItemStack(gates3_and[2]), gates3_and[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates3_or[2]), gates3_or[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates3_xor[2]), gates3_xor[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates3_nand[2]), gates3_nand[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates3_nor[2]), gates3_nor[0], p);
		GameRegistry.addShapelessRecipe(new ItemStack(gates3_xnor[2]), gates3_xnor[0], p);

		/* CONVERTERS */

		GameRegistry.addShapedRecipe(new ItemStack(converters[0], 1, 0), "SPS", "SWS", "SBS", 'B', buffer, 'S', slab,
				'W', wire, 'P', p);
		GameRegistry.addShapedRecipe(new ItemStack(converters[0], 1, 8), "SBS", "SWS", "SPS", 'B', buffer, 'S', slab,
				'W', wire, 'P', p);
		GameRegistry.addShapelessRecipe(new ItemStack(converters[0], 1, 0), new ItemStack(converters[0], 1, 8));
		GameRegistry.addShapelessRecipe(new ItemStack(converters[0], 1, 8), new ItemStack(converters[0], 1, 0));
	}
}
