package nf.fr.ephys.playerproxies.client.gui;

import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class GuiModConfigFactory implements IModGuiFactory {
	// seriously, why can't I just put GuiModConfigFactory.class.toString() in @Mod ? >_>
	public static final String CLASSNAME = "nf.fr.ephys.playerproxies.client.gui.GuiModConfigFactory";

	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiModConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}