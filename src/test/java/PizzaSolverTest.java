import com.student_work.PizzaSolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class PizzaSolverTest {

    @BeforeEach
    void setUp() {
        // Ensure a clean stdin for every test
        flushStdIn();
    }

    /**
     * Helper method to run PizzaSolution.main() with simulated stdin and capture stdout
     * @param orders array of [arrivalTime, cookTime] pairs
     * @return the output from the program (minimum average wait time)
     */
    private long runPizzaSolution(long[][] orders) {
        StringBuilder input = new StringBuilder();
        input.append(orders.length).append("\n");
        for (long[] order : orders) {
            input.append(order[0]).append(" ").append(order[1]).append("\n");
        }

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayInputStream testIn = null;
        ByteArrayOutputStream testOut = null;

        try {
            // Start from a clean stdin state
            flushStdIn();

            // Provide this test's input
            testIn = new ByteArrayInputStream(input.toString().getBytes());
            System.setIn(testIn);

            // Force StdIn to rebind to the new System.in
            bindStdIn();

            // Capture stdout; silence stderr
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut, true));
            System.setErr(new PrintStream(new ByteArrayOutputStream(), true));

            // Run program
            PizzaSolver.main(new String[]{});

            System.out.flush();
            System.err.flush();

            // Parse single integer answer
            String output = testOut.toString().trim();
            return Long.parseLong(output);
        } finally {
            // Close streams we created
            if (testIn != null) try { testIn.close(); } catch (IOException ignored) {}
            if (testOut != null) try { testOut.close(); } catch (IOException ignored) {}

            // Restore originals
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);

            // Leave a clean, empty stdin for the next test
            flushStdIn();
        }
    }

    private void flushStdIn() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        bindStdIn();
    }

    /** Force edu.princeton.cs.algs4.StdIn to read from the current System.in */
    private void bindStdIn() {
        try {
            Class<?> stdInClass = Class.forName("edu.princeton.cs.algs4.StdIn");
            java.lang.reflect.Field scannerField = stdInClass.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(null, new java.util.Scanner(System.in));
        } catch (Exception e) {
            System.err.println("Warning: Could not bind StdIn scanner: " + e.getMessage());
        }
    }

    // ========== Sample Test Cases from Problem ==========

    @Test
    public void testSampleInput00() {
        // Sample Input #00: Expected output = 9
        long[][] orders = {{0, 3}, {1, 9}, {2, 6}};
        long result = runPizzaSolution(orders);
        assertEquals(9, result);
    }

    @Test
    public void testSampleInput01() {
        // Sample Input #01: Expected output = 8
        long[][] orders = {{0, 3}, {1, 9}, {2, 5}};
        long result = runPizzaSolution(orders);
        assertEquals(8, result);
    }

    // ========== Single Customer Cases ==========

    @Test
    public void testSingleCustomer() {
        // One customer, wait time equals cook time
        long[][] orders = {{0, 5}};
        long result = runPizzaSolution(orders);
        assertEquals(5, result);
    }

    @Test
    public void testSingleCustomerLateArrival() {
        // Customer arrives at t=10, cook time = 5
        // Wait time = (10 + 5) - 10 = 5
        long[][] orders = {{10, 5}};
        long result = runPizzaSolution(orders);
        assertEquals(5, result);
    }

    @Test
    public void testSingleCustomerLargeCookTime() {
        long[][] orders = {{0, 1000000}};
        long result = runPizzaSolution(orders);
        assertEquals(1000000, result);
    }

    // ========== All Arrive at Same Time ==========

    @Test
    public void testAllArriveSameTime() {
        // All arrive at t=0, should cook shortest first
        // Cook order: 2, 3, 5
        // Wait times: 2, 5, 10
        // Average: (2+5+10)/3 = 17/3 = 5
        long[][] orders = {{0, 5}, {0, 3}, {0, 2}};
        long result = runPizzaSolution(orders);
        assertEquals(5, result);
    }

    @Test
    public void testAllArriveSameTimeWithDuplicates() {
        // All arrive at t=0, cook times = [4, 4, 4]
        // Wait times: 4, 8, 12
        // Average: 24/3 = 8
        long[][] orders = {{0, 4}, {0, 4}, {0, 4}};
        long result = runPizzaSolution(orders);
        assertEquals(8, result);
    }

    @Test
    public void testAllArriveLaterSameTime() {
        // All arrive at t=100
        // Cook order: 1, 2, 3
        // Wait times: 1, 3, 6
        // Average: 10/3 = 3
        long[][] orders = {{100, 3}, {100, 2}, {100, 1}};
        long result = runPizzaSolution(orders);
        assertEquals(3, result);
    }

    // ========== Two Customer Cases ==========

    @Test
    public void testTwoCustomersSameArrival() {
        // Both arrive at t=0
        // Cook shorter first: 3, then 5
        // Wait times: 3, 8
        // Average: 11/2 = 5
        long[][] orders = {{0, 5}, {0, 3}};
        long result = runPizzaSolution(orders);
        assertEquals(5, result);
    }

    @Test
    public void testTwoCustomersSequential() {
        // First arrives at t=0, second at t=5
        // Cook first immediately (3 units), finish at t=3
        // Second already arrived, cook it (3 units), finish at t=8
        // Wait times: 3, 8-5=3
        // Average: 6/2 = 3
        long[][] orders = {{0, 3}, {5, 3}};
        long result = runPizzaSolution(orders);
        assertEquals(3, result);
    }

    @Test
    public void testTwoCustomersSecondShorter() {
        // First: t=0, cook=10
        // Second: t=1, cook=2
        // Greedy: cook second first (but first is being cooked)
        // Actually: cook first (0-10), then second (10-12)
        // Wait times: 10, 12-1=11
        // Average: 21/2 = 10
        long[][] orders = {{0, 10}, {1, 2}};
        long result = runPizzaSolution(orders);
        assertEquals(10, result);
    }

    // ========== Sequential Arrivals ==========

    @Test
    public void testSequentialArrivalsIncreasingCookTime() {
        // Arrive at 0,1,2 with cook times 1,2,3
        // t=0-1: cook first (wait=1)
        // t=1-3: cook second (wait=3-1=2)
        // t=3-6: cook third (wait=6-2=4)
        // Average: (1+2+4)/3 = 7/3 = 2
        long[][] orders = {{0, 1}, {1, 2}, {2, 3}};
        long result = runPizzaSolution(orders);
        assertEquals(2, result);
    }

    @Test
    public void testSequentialArrivalsDecreasingCookTime() {
        // Arrive at 0,1,2 with cook times 3,2,1
        // t=0-3: cook first (wait=3)
        // At t=3, both second and third have arrived
        // Cook shorter one: third (1 unit)
        // t=3-4: cook third (wait=4-2=2)
        // t=4-6: cook second (wait=6-1=5)
        // Average: (3+5+2)/3 = 10/3 = 3
        long[][] orders = {{0, 3}, {1, 2}, {2, 1}};
        long result = runPizzaSolution(orders);
        assertEquals(3, result);
    }

    // ========== Idle Time Cases ==========

    @Test
    public void testIdleTimeBetweenCustomers() {
        // First: t=0, cook=2
        // Second: t=10, cook=3
        // t=0-2: cook first (wait=2)
        // t=2-10: idle
        // t=10-13: cook second (wait=13-10=3)
        // Average: 5/2 = 2
        long[][] orders = {{0, 2}, {10, 3}};
        long result = runPizzaSolution(orders);
        assertEquals(2, result);
    }

    @Test
    public void testLargeGapsBetweenArrivals() {
        // Arrivals: 0, 1000, 2000
        long[][] orders = {{0, 5}, {1000, 10}, {2000, 3}};
        // t=0-5: first (wait=5)
        // t=1000-1010: second (wait=10)
        // t=2000-2003: third (wait=3)
        // Average: 18/3 = 6
        long result = runPizzaSolution(orders);
        assertEquals(6, result);
    }

    @Test
    public void testIdleWithMultipleWaiting() {
        // First arrives early, finishes, then idle
        // Then multiple arrive while idle
        long[][] orders = {{0, 1}, {100, 3}, {100, 2}, {100, 4}};
        // t=0-1: first (wait=1)
        // t=100-102: shortest of waiting (wait=2)
        // t=102-105: next shortest (wait=105-100=5)
        // t=105-109: last (wait=109-100=9)
        // Average: (1+2+5+9)/4 = 17/4 = 4
        long result = runPizzaSolution(orders);
        assertEquals(4, result);
    }

    // ========== Optimal Order Tests ==========

    @Test
    public void testGreedyOptimal() {
        // Test that greedy shortest-first is optimal
        // Arrivals all at t=0, cook times: 1,2,3,4,5
        // Optimal order: 1,2,3,4,5
        // Wait times: 1, 3, 6, 10, 15
        // Average: 35/5 = 7
        long[][] orders = {{0, 5}, {0, 1}, {0, 3}, {0, 2}, {0, 4}};
        long result = runPizzaSolution(orders);
        assertEquals(7, result);
    }

    @Test
    public void testReverseInputOrder() {
        // Input in reverse order, should still be optimal
        long[][] orders = {{0, 5}, {0, 4}, {0, 3}, {0, 2}, {0, 1}};
        long result = runPizzaSolution(orders);
        assertEquals(7, result);
    }

    // ========== Edge Cases ==========

    @Test
    public void testMinimumCookTime() {
        // Cook time = 1 (minimum per constraints)
        long[][] orders = {{0, 1}, {1, 1}, {2, 1}};
        // Wait times: 1, 1, 1
        // Average: 3/3 = 1
        long result = runPizzaSolution(orders);
        assertEquals(1, result);
    }

    @Test
    public void testAllIdenticalOrders() {
        // All orders identical: arrival=0, cook=5
        long[][] orders = {{0, 5}, {0, 5}, {0, 5}};
        // Wait times: 5, 10, 15
        // Average: 30/3 = 10
        long result = runPizzaSolution(orders);
        assertEquals(10, result);
    }

    @Test
    public void testVeryLongSingleJob() {
        // One very long job among short jobs
        long[][] orders = {{0, 1}, {0, 1000}, {0, 1}, {0, 1}};
        // Cook order: 1,1,1,1000
        // Wait times: 1, 2, 3, 1003
        // Average: 1009/4 = 252
        long result = runPizzaSolution(orders);
        assertEquals(252, result);
    }

    // ========== Large Values ==========

    @Test
    public void testMaxConstraintValues() {
        // Maximum values from constraints
        // T can be up to 10^9, L can be up to 10^9
        long[][] orders = {{1000000000L, 1000000000L}};
        long result = runPizzaSolution(orders);
        assertEquals(1000000000L, result);
    }

    @Test
    public void testLargeArrivalTimes() {
        long[][] orders = {
                {1000000000L, 5},
                {1000000001L, 3},
                {1000000002L, 7}
        };
        // All arrive around 10^9
        // Cook order at that time: 3, 5, 7
        // Wait times: 3, 8, 15
        // Average: 26/3 = 8
        long result = runPizzaSolution(orders);
        assertEquals(8, result);
    }

    @Test
    public void testLargeCookTimes() {
        long[][] orders = {
                {0, 1000000},
                {1, 500000},
                {2, 750000}
        };
        // Cook order: starts with first (already cooking)
        // Then shortest available: second, then third
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    // ========== Multiple Customers Complex Scenarios ==========

    @Test
    public void testFiveCustomersMixed() {
        // Mix of arrival times and cook times
        long[][] orders = {
                {0, 5},
                {2, 2},
                {3, 8},
                {5, 1},
                {7, 3}
        };
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    @Test
    public void testTenCustomersIncreasing() {
        long[][] orders = new long[10][2];
        for (int i = 0; i < 10; i++) {
            orders[i][0] = i;
            orders[i][1] = i + 1;
        }
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    @Test
    public void testTenCustomersDecreasing() {
        long[][] orders = new long[10][2];
        for (int i = 0; i < 10; i++) {
            orders[i][0] = i;
            orders[i][1] = 10 - i;
        }
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    // ========== Stress Tests ==========

    @Test
    public void testManyCustomersSameTime() {
        // 50 customers all arrive at t=0
        long[][] orders = new long[50][2];
        for (int i = 0; i < 50; i++) {
            orders[i][0] = 0;
            orders[i][1] = (i % 10) + 1; // Cook times 1-10
        }
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    @Test
    public void testManyCustomersSequential() {
        // 100 customers arrive sequentially
        long[][] orders = new long[100][2];
        for (int i = 0; i < 100; i++) {
            orders[i][0] = i;
            orders[i][1] = (i % 5) + 1; // Cook times 1-5
        }
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    @Test
    public void testManyCustomersRandom() {
        // 100 customers with varying arrival and cook times
        long[][] orders = new long[100][2];
        for (int i = 0; i < 100; i++) {
            orders[i][0] = i * 10; // Arrive every 10 time units
            orders[i][1] = ((i * 7) % 20) + 1; // Cook times 1-20
        }
        long result = runPizzaSolution(orders);
        assertTrue(result > 0);
    }

    // ========== Specific Algorithm Verification ==========

    @Test
    public void testGreedyChoiceCorrectness() {
        // Verify greedy always picks shortest available
        // Arrivals: 0,0,0 Cook: 5,3,1
        // Should cook: 1,3,5
        // Wait: 1, 4, 9
        // Average: 14/3 = 4
        long[][] orders = {{0, 5}, {0, 3}, {0, 1}};
        long result = runPizzaSolution(orders);
        assertEquals(4, result);
    }

    @Test
    public void testWaitForArrival() {
        // Cook finishes before next arrival
        // Should wait for next customer
        long[][] orders = {{0, 1}, {5, 1}};
        // t=0-1: first (wait=1)
        // t=1-5: idle
        // t=5-6: second (wait=1)
        // Average: 2/2 = 1
        long result = runPizzaSolution(orders);
        assertEquals(1, result);
    }

    @Test
    public void testMultipleWaitingChooseShortest() {
        // Multiple customers waiting, must choose shortest
        long[][] orders = {{0, 10}, {1, 3}, {2, 5}, {3, 2}};
        // t=0-10: first (wait=10)
        // At t=10, customers 2,3,4 are waiting with cook times 3,5,2
        // Choose shortest: customer 4 (cook=2)
        // t=10-12: customer 4 (wait=12-3=9)
        // t=12-15: customer 2 (wait=15-1=14)
        // t=15-20: customer 3 (wait=20-2=18)
        // Average: (10+14+9+18)/4 = 51/4 = 12
        long result = runPizzaSolution(orders);
        assertEquals(12, result);
    }

    // ========== Boundary Arithmetic Tests ==========

    @Test
    public void testIntegerDivisionTruncation() {
        // Test that we correctly truncate (not round)
        // Total wait time = 10, customers = 3
        // Average = 10/3 = 3.333... -> should be 3
        long[][] orders = {{0, 2}, {0, 3}, {0, 5}};
        // Wait: 2, 5, 10
        // Average: 17/3 = 5.666... -> 5
        long result = runPizzaSolution(orders);
        assertEquals(5, result);
    }

    @Test
    public void testExactIntegerAverage() {
        // Average should be exact integer
        long[][] orders = {{0, 2}, {0, 4}};
        // Wait: 2, 6
        // Average: 8/2 = 4
        long result = runPizzaSolution(orders);
        assertEquals(4, result);
    }

    @Test
    public void testLargeWaitTimeSum() {
        // Test that sum doesn't overflow
        long[][] orders = {
                {0, 100000},
                {0, 100000},
                {0, 100000}
        };
        // Wait: 100000, 200000, 300000
        // Sum: 600000
        // Average: 200000
        long result = runPizzaSolution(orders);
        assertEquals(200000, result);
    }
}