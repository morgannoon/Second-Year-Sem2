/**
 * A driver for CS1501 LZW Lab
 * @author    Dr. Farnan
 */
package cs1501_lzw_lab;

import java.io.*;

public class App {
    public static void main(String[] args) {
        // Set new I/O streams to compress
        try {
            FileInputStream inf = new FileInputStream(new File("build/resources/main/small.txt"));
            BinaryStdIn.setIn(inf);
        }
        catch (Exception e) {
            System.out.println("Error opening sample file");
        }
        ByteArrayOutputStream compress_os = new ByteArrayOutputStream();
        BinaryStdOut.setOut(new PrintStream(compress_os));

        // Compress
        LZW.compress();

        int compressed_len = compress_os.size();

        // Set new I/O streams to expand
        ByteArrayInputStream expand_is = new ByteArrayInputStream(compress_os.toByteArray());
        BinaryStdIn.setIn(expand_is);
        ByteArrayOutputStream expand_os = new ByteArrayOutputStream();
        BinaryStdOut.setOut(new PrintStream(expand_os));

        // Expand
        LZW.expand();

        // Output
        System.out.printf("Compressed length was %d\n", compressed_len);
        System.out.println("Expanded content was:");
        System.out.println("'''");
        System.out.println(expand_os.toString());
        System.out.println("'''");
    }
}
