/**
 * Basic tests for CS1501 LZW Lab
 * @author    Dr. Farnan
 */
package cs1501_lzw_lab;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;
import java.nio.file.*;

import static java.time.Duration.ofSeconds;

class BasicTests {
    final int DEFAULT_TIMEOUT = 10;

    @Test
    @DisplayName("Testing compressed size")
    void basic_comp_size_test() {
        try {
            FileInputStream inf = new FileInputStream(new File("build/resources/test/large.txt"));
            BinaryStdIn.setIn(inf);
        }
        catch (Exception e) {
            assertTrue(false);
        }

        ByteArrayOutputStream compress_os = new ByteArrayOutputStream();
        BinaryStdOut.setOut(new PrintStream(compress_os));

        LZW.compress();

        assertEquals(501776, compress_os.size());
    }

    @Test
    @DisplayName("Testing correct expansion")
    void basic_comp_exp_test() {
        String test_file = "build/resources/test/large.txt";
        String original = "";
        try {
            original = Files.readString(Path.of(test_file));
            FileInputStream inf = new FileInputStream(new File(test_file));
            BinaryStdIn.setIn(inf);
        }
        catch (Exception e) {
            assertTrue(false);
        }

        ByteArrayOutputStream compress_os = new ByteArrayOutputStream();
        BinaryStdOut.setOut(new PrintStream(compress_os));

        LZW.compress();
        
        ByteArrayInputStream expand_is = new ByteArrayInputStream(compress_os.toByteArray());
        BinaryStdIn.setIn(expand_is);
        ByteArrayOutputStream expand_os = new ByteArrayOutputStream();
        BinaryStdOut.setOut(new PrintStream(expand_os));

        LZW.expand();

        assertEquals(original, expand_os.toString());
    }
}
