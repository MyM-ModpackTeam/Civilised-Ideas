package headfishindustries.civilisedideas;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import headfishindustries.civilisedideas.block.BlockBonerock;
import headfishindustries.civilisedideas.block.BlockRedrock;
import headfishindustries.civilisedideas.block.BlockSlindWeed;
import headfishindustries.civilisedideas.block.BlockWilliWeed;
import headfishindustries.civilisedideas.commands.GetDim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid="ci",modLanguage = "java", name="Civilised Ideas", version="1.0")

public class CivilisedIdeas {
	
	@Instance(value = "ci")
	public static CivilisedIdeas instance;
	
	public static Block blockRedrock = new BlockRedrock();
	public static Block blockBonerock = new BlockBonerock();
	public static Block blockWilliWeed = new BlockWilliWeed();
	public static Block blockSlindWeed = new BlockSlindWeed();
	
	//public static Item itemGrapple;

	//public static Entity entityGrapple;
	
	

	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		ICommandManager cmdManage = event.getServer().getCommandManager();
		ServerCommandManager svrCommandManage = ((ServerCommandManager)cmdManage);
		svrCommandManage.registerCommand(new GetDim());
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		//Block and Item Initialization
		//itemGrapple = new ItemGrapple();
		
		GameRegistry.registerBlock(blockBonerock, "Bonerock");
		GameRegistry.registerBlock(blockRedrock, "Redrock");
		GameRegistry.registerBlock(blockSlindWeed, "SlindWeed");
		GameRegistry.registerBlock(blockWilliWeed, "WilliWeed");

	}
	

	@EventHandler
	public void init(FMLInitializationEvent event){
		//Proxy, TileEntity, entity, GUI and Packet registration
	
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		
		
		
	}
	
}
