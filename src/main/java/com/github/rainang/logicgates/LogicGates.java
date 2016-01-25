package com.github.rainang.logicgates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = LogicGates.MODID, version = LogicGates.VERSION, acceptedMinecraftVersions = LogicGates.MCVERSION)
public class LogicGates {

	public static final String MODID     = "logicgates";
	public static final String MCVERSION = "@mcversion@";
	public static final String VERSION   = "@version@";

	protected static final CreativeTabs TAB_REDSTONE = new CreativeTabs("redstonelogic") {
		@Override
		public Item getTabIconItem() {
			return repeaters[0][0][0].getItem(null, null);
		}
	};

	protected static final CreativeTabs TAB_ENDER = new CreativeTabs("enderlogic") {
		@Override
		public Item getTabIconItem() {
			return repeaters[0][1][1].getItem(null, null);
		}
	};

	public static final BlockRepeater[][][] repeaters
			= new BlockRepeater[2][EnumSignal.values().length][EnumSignal.values().length];

	public static final BlockInverter[][] inverters = new BlockInverter[2][EnumSignal.values().length];

	public static final BlockDirector[][] directors = new BlockDirector[2][EnumSignal.values().length];

	public static final BlockVerticalRepeater[][] verticals = new BlockVerticalRepeater[2][EnumSignal.values().length];

	public static final BlockGate[][] gates = new BlockGate[EnumSignal.values().length][EnumGate.values().length];

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for(int power = 0; power < repeaters.length; power++)
			for(EnumSignal in : EnumSignal.values()) {
				inverters[power][in.ordinal()] = new BlockInverter(in, power == 1);
				verticals[power][in.ordinal()] = new BlockVerticalRepeater(in, power == 1);
				directors[power][in.ordinal()] = new BlockDirector(in, power == 1);
				registerBlock(inverters[power][in.ordinal()]);
				registerBlock(verticals[power][in.ordinal()]);
				registerBlock(directors[power][in.ordinal()]);
				for(EnumSignal out : EnumSignal.values()) {
					repeaters[power][in.ordinal()][out.ordinal()] = new BlockRepeater(in, out, power == 1);
					registerBlock(repeaters[power][in.ordinal()][out.ordinal()]);
				}
			}

		for(EnumSignal sig : EnumSignal.values())
			for(EnumGate gate : EnumGate.values()) {
				gates[sig.ordinal()][gate.ordinal()] = new BlockGate(sig, gate);
				registerBlock(gates[sig.ordinal()][gate.ordinal()]);
			}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if(event.getSide() == Side.CLIENT) {
			for(int power = 0; power < repeaters.length; power++)
				for(EnumSignal in : EnumSignal.values()) {
					registerBlockItem(inverters[power][in.ordinal()]);
					registerBlockItem(verticals[power][in.ordinal()]);
					registerBlockItem(directors[power][in.ordinal()]);
					for(EnumSignal out : EnumSignal.values())
						registerBlockItem(repeaters[power][in.ordinal()][out.ordinal()]);
				}

			for(EnumSignal sig : EnumSignal.values())
				for(EnumGate gate : EnumGate.values())
					registerBlockItem(gates[sig.ordinal()][gate.ordinal()]);
		}
		registerRecipes();
	}

	private static void registerBlock(Block block) {
		String name = block.getUnlocalizedName().substring(5);
		GameRegistry.registerBlock(block, name);
	}

	@SideOnly(Side.CLIENT)
	private static void registerBlockItem(Block block) {
		Item item = Item.getItemFromBlock(block);
		ModelResourceLocation mrl = new ModelResourceLocation(
				LogicGates.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, mrl);
	}

	private static void registerRecipes() {
		ItemStack stone = new ItemStack(Blocks.stone, 1, BlockStone.EnumType.STONE.getMetadata());
		ItemStack slab = new ItemStack(Blocks.stone_slab, 1, BlockStone.EnumType.STONE.getMetadata());

		Block torch = Blocks.redstone_torch;

		Item wire = Items.redstone;
		Item pearl = Items.ender_pearl;

		ItemStack r_repeater = new ItemStack(Item.getItemFromBlock(repeaters[0][0][0]));
		ItemStack e_repeater = new ItemStack(Item.getItemFromBlock(repeaters[0][1][1]));
		ItemStack r_gate = new ItemStack(Item.getItemFromBlock(gates[0][0]));
		ItemStack e_gate = new ItemStack(Item.getItemFromBlock(gates[1][0]));
		ItemStack r_inverter = new ItemStack(Item.getItemFromBlock(inverters[0][0]));
		ItemStack e_inverter = new ItemStack(Item.getItemFromBlock(inverters[0][1]));
		ItemStack r_vertical = new ItemStack(Item.getItemFromBlock(verticals[0][0]));
		ItemStack e_vertical = new ItemStack(Item.getItemFromBlock(verticals[0][1]));
		ItemStack r_redirect = new ItemStack(Item.getItemFromBlock(directors[0][0]));
		ItemStack e_redirect = new ItemStack(Item.getItemFromBlock(directors[0][1]));
		ItemStack r_converter = new ItemStack(Item.getItemFromBlock(repeaters[0][0][1]));
		ItemStack e_converter = new ItemStack(Item.getItemFromBlock(repeaters[0][1][0]));

		GameRegistry.addShapedRecipe(r_repeater, "STS", "SWS", "STS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapelessRecipe(e_repeater, r_repeater, pearl);

		GameRegistry.addShapedRecipe(r_gate, "STS", "TWT", "SSS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapelessRecipe(e_gate, r_gate, pearl);

		GameRegistry.addShapedRecipe(r_inverter, "STS", "SWS", "STS", 'S', slab, 'W', stone, 'T', torch);
		GameRegistry.addShapelessRecipe(e_inverter, r_inverter, pearl);

		GameRegistry.addShapedRecipe(r_vertical, "SWS", "WTW", "SWS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapelessRecipe(e_vertical, r_vertical, pearl);

		GameRegistry.addShapedRecipe(r_redirect, "SSS", "TWS", "STS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapelessRecipe(e_redirect, r_redirect, pearl);

		GameRegistry.addShapedRecipe(r_converter, "SPS", "SWS", "STS", 'S', slab, 'W', wire, 'T', torch, 'P', pearl);
		GameRegistry.addShapedRecipe(e_converter, "STS", "SWS", "SPS", 'S', slab, 'W', wire, 'T', torch, 'P', pearl);
		GameRegistry.addShapelessRecipe(e_converter, r_converter);
		GameRegistry.addShapelessRecipe(r_converter, e_converter);
	}
}
