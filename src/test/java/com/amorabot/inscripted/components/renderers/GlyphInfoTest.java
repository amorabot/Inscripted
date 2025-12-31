package com.amorabot.inscripted.components.renderers;

import com.amorabot.inscripted.item.render.GlyphInfo;
import com.amorabot.inscripted.item.render.ItemRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlyphInfoTest {

    public GlyphInfoTest(){
        GlyphInfo.loadMappings();
    }

    @Test
    void countRuneDivLength() {
//        assertEquals(34,GlyphInfo.countStringPixelLength(.TOP_RUNIC_BAR)); //   '- --=÷¦• '
    }
    @Test
    void countHPIndicator() {
        // ʜᴘ:
        assertEquals(11,GlyphInfo.countStringPixelLength("ʜᴘ:"));
    }
}