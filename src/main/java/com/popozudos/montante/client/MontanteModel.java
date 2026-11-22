package com.popozudos.montante.client;

import com.popozudos.montante.MontanteMod;
import com.popozudos.montante.item.MontanteDoCometaItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MontanteModel extends GeoModel<MontanteDoCometaItem> {
    @Override
    public ResourceLocation getModelResource(MontanteDoCometaItem item) {
        return new ResourceLocation(MontanteMod.MODID, "geo/montante_do_cometa.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MontanteDoCometaItem item) {
        return new ResourceLocation(MontanteMod.MODID, "textures/item/montante_do_cometa.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MontanteDoCometaItem item) {
        return new ResourceLocation(MontanteMod.MODID, "animations/montante_do_cometa.animation.json");
    }
}
