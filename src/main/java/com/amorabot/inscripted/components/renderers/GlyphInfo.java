package com.amorabot.inscripted.components.renderers;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum GlyphInfo {
    DEFAULT(' ', 4),
    UNICODE_0020(' ', 3),
    UNICODE_0021('!', 1),
    UNICODE_0022('"', 3),
    UNICODE_0023('#', 5),
    UNICODE_0024('$', 5),
    UNICODE_0025('%', 5),
    UNICODE_0026('&', 5),
    UNICODE_0027('\'', 1),
    UNICODE_0028('(', 3),
    UNICODE_0029(')', 3),
    UNICODE_002A('*', 3),
    UNICODE_002B('+', 5),
    UNICODE_002C(',', 1),
    UNICODE_002D('-', 5),
    UNICODE_002E('.', 1),
    UNICODE_002F('/', 5),
    UNICODE_0030('0', 5),
    UNICODE_0031('1', 5),
    UNICODE_0032('2', 5),
    UNICODE_0033('3', 5),
    UNICODE_0034('4', 5),
    UNICODE_0035('5', 5),
    UNICODE_0036('6', 5),
    UNICODE_0037('7', 5),
    UNICODE_0038('8', 5),
    UNICODE_0039('9', 5),
    UNICODE_003A(':', 1),
    UNICODE_003B(';', 1),
    UNICODE_003C('<', 4),
    UNICODE_003D('=', 5),
    UNICODE_003E('>', 4),
    UNICODE_003F('?', 5),
    UNICODE_0040('@', 6),
    UNICODE_0041('A', 5),
    UNICODE_0042('B', 5),
    UNICODE_0043('C', 5),
    UNICODE_0044('D', 5),
    UNICODE_0045('E', 5),
    UNICODE_0046('F', 5),
    UNICODE_0047('G', 5),
    UNICODE_0048('H', 5),
    UNICODE_0049('I', 3),
    UNICODE_004A('J', 5),
    UNICODE_004B('K', 5),
    UNICODE_004C('L', 5),
    UNICODE_004D('M', 5),
    UNICODE_004E('N', 5),
    UNICODE_004F('O', 5),
    UNICODE_0050('P', 5),
    UNICODE_0051('Q', 5),
    UNICODE_0052('R', 5),
    UNICODE_0053('S', 5),
    UNICODE_0054('T', 5),
    UNICODE_0055('U', 5),
    UNICODE_0056('V', 5),
    UNICODE_0057('W', 5),
    UNICODE_0058('X', 5),
    UNICODE_0059('Y', 5),
    UNICODE_005A('Z', 5),
    UNICODE_005B('[', 3),
    UNICODE_005C('\\', 5),
    UNICODE_005D(']', 3),
    UNICODE_005E('^', 5),
    UNICODE_005F('_', 5),
    UNICODE_2605('★', 7),
    UNICODE_2606('☆', 7),
    UNICODE_0061('a', 5),
    UNICODE_0062('b', 5),
    UNICODE_0063('c', 5),
    UNICODE_0064('d', 5),
    UNICODE_0065('e', 5),
    UNICODE_0066('f', 4),
    UNICODE_0067('g', 5),
    UNICODE_0068('h', 5),
    UNICODE_0069('i', 1),
    UNICODE_006A('j', 5),
    UNICODE_006B('k', 4),
    UNICODE_006C('l', 2),
    UNICODE_006D('m', 5),
    UNICODE_006E('n', 5),
    UNICODE_006F('o', 5),
    UNICODE_0070('p', 5),
    UNICODE_0071('q', 5),
    UNICODE_0072('r', 5),
    UNICODE_0073('s', 5),
    UNICODE_0074('t', 3),
    UNICODE_0075('u', 5),
    UNICODE_0076('v', 5),
    UNICODE_0077('w', 5),
    UNICODE_0078('x', 5),
    UNICODE_0079('y', 5),
    UNICODE_007A('z', 5),
    UNICODE_00F7('÷', 5),
    UNICODE_00A6('¦', 1),
    UNICODE_007C('|', 1),
    UNICODE_00B7('·', 1),
    UNICODE_2022('•', 2),
    UNICODE_00AF('¯', 5),
    UNICODE_2764('❤', 7),
    UNICODE_2744('❄', 7),
    UNICODE_26A1('⚡', 5),
    UNICODE_1D00('ᴀ', 5),
    UNICODE_0299('ʙ', 5),
    UNICODE_1D04('ᴄ', 5),
    UNICODE_1D05('ᴅ', 5),
    UNICODE_1D07('ᴇ', 5),
    UNICODE_1D08('ꜰ', 5),
    UNICODE_0262('ɢ', 5),
    UNICODE_029C('ʜ', 5),
    UNICODE_026A('ɪ', 3),
    UNICODE_1D0A('ᴊ', 5),
    UNICODE_1D0B('ᴋ', 5),
    UNICODE_029F('ʟ', 5),
    UNICODE_1D0D('ᴍ', 5),
    UNICODE_0274('ɴ', 5),
    UNICODE_1D0F('ᴏ', 5),
    UNICODE_1D18('ᴘ', 5),
    UNICODE_A7AF('ꞯ', 5),
    UNICODE_01A6('ʀ', 5),
    UNICODE_A731('ꜱ', 5),
    UNICODE_1D1B('ᴛ', 5),
    UNICODE_1D1C('ᴜ', 5),
    UNICODE_1D20('ᴠ', 5),
    UNICODE_1D21('ᴡ', 5),
    UNICODE_1D22('ᴢ', 5),

    SHIELD(DefenceTypes.ARMOR.getSpecialChar().charAt(0), 7),
    WARD(DefenceTypes.WARD.getSpecialChar().charAt(0), 7),
    FIRE(DefenceTypes.FIRE.getSpecialChar().charAt(0), 7),
    MOON(DefenceTypes.ABYSSAL.getSpecialChar().charAt(0), 7),
    DODGE('✦', 7);
/*      535555123 = 34
--=÷•   - --=÷¦•
ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ
*/

    @Getter
    private final Character unicode;
    @Getter
    private final int width;
    private static final Map<Character, Integer> sizeMappings = new HashMap<>();

    GlyphInfo(Character unicode, int pixelSize){
        this.unicode = unicode;
        this.width = pixelSize;
    }

    public static void loadMappings(){
        for (GlyphInfo glyph : GlyphInfo.values()){
            sizeMappings.put(glyph.getUnicode(),glyph.getWidth());
        }
    }

    public static int getGlyphWidth(Character character){
        if (!sizeMappings.containsKey(character)){
            Utils.error("Pixel size mapping not found for: " + character);
        }
        return sizeMappings.getOrDefault(character,5);
    }

    public static int countStringPixelLength(String s){
        int len = 0;
        char[] characters = s.toCharArray();
        for (char c : characters){
            len += getGlyphWidth(c);
        }
        return len;
    }
}
