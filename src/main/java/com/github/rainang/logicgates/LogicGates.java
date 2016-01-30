package com.github.rainang.logicgates;

import com.github.rainang.logicgates.block.BlockDiode;
import com.github.rainang.logicgates.diode.DiodeFactory;
import com.github.rainang.logicgates.diode.Gate;
import com.github.rainang.logicgates.item.ItemDiode1In;
import com.github.rainang.logicgates.item.ItemGate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

	public static final CreativeTabs TAB_GATES = new CreativeTabs("logicgates") {
		@Override
		public Item getTabIconItem() {
			return item_gate;
		}
	};

	public static final BlockDiode[] repeaters  = DiodeFactory.create1InputDiode("repeater", Gate.BUFFER);
	public static final BlockDiode[] inverters  = DiodeFactory.create1InputDiode("inverter", Gate.NOT);
	public static final BlockDiode[] converters = DiodeFactory.createConverterDiode();

	public static final BlockDiode[] gates_and  = DiodeFactory.create2InputDiode("and", Gate.AND);
	public static final BlockDiode[] gates_or   = DiodeFactory.create2InputDiode("or", Gate.OR);
	public static final BlockDiode[] gates_xor  = DiodeFactory.create2InputDiode("xor", Gate.XOR);
	public static final BlockDiode[] gates_nand = DiodeFactory.create2InputDiode("nand", Gate.NAND);
	public static final BlockDiode[] gates_nor  = DiodeFactory.create2InputDiode("nor", Gate.NOR);
	public static final BlockDiode[] gates_xnor = DiodeFactory.create2InputDiode("xnor", Gate.XNOR);

	public static final BlockDiode[] gates3_and  = DiodeFactory.create3InputDiode("and", Gate.AND);
	public static final BlockDiode[] gates3_or   = DiodeFactory.create3InputDiode("or", Gate.OR);
	public static final BlockDiode[] gates3_xor  = DiodeFactory.create3InputDiode("xor", Gate.XOR);
	public static final BlockDiode[] gates3_nand = DiodeFactory.create3InputDiode("nand", Gate.NAND);
	public static final BlockDiode[] gates3_nor  = DiodeFactory.create3InputDiode("nor", Gate.NOR);
	public static final BlockDiode[] gates3_xnor = DiodeFactory.create3InputDiode("xnor", Gate.XNOR);

	public static final BlockDiode[] vertical_transmitters = DiodeFactory.createVerticalTransmitters();
	public static final BlockDiode[] vertical_receivers    = DiodeFactory.createVerticalReceivers();

	public static final ItemGate item_gate = new ItemGate();

	@EventHandler
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

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerRecipes();

		if(event.getSide() == Side.SERVER)
			return;

		registerItemRender(item_gate);

		for(BlockDiode diode : repeaters)
			registerItemRender(diode);
		for(BlockDiode diode : inverters)
			registerItemRender(diode);
		for(BlockDiode diode : converters)
			registerItemRender(diode);

		for(BlockDiode diode : gates_and)
			registerItemRender(diode);
		for(BlockDiode diode : gates_or)
			registerItemRender(diode);
		for(BlockDiode diode : gates_xor)
			registerItemRender(diode);
		for(BlockDiode diode : gates_nand)
			registerItemRender(diode);
		for(BlockDiode diode : gates_nor)
			registerItemRender(diode);
		for(BlockDiode diode : gates_xnor)
			registerItemRender(diode);

		for(BlockDiode diode : gates3_and)
			registerItemRender(diode);
		for(BlockDiode diode : gates3_or)
			registerItemRender(diode);
		for(BlockDiode diode : gates3_xor)
			registerItemRender(diode);
		for(BlockDiode diode : gates3_nand)
			registerItemRender(diode);
		for(BlockDiode diode : gates3_nor)
			registerItemRender(diode);
		for(BlockDiode diode : gates3_xnor)
			registerItemRender(diode);

		for(BlockDiode diode : vertical_transmitters)
			registerItemRender(diode);
		for(BlockDiode diode : vertical_receivers)
			registerItemRender(diode);
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

	@SideOnly(Side.CLIENT)
	private static void registerItemRender(Block block) {
		registerItemRender(Item.getItemFromBlock(block));
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemRender(Item item) {
		if(item.getHasSubtypes()) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			item.getSubItems(item, null, list);
			for(ItemStack stack : list)
				registerItemRender(stack);

			List<ItemStack> variants = new ArrayList<ItemStack>();
			item.getSubItems(item, null, variants);
			ResourceLocation[] resourceLocations = new ResourceLocation[variants.size()];
			for(int i = 0; i < resourceLocations.length; i++)
				resourceLocations[i] = getResourceLocation(variants.get(i));
			ModelBakery.registerItemVariants(item_gate, resourceLocations);
		} else {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, getResourceLocation(item));
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemRender(ItemStack stack) {
		Minecraft.getMinecraft()
				.getRenderItem()
				.getItemModelMesher()
				.register(stack.getItem(), stack.getItemDamage(), getResourceLocation(stack));
	}

	public static ModelResourceLocation getResourceLocation(Item item) {
		return new ModelResourceLocation(LogicGates.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory");
	}

	public static ModelResourceLocation getResourceLocation(ItemStack stack) {
		return new ModelResourceLocation(
				LogicGates.MODID + ":" + stack.getItem().getUnlocalizedName(stack).substring(5), "inventory");
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
