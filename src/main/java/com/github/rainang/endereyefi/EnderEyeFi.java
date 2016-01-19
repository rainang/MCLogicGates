package com.github.rainang.endereyefi;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = EnderEyeFi.MODID, version = EnderEyeFi.VERSION, acceptedMinecraftVersions = EnderEyeFi.MCVERSION)
public class EnderEyeFi {

	public static final String MODID     = "endereyefi";
	public static final String MCVERSION = "@mcversion@";
	public static final String VERSION   = "@version@";

	protected static final CreativeTabs TAB_EYE = new CreativeTabs("ender") {
		@Override
		public Item getTabIconItem() {
			return Items.ender_eye;
		}
	};

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		EnderBlocks.init(event.getSide() == Side.CLIENT);

		Items.repeater.setCreativeTab(null);
		for(Object o : CraftingManager.getInstance().getRecipeList())
			if(ItemStack.areItemsEqual(new ItemStack(Items.repeater, 1), ((IRecipe)o).getRecipeOutput())) {
				CraftingManager.getInstance().getRecipeList().remove(o);
				break;
			}

		ItemStack stackStone = new ItemStack(Blocks.stone, 1, BlockStone.EnumType.STONE.getMetadata());
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.REPEATERS[0][0][1], 1), "#X#", "III", '#',
				Blocks.redstone_torch, 'X', Items.redstone, 'I', stackStone);
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.REPEATERS[0][3][1], 1), "#X#", "III", '#',
				Items.ender_pearl, 'X', Items.redstone, 'I', stackStone);
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.REPEATERS[0][2][1], 1), "RX#", "III", 'R',
				Blocks.redstone_torch, 'X', Items.redstone, '#', Items.ender_pearl, 'I', stackStone);
		GameRegistry.addShapelessRecipe(new ItemStack(EnderBlocks.REPEATERS[0][1][1], 1),
				EnderBlocks.REPEATERS[0][2][1]);
		GameRegistry.addShapelessRecipe(new ItemStack(EnderBlocks.REPEATERS[0][2][1], 1),
				EnderBlocks.REPEATERS[0][1][1]);

		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.CASTERS[0][0][0], 6), "#X#", "#E#", "#X#", 'E',
				Items.ender_eye, '#', Blocks.obsidian, 'X', Blocks.redstone_block);
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.CASTERS[1][0][0], 6), "#X#", "#E#", "#Y#", 'E',
				Items.ender_eye, '#', Blocks.obsidian, 'Y', Items.ender_pearl, 'X', Blocks.redstone_block);
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.CASTERS[2][0][0], 6), "#X#", "#E#", "#Y#", 'E',
				Items.ender_eye, '#', Blocks.obsidian, 'X', Items.ender_pearl, 'Y', Blocks.redstone_block);
		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.CASTERS[3][0][0], 6), "#X#", "#E#", "#X#", 'E',
				Items.ender_eye, '#', Blocks.obsidian, 'X', Items.ender_pearl);

		GameRegistry.addShapelessRecipe(new ItemStack(EnderBlocks.CASTERS[1][0][0], 1), EnderBlocks.CASTERS[2][0][0]);
		GameRegistry.addShapelessRecipe(new ItemStack(EnderBlocks.CASTERS[2][0][0], 1), EnderBlocks.CASTERS[1][0][0]);

		GameRegistry.addShapedRecipe(new ItemStack(EnderBlocks.enderCasterEye, 1), "#X#", "XEX", "#X#", 'E',
				Items.ender_eye, '#', Blocks.obsidian, 'X', Blocks.glass);
	}

	@SubscribeEvent
	public void subscribe(PlayerInteractEvent event) {
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			IBlockState state = event.world.getBlockState(event.pos);
			if(Blocks.unpowered_repeater == state.getBlock())
				event.world.setBlockState(event.pos, EnderBlocks.REPEATERS[0][0][1].getDefaultState()
						.withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING)));
			else if(Blocks.powered_repeater == state.getBlock())
				event.world.setBlockState(event.pos, EnderBlocks.REPEATERS[1][0][1].getDefaultState()
						.withProperty(BlockDirectional.FACING, state.getValue(BlockDirectional.FACING)));
		}
	}
}
