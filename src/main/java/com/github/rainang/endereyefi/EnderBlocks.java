package com.github.rainang.endereyefi;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EnderBlocks {

	protected static final BlockRepeater[][][] REPEATERS = new BlockRepeater[2][4][3];
	protected static final BlockCaster[][][]   CASTERS   = new BlockCaster[4][2][2];

	protected static final Block enderCasterEye = new BlockCasterEye();

	protected static void init(boolean client) {
		for(int p = 0; p < 2; p++)
			for(int t = 0; t < 4; t++)
				for(int o = 0; o < 3; o++) {
					REPEATERS[p][t][o] = new BlockRepeater(p == 1, t, o);
					registerBlock(REPEATERS[p][t][o], client);
				}
		for(int type = 1; type < 4; type++)
			for(int neg = 0; neg < 2; neg++)
				for(int on = 0; on < 2; on++) {
					CASTERS[type][neg][on] = neg == 0 ? new BlockCaster(type, on == 1)
													  : new BlockCaster.BlockCasterNeg(type, on == 1);
					registerBlock(CASTERS[type][neg][on], client);
				}
		registerBlock(enderCasterEye, client);
	}

	private static void registerBlock(Block block, boolean client) {
		String name = block.getUnlocalizedName().substring(5);
		GameRegistry.registerBlock(block, name);
		if(client) {
			Item item = Item.getItemFromBlock(block);
			ModelResourceLocation mrl = new ModelResourceLocation(
					EnderEyeFi.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory");
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, mrl);
		}
	}

	public static BlockCaster getEnderDiode(int type, boolean isNegative, boolean isPowered) {
		return CASTERS[type][isNegative ? 1 : 0][isPowered ? 1 : 0];
	}
}
