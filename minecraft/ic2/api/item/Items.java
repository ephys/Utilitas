/*     */ package ic2.api.item;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import net.minecraft.item.ItemStack;
/*     */ 
/*     */ public final class Items
/*     */ {
/*     */   private static Class<?> Ic2Items;
/*     */ 
/*     */   public static ItemStack getItem(String name)
/*     */   {
/*     */     try
/*     */     {
/*  27 */       if (Ic2Items == null) Ic2Items = Class.forName(getPackage() + ".core.Ic2Items");
/*     */ 
/*  29 */       Object ret = Ic2Items.getField(name).get(null);
/*     */ 
/*  31 */       if ((ret instanceof ItemStack)) {
/*  32 */         return (ItemStack)ret;
/*     */       }
/*  34 */       return null;
/*     */     }
/*     */     catch (Exception e) {
/*  37 */       System.out.println("IC2 API: Call getItem failed for " + name);
/*     */     }
/*  39 */     return null;
/*     */   }
/*     */ 
/*     */   private static String getPackage()
/*     */   {
/* 587 */     Package pkg = Items.class.getPackage();
/*     */ 
/* 589 */     if (pkg != null) {
/* 590 */       String packageName = pkg.getName();
/*     */ 
/* 592 */       return packageName.substring(0, packageName.length() - ".api.item".length());
/*     */     }
/*     */ 
/* 595 */     return "ic2";
/*     */   }
/*     */ }

/* Location:           /home/ephys/binnie-mods-1.8.0.jar
 * Qualified Name:     ic2.api.item.Items
 * JD-Core Version:    0.6.2
 */