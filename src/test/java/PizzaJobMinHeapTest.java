import com.student_work.PizzaJob;
import com.student_work.PizzaJobMinHeap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PizzaJobMinHeapTest {

    // Helper: create a new empty heap
    private PizzaJobMinHeap newHeap() {
        return new PizzaJobMinHeap();
    }

    // Helper: create heap with sample values
    private PizzaJobMinHeap sampleHeap() {
        PizzaJobMinHeap heap = new PizzaJobMinHeap();
        heap.insert(new PizzaJob(1, 5));
        heap.insert(new PizzaJob(2, 3));
        heap.insert(new PizzaJob(3, 7));
        heap.insert(new PizzaJob(4, 1));
        return heap;
    }

    // ========== Constructor Tests ==========

    @Test
    void defaultConstructor_createsEmptyHeap() {
        PizzaJobMinHeap heap = new PizzaJobMinHeap();
        assertEquals(0, heap.size(), "New heap should have size 0.");
        assertTrue(heap.isEmpty(), "New heap should be empty.");
    }

    @Test
    void constructorWithCapacity_createsEmptyHeap() {
        PizzaJobMinHeap heap = new PizzaJobMinHeap(10);
        assertEquals(0, heap.size(), "New heap should have size 0.");
        assertTrue(heap.isEmpty(), "New heap should be empty.");
    }

    @Test
    void constructorWithZeroCapacity_stillWorks() {
        PizzaJobMinHeap heap = new PizzaJobMinHeap(0);
        assertEquals(0, heap.size(), "Heap should have size 0.");
        assertTrue(heap.isEmpty(), "Heap should be empty.");

        assertDoesNotThrow(() -> heap.insert(new PizzaJob(1, 5)),
                "Should allow insertions even with zero initial capacity.");
        assertEquals(1, heap.size(), "After insert, size should be 1.");
    }

    // ========== Basic Operation Tests ==========

    @Test
    void insertSingleElement_updatesStateCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        PizzaJob job = new PizzaJob(100, 42);
        heap.insert(job);

        assertEquals(1, heap.size(), "Size should be 1 after single insert.");
        assertFalse(heap.isEmpty(), "Heap should not be empty after insert.");
        assertEquals(42, heap.min().getCookTime(), "Min should return the inserted element.");
    }

    @Test
    void insertMultipleElements_tracksMinCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 5));
        heap.insert(new PizzaJob(2, 3));
        heap.insert(new PizzaJob(3, 7));

        assertEquals(3, heap.size(), "Size should be 3 after three inserts.");
        assertEquals(3, heap.min().getCookTime(), "Min should be the job with smallest cook time.");
    }

    @Test
    void minOnEmptyHeap_throwsIllegalStateException() {
        PizzaJobMinHeap heap = newHeap();
        assertThrows(IllegalStateException.class, () -> heap.min(),
                "Calling min() on empty heap should throw IllegalStateException.");
    }

    @Test
    void delMinOnEmptyHeap_throwsIllegalStateException() {
        PizzaJobMinHeap heap = newHeap();
        assertThrows(IllegalStateException.class, () -> heap.delMin(),
                "Calling delMin() on empty heap should throw IllegalStateException.");
    }

    @Test
    void delMinSingleElement_returnsElementAndBecomesEmpty() {
        PizzaJobMinHeap heap = newHeap();
        PizzaJob job = new PizzaJob(100, 42);
        heap.insert(job);

        PizzaJob min = heap.delMin();
        assertEquals(42, min.getCookTime(), "delMin should return the only element.");
        assertEquals(0, heap.size(), "Size should be 0 after removing only element.");
        assertTrue(heap.isEmpty(), "Heap should be empty after removing only element.");
    }

    @Test
    void delMinMultipleElements_removesInAscendingCookTimeOrder() {
        PizzaJobMinHeap heap = sampleHeap();

        assertEquals(1, heap.delMin().getCookTime(), "First min should have cook time 1.");
        assertEquals(3, heap.size(), "Size should be 3 after first delMin.");

        assertEquals(3, heap.delMin().getCookTime(), "Second min should have cook time 3.");
        assertEquals(2, heap.size(), "Size should be 2 after second delMin.");

        assertEquals(5, heap.delMin().getCookTime(), "Third min should have cook time 5.");
        assertEquals(1, heap.size(), "Size should be 1 after third delMin.");

        assertEquals(7, heap.delMin().getCookTime(), "Fourth min should have cook time 7.");
        assertEquals(0, heap.size(), "Size should be 0 after fourth delMin.");
        assertTrue(heap.isEmpty(), "Heap should be empty after all delMins.");
    }

    // ========== Heap Property Tests ==========

    @Test
    void heapProperty_maintainedThroughMultipleOperations() {
        PizzaJobMinHeap heap = newHeap();
        long[] cookTimes = {15, 10, 20, 8, 25, 30, 5};

        for (int i = 0; i < cookTimes.length; i++) {
            heap.insert(new PizzaJob(i, cookTimes[i]));
        }

        long prev = heap.delMin().getCookTime();
        while (!heap.isEmpty()) {
            long current = heap.delMin().getCookTime();
            assertTrue(prev <= current,
                    "Heap property violated: " + prev + " should be <= " + current);
            prev = current;
        }
    }

    @Test
    void minAlwaysReturnsSmallestCookTime_throughIncrementalInserts() {
        PizzaJobMinHeap heap = newHeap();

        heap.insert(new PizzaJob(1, 100));
        assertEquals(100, heap.min().getCookTime(), "Min should have cook time 100.");

        heap.insert(new PizzaJob(2, 50));
        assertEquals(50, heap.min().getCookTime(), "Min should have cook time 50 after inserting smaller value.");

        heap.insert(new PizzaJob(3, 75));
        assertEquals(50, heap.min().getCookTime(), "Min should still have cook time 50.");

        heap.insert(new PizzaJob(4, 25));
        assertEquals(25, heap.min().getCookTime(), "Min should have cook time 25 after inserting new smallest.");

        heap.insert(new PizzaJob(5, 30));
        assertEquals(25, heap.min().getCookTime(), "Min should still have cook time 25.");
    }

    // ========== Order Tests ==========

    @Test
    void elementsRemovedInSortedCookTimeOrder_withMixedInsertionPattern() {
        PizzaJobMinHeap heap = newHeap();
        long[] cookTimes = {50, 30, 70, 10, 40, 60, 80, 20};

        for (int i = 0; i < cookTimes.length; i++) {
            heap.insert(new PizzaJob(i, cookTimes[i]));
        }

        long[] expected = {10, 20, 30, 40, 50, 60, 70, 80};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], heap.delMin().getCookTime(),
                    "Job at position " + i + " should have cook time " + expected[i]);
        }
    }

    @Test
    void reverseOrderInsertion_stillProducesSortedOutput() {
        PizzaJobMinHeap heap = newHeap();

        for (long i = 100; i > 0; i--) {
            heap.insert(new PizzaJob(i, i));
        }

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin().getCookTime(),
                    "Job should have cook time " + i + " in sorted order.");
        }
    }

    @Test
    void ascendingOrderInsertion_stillProducesSortedOutput() {
        PizzaJobMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(new PizzaJob(i, i));
        }

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin().getCookTime(),
                    "Job should have cook time " + i + " in sorted order.");
        }
    }

    // ========== Duplicate Cook Time Tests ==========

    @Test
    void duplicateCookTimes_allRetainedAndReturned() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 5));
        heap.insert(new PizzaJob(2, 5));
        heap.insert(new PizzaJob(3, 5));

        assertEquals(3, heap.size(), "Size should be 3 with duplicates.");
        assertEquals(5, heap.delMin().getCookTime(), "First duplicate should have cook time 5.");
        assertEquals(5, heap.delMin().getCookTime(), "Second duplicate should have cook time 5.");
        assertEquals(5, heap.delMin().getCookTime(), "Third duplicate should have cook time 5.");
        assertTrue(heap.isEmpty(), "Heap should be empty after removing all duplicates.");
    }

    @Test
    void mixedDuplicateCookTimes_returnedInSortedOrder() {
        PizzaJobMinHeap heap = newHeap();
        long[] cookTimes = {3, 1, 4, 1, 5, 9, 2, 6, 5};

        for (int i = 0; i < cookTimes.length; i++) {
            heap.insert(new PizzaJob(i, cookTimes[i]));
        }

        long[] expected = {1, 1, 2, 3, 4, 5, 5, 6, 9};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], heap.delMin().getCookTime(),
                    "Job at position " + i + " should have cook time " + expected[i]);
        }
    }

    // ========== Clear Tests ==========

    @Test
    void clearOnEmptyHeap_remainsEmpty() {
        PizzaJobMinHeap heap = newHeap();
        heap.clear();

        assertEquals(0, heap.size(), "Cleared empty heap should have size 0.");
        assertTrue(heap.isEmpty(), "Cleared empty heap should be empty.");
    }

    @Test
    void clearOnNonEmptyHeap_resetsState() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 1));
        heap.insert(new PizzaJob(2, 2));
        heap.insert(new PizzaJob(3, 3));

        heap.clear();
        assertEquals(0, heap.size(), "Size should be 0 after clear.");
        assertTrue(heap.isEmpty(), "Heap should be empty after clear.");

        assertThrows(IllegalStateException.class, () -> heap.min(),
                "min() should throw after clear.");
    }

    @Test
    void insertAfterClear_worksCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 1));
        heap.insert(new PizzaJob(2, 2));
        heap.clear();

        heap.insert(new PizzaJob(5, 5));
        heap.insert(new PizzaJob(3, 3));

        assertEquals(2, heap.size(), "Size should be 2 after re-inserting.");
        assertEquals(3, heap.min().getCookTime(), "Min should have the smallest cook time.");
    }

    // ========== Size and Empty Tests ==========

    @Test
    void sizeTracking_accurateThroughOperations() {
        PizzaJobMinHeap heap = newHeap();
        assertEquals(0, heap.size(), "Initial size should be 0.");

        heap.insert(new PizzaJob(1, 1));
        assertEquals(1, heap.size(), "Size should be 1 after insert.");

        heap.insert(new PizzaJob(2, 2));
        assertEquals(2, heap.size(), "Size should be 2 after second insert.");

        heap.delMin();
        assertEquals(1, heap.size(), "Size should be 1 after delMin.");

        heap.delMin();
        assertEquals(0, heap.size(), "Size should be 0 after removing all.");
    }

    @Test
    void isEmpty_reflectsHeapState() {
        PizzaJobMinHeap heap = newHeap();
        assertTrue(heap.isEmpty(), "New heap should be empty.");

        heap.insert(new PizzaJob(1, 1));
        assertFalse(heap.isEmpty(), "Heap with element should not be empty.");

        heap.delMin();
        assertTrue(heap.isEmpty(), "Heap should be empty after removing only element.");
    }

    // ========== Resize Tests ==========

    @Test
    void resizeGrowth_handlesLargeInsertions() {
        PizzaJobMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(new PizzaJob(i, i));
        }

        assertEquals(100, heap.size(), "Size should be 100 after 100 inserts.");

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin().getCookTime(),
                    "Job should have cook time " + i + " after resize operations.");
        }
    }

    @Test
    void resizeShrink_handlesLargeDeletions() {
        PizzaJobMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(new PizzaJob(i, i));
        }

        for (int i = 1; i <= 95; i++) {
            heap.delMin();
        }

        assertEquals(5, heap.size(), "Size should be 5 after removing 95 elements.");

        for (long i = 96; i <= 100; i++) {
            assertEquals(i, heap.delMin().getCookTime(),
                    "Remaining jobs should have correct cook times after shrink.");
        }
    }

    // ========== Edge Case Tests ==========

    @Test
    void minDoesNotRemoveElement_canBeCalledRepeatedly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 5));

        assertEquals(5, heap.min().getCookTime(), "First min() call should return cook time 5.");
        assertEquals(1, heap.size(), "Size should still be 1 after min().");
        assertEquals(5, heap.min().getCookTime(), "Second min() call should return cook time 5.");
        assertEquals(1, heap.size(), "Size should still be 1 after second min().");
    }

    @Test
    void largeCookTimes_handledCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, Long.MAX_VALUE));
        heap.insert(new PizzaJob(2, Long.MAX_VALUE - 1));
        heap.insert(new PizzaJob(3, Long.MAX_VALUE - 2));

        assertEquals(Long.MAX_VALUE - 2, heap.delMin().getCookTime(),
                "Smallest large cook time should be Long.MAX_VALUE - 2.");
        assertEquals(Long.MAX_VALUE - 1, heap.delMin().getCookTime(),
                "Next should be Long.MAX_VALUE - 1.");
        assertEquals(Long.MAX_VALUE, heap.delMin().getCookTime(),
                "Largest should be Long.MAX_VALUE.");
    }

    @Test
    void smallCookTimes_handledCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 0));
        heap.insert(new PizzaJob(2, 1));
        heap.insert(new PizzaJob(3, 2));

        assertEquals(0, heap.delMin().getCookTime(),
                "Smallest should be 0.");
        assertEquals(1, heap.delMin().getCookTime(),
                "Next should be 1.");
        assertEquals(2, heap.delMin().getCookTime(),
                "Largest should be 2.");
    }

    @Test
    void zeroCookTime_handledCorrectly() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 0));
        heap.insert(new PizzaJob(2, 1));
        heap.insert(new PizzaJob(3, 2));

        assertEquals(0, heap.delMin().getCookTime(), "First should be 0.");
        assertEquals(1, heap.delMin().getCookTime(), "Second should be 1.");
        assertEquals(2, heap.delMin().getCookTime(), "Third should be 2.");
    }

    // ========== Arrival Time Independence Tests ==========

    @Test
    void orderingIgnoresArrivalTime_onlyUsesCookTime() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(100, 5));  // Late arrival, short cook
        heap.insert(new PizzaJob(1, 10));   // Early arrival, long cook
        heap.insert(new PizzaJob(50, 3));   // Mid arrival, shortest cook

        assertEquals(3, heap.delMin().getCookTime(),
                "Should prioritize shortest cook time regardless of arrival.");
        assertEquals(5, heap.delMin().getCookTime(),
                "Should prioritize second shortest cook time.");
        assertEquals(10, heap.delMin().getCookTime(),
                "Should prioritize longest cook time last.");
    }

    @Test
    void sameCookTimeDifferentArrivalTimes_bothRetained() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(100, 5));
        heap.insert(new PizzaJob(200, 5));
        heap.insert(new PizzaJob(300, 5));

        assertEquals(3, heap.size(), "All jobs with same cook time should be retained.");

        for (int i = 0; i < 3; i++) {
            assertEquals(5, heap.delMin().getCookTime(),
                    "All jobs should have cook time 5.");
        }
    }

    // ========== Stress Tests ==========

    @Test
    void largeNumberOfJobs_maintainsCorrectOrder() {
        PizzaJobMinHeap heap = newHeap();
        int n = 10000;

        for (long i = n; i > 0; i--) {
            heap.insert(new PizzaJob(i, i));
        }

        assertEquals(n, heap.size(), "Size should be " + n + " after " + n + " inserts.");

        for (long i = 1; i <= n; i++) {
            assertEquals(i, heap.delMin().getCookTime(),
                    "Job should have cook time " + i + " in sorted order.");
        }

        assertTrue(heap.isEmpty(), "Heap should be empty after removing all jobs.");
    }

    @Test
    void randomInsertions_producesSortedOutput() {
        PizzaJobMinHeap heap = newHeap();
        Random rand = new Random(12345); // Fixed seed for reproducibility
        ArrayList<PizzaJob> jobs = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            long cookTime = Math.abs(rand.nextLong() % 10000);
            PizzaJob job = new PizzaJob(i, cookTime);
            jobs.add(job);
            heap.insert(job);
        }

        Collections.sort(jobs, Comparator.comparingLong(PizzaJob::getCookTime));

        for (int i = 0; i < jobs.size(); i++) {
            assertEquals(jobs.get(i).getCookTime(), heap.delMin().getCookTime(),
                    "Job at position " + i + " should match sorted cook time order.");
        }
    }

    @Test
    void alternatingInsertDelete_maintainsCorrectState() {
        PizzaJobMinHeap heap = newHeap();

        heap.insert(new PizzaJob(1, 5));
        heap.insert(new PizzaJob(2, 3));
        assertEquals(3, heap.delMin().getCookTime(), "First delMin should return cook time 3.");

        heap.insert(new PizzaJob(3, 7));
        heap.insert(new PizzaJob(4, 1));
        assertEquals(1, heap.delMin().getCookTime(), "Second delMin should return cook time 1.");

        heap.insert(new PizzaJob(5, 9));
        assertEquals(5, heap.delMin().getCookTime(), "Third delMin should return cook time 5.");
        assertEquals(7, heap.delMin().getCookTime(), "Fourth delMin should return cook time 7.");
        assertEquals(9, heap.delMin().getCookTime(), "Fifth delMin should return cook time 9.");

        assertTrue(heap.isEmpty(), "Heap should be empty at end.");
    }

    @Test
    void repeatedClearAndInsert_maintainsCorrectness() {
        PizzaJobMinHeap heap = newHeap();

        for (int round = 0; round < 5; round++) {
            for (long i = 1; i <= 10; i++) {
                heap.insert(new PizzaJob(i, i));
            }
            assertEquals(10, heap.size(), "Size should be 10 in round " + round);
            heap.clear();
            assertEquals(0, heap.size(), "Size should be 0 after clear in round " + round);
        }
    }

    // ========== Complex Scenario Tests ==========

    @Test
    void buildHeapFromScratch_maintainsHeapProperty() {
        PizzaJobMinHeap heap = newHeap();
        long[] cookTimes = {23, 17, 14, 6, 13, 10, 1, 5, 7, 12};

        for (int i = 0; i < cookTimes.length; i++) {
            heap.insert(new PizzaJob(i, cookTimes[i]));
        }

        ArrayList<Long> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.delMin().getCookTime());
        }

        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1) <= result.get(i),
                    "Result must be non-decreasing: " + result.get(i - 1) + " <= " + result.get(i));
        }
    }

    @Test
    void multipleMinPeeks_doNotAffectHeapState() {
        PizzaJobMinHeap heap = newHeap();
        heap.insert(new PizzaJob(1, 10));
        heap.insert(new PizzaJob(2, 5));
        heap.insert(new PizzaJob(3, 15));

        for (int i = 0; i < 10; i++) {
            assertEquals(5, heap.min().getCookTime(), "Min should always have cook time 5 on peek " + i);
        }

        assertEquals(3, heap.size(), "Size should still be 3 after 10 peeks.");
        assertEquals(5, heap.delMin().getCookTime(), "delMin should return cook time 5.");
        assertEquals(2, heap.size(), "Size should be 2 after delMin.");
    }

    @Test
    void emptyAfterMultipleOperations_behavesCorrectly() {
        PizzaJobMinHeap heap = newHeap();

        for (int i = 0; i < 3; i++) {
            heap.insert(new PizzaJob(i, i));
            heap.delMin();
        }

        assertTrue(heap.isEmpty(), "Heap should be empty after balanced insert/delMin operations.");
    }
}