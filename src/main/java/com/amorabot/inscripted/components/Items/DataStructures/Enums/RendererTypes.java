package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum RendererTypes {
    /*These Enum constants are used to map to concrete implementations of ItemRenderer
    A Weapon instance may have a BASIC RendererType. This should map, in the Weapon's implementation of getRenderer()
    to a Renderer class that implements ItemRenderer and should correspond to a Basic-Weapon-Renderer

    Same thing for a CORRUPTED RendererType, for example. The Weapon's getRenderer() method should return a
    a implementation of a ItemRenderer that reflects Corrupted characteristics(Specific naming, skull icon, red-ish gray text...).
     */
    UNIDENTIFIED,
    BASIC,
    UNIQUE,
    CORRUPTED
}
