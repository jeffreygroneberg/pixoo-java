package com.pixoo.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Font support for Pixoo devices using PICO-8 style font glyphs.
 */
public class Font {
    
    private static final Map<Character, int[]> FONT_PICO_8 = new HashMap<>();
    
    static {
        // Numbers 0-9
        FONT_PICO_8.put('0', new int[]{1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('1', new int[]{1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('2', new int[]{1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('3', new int[]{1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('4', new int[]{1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('5', new int[]{1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('6', new int[]{1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('7', new int[]{1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('8', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('9', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1});
        
        // Lowercase letters a-z
        FONT_PICO_8.put('a', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1});
        FONT_PICO_8.put('b', new int[]{0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('c', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1});
        FONT_PICO_8.put('d', new int[]{0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('e', new int[]{0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1});
        FONT_PICO_8.put('f', new int[]{0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('g', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('h', new int[]{0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1});
        FONT_PICO_8.put('i', new int[]{0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('j', new int[]{0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('k', new int[]{0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('l', new int[]{0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1});
        FONT_PICO_8.put('m', new int[]{0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('n', new int[]{0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('o', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('p', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1});
        FONT_PICO_8.put('q', new int[]{0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1});
        FONT_PICO_8.put('r', new int[]{0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1});
        FONT_PICO_8.put('s', new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('t', new int[]{0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('u', new int[]{0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('v', new int[]{0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1});
        FONT_PICO_8.put('w', new int[]{0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1});
        FONT_PICO_8.put('x', new int[]{0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1});
        FONT_PICO_8.put('y', new int[]{0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('z', new int[]{0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1});
        
        // Uppercase letters A-Z
        FONT_PICO_8.put('A', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('B', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('C', new int[]{0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1});
        FONT_PICO_8.put('D', new int[]{1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('E', new int[]{1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('F', new int[]{1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('G', new int[]{0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('H', new int[]{1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('I', new int[]{1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('J', new int[]{1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('K', new int[]{1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('L', new int[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('M', new int[]{1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('N', new int[]{1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('O', new int[]{0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1});
        FONT_PICO_8.put('P', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1});
        FONT_PICO_8.put('Q', new int[]{0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1});
        FONT_PICO_8.put('R', new int[]{1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('S', new int[]{0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('T', new int[]{1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('U', new int[]{1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('V', new int[]{1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1});
        FONT_PICO_8.put('W', new int[]{1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1});
        FONT_PICO_8.put('X', new int[]{1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1});
        FONT_PICO_8.put('Y', new int[]{1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1});
        FONT_PICO_8.put('Z', new int[]{1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1});
        
        // Special characters
        FONT_PICO_8.put('!', new int[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1});
        FONT_PICO_8.put('\'', new int[]{0, 1, 0, 1});
        FONT_PICO_8.put('(', new int[]{0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1});
        FONT_PICO_8.put(')', new int[]{0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1});
        FONT_PICO_8.put('+', new int[]{0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1});
        FONT_PICO_8.put(',', new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1});
        FONT_PICO_8.put('-', new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('<', new int[]{0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1});
        FONT_PICO_8.put('=', new int[]{0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1});
        FONT_PICO_8.put('>', new int[]{1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1});
        FONT_PICO_8.put('?', new int[]{1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1});
        FONT_PICO_8.put('[', new int[]{1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1});
        FONT_PICO_8.put(']', new int[]{0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('^', new int[]{0, 1, 0, 1, 0, 1});
        FONT_PICO_8.put('_', new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1});
        FONT_PICO_8.put(':', new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0});
        FONT_PICO_8.put(';', new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1});
        FONT_PICO_8.put('.', new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        FONT_PICO_8.put('/', new int[]{0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1});
        FONT_PICO_8.put('{', new int[]{0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1});
        FONT_PICO_8.put('|', new int[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1});
        FONT_PICO_8.put('}', new int[]{1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1});
        FONT_PICO_8.put('~', new int[]{0, 0, 0, 0, 0, 1, 1, 1, 1, 1});
        FONT_PICO_8.put('$', new int[]{1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1});
        FONT_PICO_8.put('@', new int[]{0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1});
        FONT_PICO_8.put('%', new int[]{1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1});
        
        // Space character
        FONT_PICO_8.put(' ', new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }
    
    /**
     * Retrieves the glyph matrix for a character.
     * @param character The character to get the glyph for
     * @return The glyph matrix as an array of integers (0 or 1), or null if not supported
     */
    public static int[] retrieveGlyph(char character) {
        return FONT_PICO_8.get(character);
    }
    
    /**
     * Returns the set of supported characters.
     * @return Set of characters that have glyphs available
     */
    public static Set<Character> getSupportedCharacters() {
        return FONT_PICO_8.keySet();
    }
    
    /**
     * Checks if a character is supported.
     * @param character The character to check
     * @return true if the character has a glyph available
     */
    public static boolean isCharacterSupported(char character) {
        return FONT_PICO_8.containsKey(character);
    }
}
