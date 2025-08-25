package IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MyDecompressorInputStream extends InputStream {

    private InputStream in;

    public MyDecompressorInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        ArrayList<Byte> decompressed = new ArrayList<>();
        int index = 0;
        int value;
        while ((value = in.read()) != -1) {
            byte val = (byte) value;
            byte count = (byte) in.read();
            for (int j = 0; j < Byte.toUnsignedInt(count); j++) {
                if (index >= b.length) {
                    return index; // Prevent overflow in case of mismatched compression
                }
                b[index++] = val;
            }
        }
        return index;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
