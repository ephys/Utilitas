package be.ephys.utilitas.feature.link_wand;

import be.ephys.utilitas.api.ILinkable;
import be.ephys.utilitas.base.helpers.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ItemLinker extends Item {

    public ItemLinker() {

        ItemHelper.name(this, "link_wand");
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean debug) {
        if (InputHelper.isShiftPressed()) {
            list.add(I18n.format("tooltip.utilitas:wand_usage_1"));
            list.add(I18n.format("tooltip.utilitas:wand_usage_2"));
            list.add(I18n.format("tooltip.utilitas:wand_usage_3"));
        } else {
            list.add(TextFormatting.LIGHT_PURPLE.toString() + TextFormatting.ITALIC.toString() + I18n.format("tooltip.utilitas:press_shift"));
        }

        ITextComponent bound = getLinkedObjectName(stack);

        ITextComponent tooltip;
        if (bound == null) {
            tooltip = new TextComponentTranslation("tooltip.utilitas:wand_unbound");
        } else {
            bound.setStyle(new Style().setColor(TextFormatting.AQUA));
            tooltip = new TextComponentTranslation("tooltip.utilitas:wand_bound", bound);
        }

        list.add(tooltip.getFormattedText());
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (player.isSneaking()) {
            bindToWand(stack, pos, player);

            return EnumActionResult.SUCCESS;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ILinkable)) {
            return EnumActionResult.PASS;
        }

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        ILinkable target = (ILinkable) te;
        target.link(player, getLinkedObject(stack));

        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

        if (!player.isSneaking()) {
            return super.onItemRightClick(stack, world, player, hand);
        }

        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        NBTTagCompound nbt = NBTHelper.getNBT(stack);

        if (nbt.hasKey("entity") || nbt.hasKey("tile")) {
            unbindWand(stack, player);
        } else {
            bindToWand(stack, player, player);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SubscribeEvent
    public void onEntityInteraction(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
        if (item == null || !(item.getItem() instanceof ItemLinker)) {
            return;
        }

        Entity target = event.getTarget();

        if (player.isSneaking()) {
            bindToWand(item, target, player);
            event.setCanceled(true);
            return;
        }

        if (target instanceof ILinkable) {
            ((ILinkable) target).link(player, getLinkedObject(item));
            event.setCanceled(true);
        }
    }

    public static Object getLinkedObject(ItemStack item) {
        NBTTagCompound nbt = NBTHelper.getNBT(item);

        int worldId = NBTHelper.getInt(item, "world", 0);
        World world = WorldHelper.getWorldForDim(worldId);
        if (world == null) {
            world = WorldHelper.getWorldForDim(0);
        }

        if (nbt.hasKey("entity")) {
            return EntityHelper.getEntityByUuid(NBTHelper.getUuid(nbt, "entity", null));
        }

        if (nbt.hasKey("tile")) {
            BlockPos pos = NBTUtil.getPosFromTag(nbt.getCompoundTag("tile"));
            return new WorldPos(pos, world);
        }

        return null;
    }

    private static ITextComponent getLinkedObjectName(ItemStack item) {
        Object obj = getLinkedObject(item);

        if (obj instanceof WorldPos) {
            WorldPos wp = (WorldPos) obj;
            BlockPos pos = wp.pos;
            World world = wp.world;

            return new TextComponentString(world.getWorldInfo().getWorldName() + " {" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "}");
        }

        if (obj instanceof Entity) {
            Entity entity = (Entity) obj;

            return entity.getDisplayName();
        }

        return null;
    }

    public static void bindToWand(ItemStack stack, BlockPos pos, EntityPlayer player) {
        NBTHelper.setInt(stack, "world", player.getEntityWorld().provider.getDimension());
        NBTHelper.setBlockPos(stack, "tile", pos);

        NBTHelper.getNBT(stack).removeTag("entity");

        printWandBoundMsg(stack, player);
    }

    public static void bindToWand(ItemStack stack, Entity entity, EntityPlayer player) {
        NBTHelper.setInt(stack, "world", entity.getEntityWorld().provider.getDimension());
        NBTHelper.setEntityUuid(stack, "entity", entity);
        NBTHelper.getNBT(stack).removeTag("tile");

        printWandBoundMsg(stack, player);
    }

    public static void unbindWand(ItemStack stack, EntityPlayer player) {
        NBTTagCompound nbt = NBTHelper.getNBT(stack);

        nbt.removeTag("entity");
        nbt.removeTag("tile");
        nbt.removeTag("world");

        ChatHelper.sendChatMessage(player, new TextComponentTranslation("message.utilitas:wand_unbound"));
    }

    private static void printWandBoundMsg(ItemStack stack, EntityPlayer player) {

        if (player.getEntityWorld().isRemote) {
            return;
        }

        ITextComponent boundItem = getLinkedObjectName(stack);
        boundItem.setStyle(new Style().setColor(TextFormatting.AQUA));

        ITextComponent msg = new TextComponentTranslation(
            "message.utilitas:wand_bound",
            stack.getDisplayName(),
            boundItem
        );

        ChatHelper.sendChatMessage(player, msg);
    }

    public static final class WorldPos {

        public final BlockPos pos;
        public final World world;

        public WorldPos(BlockPos pos, World world) {
            this.pos = pos;
            this.world = world;
        }
    }
}
