package nf.fr.ephys.playerproxies.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import nf.fr.ephys.playerproxies.common.PlayerProxies;

public class EnchantmentNoVoidFog extends Enchantment {
	public static int ENCHANTMENT_ID = 150;

	public static void register() {
		//PlayerProxies.Enchantments.noVoidFog = new EnchantmentNoVoidFog(ENCHANTMENT_ID);
	}
	
	protected EnchantmentNoVoidFog(int id) {
		super(id, 1, EnumEnchantmentType.armor_head);

		this.setName("pp.novoidfog");
	}

	public int getMinEnchantability(int level) {
		return 1;
	}

	public int getMaxEnchantability(int level) {
		return this.getMinEnchantability(level) + 40;
	}

	public int getMaxLevel() {
		return 1;
	}
}
