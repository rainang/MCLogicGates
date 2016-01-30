package com.github.rainang.logicgates.client;

import com.github.rainang.logicgates.block.BlockDiode;
import com.github.rainang.logicgates.common.CommonProxy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static com.github.rainang.logicgates.LogicGates.*;

public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);

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

	private static void registerItemRender(Block block) {
		registerItemRender(Item.getItemFromBlock(block));
	}

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

	private static void registerItemRender(ItemStack stack) {
		Minecraft.getMinecraft()
				.getRenderItem()
				.getItemModelMesher()
				.register(stack.getItem(), stack.getItemDamage(), getResourceLocation(stack));
	}

	public static ModelResourceLocation getResourceLocation(Item item) {
		return new ModelResourceLocation(MODID + ":" + item.getUnlocalizedName().substring(5), "inventory");
	}

	public static ModelResourceLocation getResourceLocation(ItemStack stack) {
		return new ModelResourceLocation(MODID + ":" + stack.getItem().getUnlocalizedName(stack).substring(5),
				"inventory");
	}
}
