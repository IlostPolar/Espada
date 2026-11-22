package com.popozudos.montante;

import com.popozudos.montante.item.MontanteDoCometaItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MontanteMod.MODID);

    // Dano total = 1 (base) + 4 (netherite) + 5 = 10 | velocidade 1.0 (pesada, tipo montante)
    public static final RegistryObject<Item> MONTANTE_DO_COMETA = ITEMS.register("montante_do_cometa",
            () -> new MontanteDoCometaItem(Tiers.NETHERITE, 5, -3.0F,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
}
