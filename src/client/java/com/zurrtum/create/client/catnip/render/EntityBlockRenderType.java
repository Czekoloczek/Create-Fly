package com.zurrtum.create.client.catnip.render;

import net.minecraft.client.renderer.rendertype.RenderType;

public enum EntityBlockRenderType {
    SOLID(
        PonderRenderTypes.getEntityBlockSolid(),
        PonderRenderTypes.getEntityBlockSolid(),
        PonderRenderTypes.getNetherEntityBlockSolid()
    ), CUTOUT(
        PonderRenderTypes.getEntityBlockCutout(),
        PonderRenderTypes.getEntityBlockCutout(),
        PonderRenderTypes.getNetherEntityBlockCutout()
    ), TRANSLUCENT(
        PonderRenderTypes.getEntityBlockTranslucent(),
        PonderRenderTypes.getEntityBlockTranslucent(),
        PonderRenderTypes.getNetherEntityBlockTranslucent()
    ), SOLID_LIGHT(
        PonderRenderTypes.getEntityBlockLightSolid(),
        PonderRenderTypes.getEntityBlockLightSolid(),
        PonderRenderTypes.getNetherEntityBlockLightSolid()
    ), CUTOUT_LIGHT(
        PonderRenderTypes.getEntityBlockLightCutout(),
        PonderRenderTypes.getEntityBlockLightCutout(),
        PonderRenderTypes.getNetherEntityBlockLightCutout()
    ), TRANSLUCENT_LIGHT(
        PonderRenderTypes.getEntityBlockLightTranslucent(),
        PonderRenderTypes.getEntityBlockLightTranslucent(),
        PonderRenderTypes.getNetherEntityBlockLightTranslucent()
    );
    private static final EntityBlockRenderType[] VALUES = values();
    private final RenderType type;
    private final RenderType overworld;
    private final RenderType nether;
    private static boolean isIrisActive = false;
    
    static {
        try {
            Class.forName("net.irisshaders.iris.pipeline.IrisPipelines");
            isIrisActive = true;
        } catch (ClassNotFoundException e) {
            isIrisActive = false;
        }
    }

    public static EntityBlockRenderType from(int index) {
        return VALUES[index];
    }

    EntityBlockRenderType(RenderType type, RenderType overworld, RenderType nether) {
        this.type = type;
        this.overworld = overworld;
        this.nether = nether;
    }

    public RenderType getRenderType(int cardinalLighting) {
        if (isIrisActive) {
            return switch (this) {
                case TRANSLUCENT -> CUTOUT.selectByLighting(cardinalLighting);
                case TRANSLUCENT_LIGHT -> CUTOUT_LIGHT.selectByLighting(cardinalLighting);
                default -> selectByLighting(cardinalLighting);
            };
        }
        return selectByLighting(cardinalLighting);
    }

    private RenderType selectByLighting(int cardinalLighting) {
        return switch (cardinalLighting) {
            case 1 -> overworld;
            case 2 -> nether;
            default -> type;
        };
    }

    public RenderType getLightRenderType(int cardinalLighting) {
        return switch (this) {
            case SOLID -> SOLID_LIGHT.getRenderType(cardinalLighting);
            case CUTOUT -> CUTOUT_LIGHT.getRenderType(cardinalLighting);
            case TRANSLUCENT -> TRANSLUCENT_LIGHT.getRenderType(cardinalLighting);
            default -> getRenderType(cardinalLighting);
        };
    }

    public boolean isLight() {
        return switch (this) {
            case SOLID_LIGHT, CUTOUT_LIGHT, TRANSLUCENT_LIGHT -> true;
            default -> false;
        };
    }
    
    public static boolean hasIris() {
        return isIrisActive;
    }
}
