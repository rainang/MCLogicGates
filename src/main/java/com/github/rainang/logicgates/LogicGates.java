package com.github.rainang.logicgates;

import com.github.rainang.logicgates.block.BlockDiode;
import com.github.rainang.logicgates.common.CommonProxy;
import com.github.rainang.logicgates.item.ItemGate;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LogicGates.MODID, version = LogicGates.VERSION, acceptedMinecraftVersions = LogicGates.MCVERSION)
public class LogicGates {

	public static final String MODID     = "logicgates";
	public static final String MCVERSION = "@mcversion@";
	public static final String VERSION   = "@version@";

	@SidedProxy(clientSide = "com.github.rainang.logicgates.client.ClientProxy",
				serverSide = "com.github.rainang.logicgates.common.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabs TAB_GATES = new CreativeTabs(MODID) {
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
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
}
