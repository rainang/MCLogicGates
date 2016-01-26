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
		Item wire = Items.redstone;
		Item pearl = Items.ender_pearl;

		ItemStack slab = new ItemStack(Blocks.stone_slab, 1, BlockStone.EnumType.STONE.getMetadata());

		ItemStack torch = new ItemStack(Blocks.redstone_torch);
		ItemStack invert = new ItemStack(inverters[0][0]);
		ItemStack vertical = new ItemStack(verticals[0][0]);
		ItemStack direct = new ItemStack(directors[0][0]);
		ItemStack repeat = new ItemStack(repeaters[0][0][0]);
		ItemStack convert = new ItemStack(repeaters[0][0][1]);
		ItemStack convert2 = new ItemStack(repeaters[0][1][0]);

		ItemStack and = new ItemStack(gates[0][0]);
		ItemStack nand = new ItemStack(gates[0][1]);
		ItemStack or = new ItemStack(gates[0][2]);
		ItemStack nor = new ItemStack(gates[0][3]);
		ItemStack xor = new ItemStack(gates[0][4]);
		ItemStack xnor = new ItemStack(gates[0][5]);

		/* REPEATERS */
		GameRegistry.addShapedRecipe(repeat, "TWT", "SSS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapedRecipe(invert, "WWT", "SSS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapedRecipe(vertical, "WTW", "SSS", 'S', slab, 'W', wire, 'T', torch);
		GameRegistry.addShapedRecipe(direct, "WT ", "SSS", 'S', slab, 'W', wire, 'T', torch);

		GameRegistry.addShapelessRecipe(new ItemStack(repeaters[0][1][1]), repeat, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(inverters[0][1]), invert, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(verticals[0][1]), vertical, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(directors[0][1]), direct, pearl);

		/* GATES */
		GameRegistry.addShapedRecipe(and, " I ", "IWI", 'I', invert, 'W', wire);
		GameRegistry.addShapedRecipe(nand, " R ", "IWI", 'I', invert, 'W', wire, 'R', repeat);
		GameRegistry.addShapedRecipe(or, " R ", "RWR", 'W', wire, 'R', repeat);
		GameRegistry.addShapedRecipe(nor, " I ", "RWR", 'I', invert, 'W', wire, 'R', repeat);
		GameRegistry.addShapedRecipe(xor, "NWN", "IWI", "WAW", 'I', invert, 'W', wire, 'N', nor, 'A', and);
		GameRegistry.addShapedRecipe(xnor, "I", "X", 'I', invert, 'X', xor);

		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][0]), and, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][1]), nand, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][2]), or, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][3]), nor, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][4]), xor, pearl);
		GameRegistry.addShapelessRecipe(new ItemStack(gates[1][5]), xnor, pearl);

		/* CONVERTERS */
		GameRegistry.addShapedRecipe(convert, "TWP", "SSS", 'S', slab, 'W', wire, 'T', torch, 'P', pearl);
		GameRegistry.addShapelessRecipe(convert, convert2);
		GameRegistry.addShapelessRecipe(convert2, convert);
	}
}
