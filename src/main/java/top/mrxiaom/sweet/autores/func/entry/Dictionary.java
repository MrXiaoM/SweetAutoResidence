package top.mrxiaom.sweet.autores.func.entry;

import java.util.Random;

public class Dictionary {
    private final Random random = new Random();
    private final String id;
    private final char[] chars;
    private final int lengthMin, lengthMax;

    public Dictionary(String id, char[] chars, int lengthMin, int lengthMax) {
        this.id = id;
        this.chars = chars;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
    }

    public String id() {
        return id;
    }

    public String generate() {
        int length = lengthMin < lengthMax
                ? (lengthMin + random.nextInt(lengthMax - lengthMin))
                : lengthMin;
        int size = chars.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(chars[random.nextInt(size)]);
        }
        return builder.toString();
    }
}
