package com.popozudos.montante.client;

import com.popozudos.montante.item.MontanteDoCometaItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MontanteRenderer extends GeoItemRenderer<MontanteDoCometaItem> {
    public MontanteRenderer() {
        super(new MontanteModel());
    }
}
