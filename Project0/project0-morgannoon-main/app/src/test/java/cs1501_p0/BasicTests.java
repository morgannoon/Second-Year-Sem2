/**
 * Basic tests for CS1501 Project 0
 * @author    Dr. Farnan
 */
package cs1501_p0;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;

public class BasicTests {

    @Test
    public void testDoneMessage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        // action
        Done.main(null);

        // assertion
        assertEquals("DONE\n", bos.toString(), "Does not output DONE");

        // undo the binding in System
        System.setOut(originalOut);
    }

    @Test
    public void testExtraMessage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        // action
        Extra.main(null);

        // assertion
        String output = bos.toString();
        assertTrue(output.contains("CS1501"), "Output string does not contain CS1501");

        // undo the binding in System
        System.setOut(originalOut);
    }
}
