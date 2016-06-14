package headfishindustries.civilisedideas.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSlindWeed extends BlockBush {
	
	private IIcon icon;

	public BlockSlindWeed() {
		super(Material.plants);
		setBlockName("ci_flowerSlindWeed");
		setHardness(0.2F);
		setStepSound(soundTypeGrass);
		setTickRandomly(false);
		setBlockBounds(0.05F, 0.0F, 0.1F, 1.0F, 0.3F, 1.0F);
		
	}
		
	   @SideOnly(Side.CLIENT)
	   public IIcon getIcon(int par1, int par2){
		   return this.icon;
	   }
	   
	   @SideOnly(Side.CLIENT)
	   public void registerBlockIcons(IIconRegister par1){
		   this.icon = par1.registerIcon("ci:slindWeed");
		   
	   }
	   
	   public boolean isOpaqueCube() {
	       return false;
	   }

}
