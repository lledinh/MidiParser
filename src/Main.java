import encoding.VariableLengthQuantity;
import file.BytesFileReader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        VariableLengthQuantity.encode(106903);
        int decodeNumber = VariableLengthQuantity.decode(8831767);
        System.out.println("decodeNumber = " + decodeNumber);
        int decodeBytes = VariableLengthQuantity.decode(new int[]{23, 195, 134, 0});
        System.out.println("decodeBytes = " + decodeBytes);

        byte[] bytes = BytesFileReader.read("./doc/note_la.mid");

        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]);
        }
    }
}
