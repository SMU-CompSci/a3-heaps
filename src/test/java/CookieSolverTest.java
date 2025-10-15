import com.student_work.CookieSolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class CookieSolverTest {

    @BeforeEach
    void setUp() {
        // Ensure a clean stdin for every test
        flushStdIn();   // installs empty System.in and resets StdIn.scanner
    }


    /**
     * Helper method to run CookieSolver.main() with simulated stdin and capture stdout
     * @param n number of cookies
     * @param k threshold value
     * @param A array of cookie sweetness values
     * @return the output from the program (the number of operations or -1)
     */
    private int runCookieSolver(int n, long k, long[] A) {
        StringBuilder input = new StringBuilder();
        input.append(n).append(" ").append(k).append("\n");
        for (int i = 0; i < A.length; i++) {
            if (i > 0) input.append(" ");
            input.append(A[i]);
        }
        input.append("\n");

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayInputStream testIn = null;
        ByteArrayOutputStream testOut = null;

        try {
            // 1) Start from a clean stdin state
            flushStdIn();

            // 2) Provide this test's input
            testIn = new ByteArrayInputStream(input.toString().getBytes());
            System.setIn(testIn);

            // 3) Force StdIn to rebind to the new System.in
            bindStdIn();

            // 4) Capture stdout; silence stderr (to keep test logs clean)
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut, true));
            System.setErr(new PrintStream(new ByteArrayOutputStream(), true));

            // 5) Run program
            CookieSolver.main(new String[]{});

            System.out.flush();
            System.err.flush();

            // 6) Parse single integer answer
            String output = testOut.toString().trim();
            return Integer.parseInt(output);
        } finally {
            // Close streams we created
            if (testIn != null) try { testIn.close(); } catch (IOException ignored) {}
            if (testOut != null) try { testOut.close(); } catch (IOException ignored) {}

            // Restore originals
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);

            // leave a clean, empty stdin for the next test
            flushStdIn();
        }
    }

    // --- replace both helpers with these ---
    private void flushStdIn() {
        System.setIn(new ByteArrayInputStream(new byte[0])); // empty
        bindStdIn(); // bind StdIn.scanner to the (empty) System.in
    }

    /** Force edu.princeton.cs.algs4.StdIn to read from the current System.in */
    private void bindStdIn() {
        try {
            Class<?> stdInClass = Class.forName("edu.princeton.cs.algs4.StdIn");
            java.lang.reflect.Field scannerField = stdInClass.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            // bind to whatever System.in is *right now*
            scannerField.set(null, new java.util.Scanner(System.in));
        } catch (Exception e) {
            System.err.println("Warning: Could not bind StdIn scanner: " + e.getMessage());
        }
    }


    // ========== Sample Test Cases from Problem ==========

    @Test
    public void testSampleInput() {
        // Sample from problem: A = [1, 2, 3, 9, 10, 12], k = 7
        // Expected: 2 operations
        long[] A = {1L, 2L, 3L, 9L, 10L, 12L};
        int result = runCookieSolver(6, 7L, A);
        assertEquals(2, result);
    }

    @Test
    public void testExampleFromDescription() {
        // Example: k = 9, A = [2, 7, 3, 6, 4, 6]
        // Expected: 4 operations
        long[] A = {2L, 7L, 3L, 6L, 4L, 6L};
        int result = runCookieSolver(6, 9L, A);
        assertEquals(4, result);
    }

    // ========== Already Satisfied Conditions ==========

    @Test
    public void testAllCookiesAlreadySweetEnough() {
        // All cookies already have sweetness >= k
        long[] A = {10L, 15L, 20L, 25L};
        int result = runCookieSolver(4, 8L, A);
        assertEquals(0, result);
    }

    @Test
    public void testAllCookiesEqualToK() {
        long[] A = {5L, 5L, 5L, 5L};
        int result = runCookieSolver(4, 5L, A);
        assertEquals(0, result);
    }

    @Test
    public void testSingleCookieSweetEnough() {
        long[] A = {100L};
        int result = runCookieSolver(1, 50L, A);
        assertEquals(0, result);
    }

    // ========== Impossible Cases (Return -1) ==========

    @Test
    public void testSingleCookieNotSweetEnough() {
        // Only one cookie and it's below threshold
        long[] A = {5L};
        int result = runCookieSolver(1, 10L, A);
        assertEquals(-1, result);
    }

    @Test
    public void testImpossibleToReachThreshold() {
        // Even after mixing all cookies, can't reach k
        long[] A = {1L, 1L};
        int result = runCookieSolver(2, 1000000L, A);
        assertEquals(-1, result);
    }

    @Test
    public void testImpossibleWithMultipleCookies() {
        // [1, 2, 3] -> mix 1,2 = 5 -> [3, 5] -> mix 3,5 = 13
        // But k is too high
        long[] A = {1L, 2L, 3L};
        int result = runCookieSolver(3, 1000L, A);
        assertEquals(-1, result);
    }

    @Test
    public void testImpossibleLastCookieTooSmall() {
        // After multiple operations, left with one cookie < k
        long[] A = {1L, 1L, 1L};
        int result = runCookieSolver(3, 100L, A);
        assertEquals(-1, result);
    }

    // ========== Simple Cases ==========

    @Test
    public void testTwoCookiesOneOperation() {
        // Two cookies, mix once to reach threshold
        long[] A = {2L, 3L};
        int result = runCookieSolver(2, 8L, A);
        // mix: 2 + 2*3 = 8
        assertEquals(1, result);
    }

    @Test
    public void testTwoCookiesBelowThreshold() {
        // Two cookies, even after mixing still below threshold
        long[] A = {1L, 2L};
        int result = runCookieSolver(2, 100L, A);
        assertEquals(-1, result);
    }

    @Test
    public void testTwoCookiesExactMatch() {
        // Mix results in exactly k
        long[] A = {3L, 5L};
        int result = runCookieSolver(2, 13L, A);
        // mix: 3 + 2*5 = 13
        assertEquals(1, result);
    }

    // ========== Multiple Operations ==========

    @Test
    public void testThreeCookiesTwoOperations() {
        long[] A = {1L, 2L, 3L};
        int result = runCookieSolver(3, 7L, A);
        // Step 1: mix 1,2 -> 5, array = [3, 5]
        // Step 2: mix 3,5 -> 13, array = [13]
        // 13 >= 7, so 2 operations
        assertEquals(2, result);
    }

    @Test
    public void testMultipleOperationsWithLargerArray() {
        long[] A = {1L, 2L, 3L, 4L, 5L};
        int result = runCookieSolver(5, 20L, A);
        // Should require multiple mixes
        assertTrue(result > 0 && result != -1);
    }

    // ========== Zero and Small Values ==========

    @Test
    public void testWithZeroValues() {
        long[] A = {0L, 1L, 2L};
        int result = runCookieSolver(3, 5L, A);
        // mix 0,1 -> 2, array = [2, 2]
        // mix 2,2 -> 6, array = [6]
        // 6 >= 5
        assertEquals(2, result);
    }

    @Test
    public void testAllZeros() {
        long[] A = {0L, 0L, 0L};
        int result = runCookieSolver(3, 1L, A);
        // mix 0,0 -> 0, array = [0, 0]
        // mix 0,0 -> 0, array = [0]
        // Can't reach 1
        assertEquals(-1, result);
    }

    @Test
    public void testKIsZero() {
        // When k = 0, all cookies satisfy the condition
        long[] A = {0L, 1L, 2L, 3L};
        int result = runCookieSolver(4, 0L, A);
        assertEquals(0, result);
    }

    @Test
    public void testSingleZeroCookieWithKZero() {
        long[] A = {0L};
        int result = runCookieSolver(1, 0L, A);
        assertEquals(0, result);
    }

    // ========== Edge Cases with Duplicates ==========

    @Test
    public void testAllSameValuesBelowK() {
        long[] A = {3L, 3L, 3L, 3L};
        int result = runCookieSolver(4, 15L, A);
        // mix 3,3 -> 9, array = [3, 3, 9]
        // mix 3,3 -> 9, array = [9, 9]
        // mix 9,9 -> 27, array = [27]
        // 27 >= 15
        assertEquals(3, result);
    }

    @Test
    public void testDuplicateValues() {
        long[] A = {2L, 2L, 4L, 4L, 6L};
        int result = runCookieSolver(5, 10L, A);
        assertTrue(result >= 0); // Should be possible
    }

    // ========== Large Values ==========

    @Test
    public void testLargeKValue() {
        long[] A = {500000L, 600000L, 7000000L};
        int result = runCookieSolver(3, 1000000L, A);
        // mix 500000, 600000 -> 1700000
        // 1700000 >= 1000000
        assertEquals(1, result);
    }

    @Test
    public void testMaxConstraintValues() {
        // Maximum values from constraints
        long[] A = {1000000L, 1000000L, 1000000L};
        long k = 1000000L;
        int result = runCookieSolver(3, k, A);
        assertEquals(0, result);
    }

    @Test
    public void testLargeKImpossible() {
        long[] A = {1L, 2L, 3L};
        long k = 1000000000L; // Maximum k
        int result = runCookieSolver(3, k, A);
        assertEquals(-1, result);
    }

    // ========== Boundary Cases ==========

    @Test
    public void testMinimumArraySize() {
        // Minimum possible array with operations needed
        long[] A = {1L, 1L};
        int result = runCookieSolver(2, 2L, A);
        // mix 1,1 -> 3
        // 3 >= 2
        assertEquals(1, result);
    }

    @Test
    public void testJustBarelyReachK() {
        // Design case where final value is just >= k
        long[] A = {5L, 6L};
        int result = runCookieSolver(2, 17L, A);
        // mix 5,6 -> 17
        assertEquals(1, result);
    }

    @Test
    public void testJustBelowK() {
        // After mixing, result is just below k
        long[] A = {5L, 6L};
        int result = runCookieSolver(2, 18L, A);
        // mix 5,6 -> 17, which is < 18
        assertEquals(-1, result);
    }

    // ========== Order Independence ==========

    @Test
    public void testOrderDoesNotMatter() {
        // Same values, different order
        long[] A1 = {3L, 1L, 2L, 5L, 4L};
        long[] A2 = {1L, 2L, 3L, 4L, 5L};
        long[] A3 = {5L, 4L, 3L, 2L, 1L};

        long k = 10L;
        int result1 = runCookieSolver(5, k, A1);
        int result2 = runCookieSolver(5, k, A2);
        int result3 = runCookieSolver(5, k, A3);

        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    // ========== Progressive Operations ==========

    @Test
    public void testSequentialMixing() {
        // [1, 2, 3, 4] with k = 15
        long[] A = {1L, 2L, 3L, 4L};
        int result = runCookieSolver(4, 15L, A);
        // mix 1,2 -> 5: [3, 4, 5]
        // mix 3,4 -> 11: [5, 11]
        // mix 5,11 -> 27: [27]
        // 27 >= 15
        assertEquals(3, result);
    }

    @Test
    public void testLongSequenceOfOperations() {
        long[] A = {1L, 1L, 1L, 1L, 1L, 1L};
        int result = runCookieSolver(6, 30L, A);
        // This will require many operations
        assertFalse(result > 0);
    }

    // ========== Mix Function Verification ==========

    @Test
    public void testMixFormulaCorrectness() {
        // Verify the mix formula: a + 2*b
        long[] A = {3L, 5L};
        int result = runCookieSolver(2, 13L, A);
        // Expected: 3 + 2*5 = 13
        assertEquals(1, result);
    }

    @Test
    public void testMixWithSmallestValues() {
        // Ensure smallest values are always picked
        long[] A = {10L, 1L, 5L, 2L};
        int result = runCookieSolver(4, 6L, A);
        // Should mix 1 and 2 first: 1 + 2*2 = 5
        // Then [5, 5, 10], should mix 5 and 5: 5 + 2*5 = 15
        // [10, 15] all >= 6
        assertEquals(2, result);
    }

    // ========== Stress Tests ==========

    @Test
    public void testManySmallCookies() {
        long[] A = new long[100];
        for (int i = 0; i < 100; i++) {
            A[i] = 1L;
        }
        int result = runCookieSolver(100, 1000L, A);
        // Should eventually build up or return -1
        assertTrue(result != 0); // Either positive or -1
    }

    @Test
    public void testManyLargeCookies() {
        long[] A = new long[100];
        for (int i = 0; i < 100; i++) {
            A[i] = 1000L;
        }
        int result = runCookieSolver(100, 500L, A);
        assertEquals(0, result); // All already satisfy
    }

    @Test
    public void testMixedSmallAndLarge() {
        long[] A = {1L, 2L, 3L, 100L, 200L, 300L};
        int result = runCookieSolver(6, 50L, A);
        // Small ones will be mixed until all >= 50
        assertTrue(result > 0);
    }

    // ========== Realistic Scenarios ==========

    @Test
    public void testTypicalUseCase() {
        long[] A = {7L, 4L, 9L, 2L, 5L};
        int result = runCookieSolver(5, 8L, A);
        // Should require some operations
        assertTrue(result >= 0);
    }

    @Test
    public void testAnotherRealisticCase() {
        long[] A = {13L, 47L, 74L, 12L, 89L, 5L};
        int result = runCookieSolver(6, 50L, A);
        // Several values below 50, should mix them
        assertTrue(result > 0);
    }

    @Test
    public void testGradualIncrease() {
        long[] A = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
        int result = runCookieSolver(10, 20L, A);
        // Many values below 20, will need mixing
        assertTrue(result > 0);
    }
}