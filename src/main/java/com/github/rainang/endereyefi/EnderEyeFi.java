package com.github.rainang.endereyefi;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = EnderEyeFi.MODID, version = EnderEyeFi.VERSION)
public class EnderEyeFi {

	public static final String MODID   = "endereyefi";
	public static final String VERSION = "0.3.2";

	protected static final BlockRepeater[][][] REPEATERS = new BlockRepeater[2][4][3];

	@EventHandler
	public void init(FMLInitializationEvent event) {
		boolean b = event.getSide() == Side.CLIENT;

		MinecraftForge.EVENT_BUS.register(this);
		for(int p = 0; p < 2; p++)
			for(int t = 0; t < 4; t++)
				for(int o = 0; o < 3; o++) {
					REPEATERS[p][t][o] = new BlockRepeater(p == 1, t, o);
					registerBlock(REPEATERS[p][t][o], b);
				}

		GameRegistry.addShapedRecipe(new ItemStack(getRepeater(0, 3, 1), 1), "prp", "sss", 'p', Items.ender_pearl, 'r',
									 Items.redstone, 's', Blocks.stone);
		GameRegistry
				.addShapedRecipe(new ItemStack(getRepeater(0, 1, 1), 1), "trp", "sss", 't', Blocks.redstone_torch, 'r',
								 Items.redstone, 'p', Items.ender_pearl, 's', Blocks.stone);

		GameRegistry.addShapelessRecipe(new ItemStack(getRepeater(0, 1, 1), 1), getRepeater(0, 2, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(getRepeater(0, 2, 1), 1), getRepeater(0, 1, 1));
	}

	private void registerBlock(Block block, boolean client) {
		String name = block.getUnlocalizedName().substring(5);
		GameRegistry.registerBlock(block, name);
		if(client) {
			ModelResourceLocation mrl = new ModelResourceLocation(MODID + ":" + name, "inventory");
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
					 .register(Item.getItemFromBlock(block), 0, mrl);
		}
	}

	@SubscribeEvent
	public void subscribe(PlayerInteractEvent event) {
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			IBlockState state = event.world.getBlockState(event.pos);
			if(Blocks.unpowered_repeater == state.getBlock())
				event.world.setBlockState(event.pos, getRepeater(0, 0, 1).getDefaultState()
																		 .withProperty(BlockDirectional.FACING,
																					   state.getValue(
																							   BlockDirectional
																									   .FACING)));
			else if(Blocks.powered_repeater == state.getBlock())
				event.world.setBlockState(event.pos, getRepeater(1, 0, 1).getDefaultState()
																		 .withProperty(BlockDirectional.FACING,
																					   state.getValue(
																							   BlockDirectional
																									   .FACING)));
		}
	}

	public static BlockRepeater getRepeater(int on, int type, int out) {
		return REPEATERS[on][type][out];
	}
}
