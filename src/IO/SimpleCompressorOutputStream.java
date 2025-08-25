package IO;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleCompressorOutputStream extends OutputStream {
    OutputStream out;
    public SimpleCompressorOutputStream() {
    }

    public SimpleCompressorOutputStream(OutputStream out) {
        this.out = out;

    }

    @Override
    public void write(int b) throws IOException {
        // Implementing write(int b) is straightforward.
        // However, for compression, you need to handle byte arrays.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void compress(byte[] uncompressedData) throws IOException {
        compress(uncompressedData, out);
    }

    public void compress(byte[] uncompressedData, OutputStream outputStream) throws IOException {
        if (uncompressedData == null || uncompressedData.length == 0) {
            return;
        }

        byte current = uncompressedData[0];
        int count = 1;
        for (int i = 1; i <= uncompressedData.length; i++) {
            if (i < uncompressedData.length && uncompressedData[i] == current && count < 255) {
                count++;
            } else {
                outputStream.write(current);
                outputStream.write((byte) count);
                if (i < uncompressedData.length) {
                    current = uncompressedData[i];
                    count = 1;
                }
            }
        }
    }
}
