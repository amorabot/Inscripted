package com.amorabot.inscripted.components.renderers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlyphInfoTest {

    public GlyphInfoTest(){
        GlyphInfo.loadMappings();
    }

    @Test
    void countRuneDivLength() {
        assertEquals(34,GlyphInfo.countStringPixelLength(ItemInterfaceRenderer.TOP_RUNIC_BAR)); //   '- --=÷¦• '
    }
    @Test
    void countHPIndicator() {
        // ʜᴘ:
        assertEquals(11,GlyphInfo.countStringPixelLength("ʜᴘ:"));
    }
}