package encoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableLengthQuantity {

    public static int decode(int vlqNumber) {
        // Get each bytes separately
        int[] vlqBytes = new int[] {
                (vlqNumber & 0x000000FF),
                ((vlqNumber & 0x0000FF00) >> 8),
                ((vlqNumber & 0x00FF0000) >> 16),
                ((vlqNumber & 0xFF000000) >> 24),
        };

        return decode(vlqBytes);
    }


    public static int decode(int[] vlqBytes) {

        return (vlqBytes[0] & 0x7f) + ((vlqBytes[1] & 0x7f) << 7) + ((vlqBytes[2] & 0x7f) << 14) + ((vlqBytes[3] & 0x7f) << 21);
    }

    public static int encode(int number) {
        int[] bytes = new int[] {
                (number & 0x7F),
                (number >> 7) & 0x7F,
                (number >> 14) & 0x7F,
                (number >> 21) & 0x7F,
        };

        int[] vlqBytes = new int[4];

        for (int i = 0; i < bytes.length; i++) {
            if (i == 0) {
                vlqBytes[i] = (bytes[i] & 0x7F);
            }
            else {
                if (bytes[i] > 0) {
                    vlqBytes[i] = (bytes[i] | 0x80);
                }
                else {
                    vlqBytes[i] = 0;
                }
            }
        }

        return vlqBytes[0] + (vlqBytes[1] << 8) + (vlqBytes[2] << 16) + (vlqBytes[3] << 24);
    }
}
