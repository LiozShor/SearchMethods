package IO;

import java.io.IOException;
import java.io.InputStream;

public class SimpleDecompressorInputStream extends InputStream {

    private byte[] decompressedData;
    private int currentPosition;
    SimpleDecompressorInputStream(InputStream in) {

    }

    public SimpleDecompressorInputStream(byte[] compressedData) {
        this.decompressedData = decompress(compressedData);
        this.currentPosition = 0;
    }

    @Override
    public int read() throws IOException {
        if (currentPosition >= decompressedData.length) {
            return -1; // End of stream
        }
        return decompressedData[currentPosition++];
    }

    // Decompress method as you originally defined
    private byte[] decompress(byte[] b) {
        if (b == null || b.length == 0) {
            return new byte[0];
        }

        int size = 0;
        for (int i = 1; i < b.length; i += 2) {
            size += b[i];
        }

        return getBytes(b, size);
    }

    private byte[] getBytes(byte[] b, int size) {
        byte[] decompressed = new byte[size];
        int index = 0;

        for (int i = 0; i < b.length; i += 2) {
            for (int j = 0; j < b[i + 1]; j++) {
                decompressed[index++] = b[i];
            }
        }

        return decompressed;
    }
}
