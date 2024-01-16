public class Base64 {
    private static final char[] BASE64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] BASE64_REVERSE_TABLE = new int[256];
    static {
        for (int i = 0; i < BASE64_REVERSE_TABLE.length; i++) {
            BASE64_REVERSE_TABLE[i] = -1;
        }
        for (int i = 0; i < BASE64_TABLE.length; i++) {
            BASE64_REVERSE_TABLE[BASE64_TABLE[i]] = i;
        }
        BASE64_REVERSE_TABLE['='] = 0;
    }

    public static String encode(byte[] data) {
        StringBuilder result = new StringBuilder();
        int paddingCount = (3 - data.length % 3) % 3;
        for (int i = 0; i < data.length; i += 3) {
            int b1 = data[i] & 0xff;
            int b2 = i + 1 < data.length ? data[i + 1] & 0xff : 0;
            int b3 = i + 2 < data.length ? data[i + 2] & 0xff : 0;
            int n = (b1 << 16) | (b2 << 8) | b3;
            result.append(BASE64_TABLE[(n >> 18) & 0x3f]);
            result.append(BASE64_TABLE[(n >> 12) & 0x3f]);
            result.append(BASE64_TABLE[(n >> 6) & 0x3f]);
            result.append(BASE64_TABLE[n & 0x3f]);
        }
        for (int i = 0; i < paddingCount; i++) {
            result.setCharAt(result.length() - 1 - i, '=');
        }
        return result.toString();
    }

    public static byte[] decode(String encodedData) {
        int paddingCount = 0;
        for (int i = encodedData.length() - 1; i >= 0 && encodedData.charAt(i) == '='; i--) {
            paddingCount++;
        }
        int dataLength = (encodedData.length() * 6 + 7) / 8 - paddingCount;
        byte[] data = new byte[dataLength];
        int dataIndex = 0;
        int n = 0;
        for (int i = 0; i < encodedData.length(); i++) {
            char c = encodedData.charAt(i);
            int b = BASE64_REVERSE_TABLE[c];
            if (b != -1) {
                n = (n << 6) | b;
                if (i % 4 == 3) {
                    data[dataIndex++] = (byte) (n >> 16);
                    data[dataIndex++] = (byte) (n >> 8);
                    data[dataIndex++] = (byte) (n);
                    n = 0;
                }
            }
        }
        if (paddingCount == 1) {
            data[dataLength - 1] = (byte) (n >> 4);
        } else if (paddingCount == 2) {
            data[dataLength - 2] = (byte) (n >> 10);
            data[dataLength - 1] = (byte) (n >> 2);
        }
        return data;
    }
}
