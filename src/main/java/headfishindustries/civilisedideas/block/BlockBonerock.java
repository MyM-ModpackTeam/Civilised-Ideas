package headfishindustries.civilisedideas.block;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import headfishindustries.civilisedideas.CivilisedIdeas;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBonerock extends Block{

	public BlockBonerock() {
		super(Material.rock);
		setBlockName("ci_rockBone");
		setBlockTextureName("ci:rockBone");
	}
}