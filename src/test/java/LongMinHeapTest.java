import com.student_work.LongMinHeap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class LongMinHeapTest {

    // Helper: create a new empty heap
    private LongMinHeap newHeap() {
        return new LongMinHeap();
    }

    // Helper: create heap with sample values
    private LongMinHeap sampleHeap() {
        LongMinHeap heap = new LongMinHeap();
        heap.insert(5L);
        heap.insert(3L);
        heap.insert(7L);
        heap.insert(1L);
        return heap;
    }

    // ========== Constructor Tests ==========

    @Test
    void defaultConstructor_createsEmptyHeap() {
        LongMinHeap heap = new LongMinHeap();
        assertEquals(0, heap.size(), "New heap should have size 0.");
        assertTrue(heap.isEmpty(), "New heap should be empty.");
    }

    @Test
    void constructorWithCapacity_createsEmptyHeap() {
        LongMinHeap heap = new LongMinHeap(10);
        assertEquals(0, heap.size(), "New heap should have size 0.");
        assertTrue(heap.isEmpty(), "New heap should be empty.");
    }

    @Test
    void constructorWithZeroCapacity_stillWorks() {
        LongMinHeap heap = new LongMinHeap(0);
        assertEquals(0, heap.size(), "Heap should have size 0.");
        assertTrue(heap.isEmpty(), "Heap should be empty.");

        assertDoesNotThrow(() -> heap.insert(5L),
                "Should allow insertions even with zero initial capacity.");
        assertEquals(1, heap.size(), "After insert, size should be 1.");
    }

    @Test
    void constructorWithNegativeCapacity_stillWorks() {
        LongMinHeap heap = new LongMinHeap(-5);
        assertDoesNotThrow(() -> heap.insert(10L),
                "Should allow insertions even with negative initial capacity.");
        assertEquals(1, heap.size(), "After insert, size should be 1.");
    }

    // ========== Basic Operation Tests ==========

    @Test
    void insertSingleElement_updatesStateCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(42L);

        assertEquals(1, heap.size(), "Size should be 1 after single insert.");
        assertFalse(heap.isEmpty(), "Heap should not be empty after insert.");
        assertEquals(42L, heap.min(), "Min should return the inserted element.");
    }

    @Test
    void insertMultipleElements_tracksMinCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(5L);
        heap.insert(3L);
        heap.insert(7L);

        assertEquals(3, heap.size(), "Size should be 3 after three inserts.");
        assertEquals(3L, heap.min(), "Min should be the smallest element.");
    }

    @Test
    void insertNull_throwsIllegalArgumentException() {
        LongMinHeap heap = newHeap();
        assertThrows(IllegalArgumentException.class, () -> heap.insert(null),
                "Inserting null should throw IllegalArgumentException.");
    }

    @Test
    void minOnEmptyHeap_throwsNoSuchElementException() {
        LongMinHeap heap = newHeap();
        assertThrows(NoSuchElementException.class, () -> heap.min(),
                "Calling min() on empty heap should throw NoSuchElementException.");
    }

    @Test
    void delMinOnEmptyHeap_throwsNoSuchElementException() {
        LongMinHeap heap = newHeap();
        assertThrows(NoSuchElementException.class, () -> heap.delMin(),
                "Calling delMin() on empty heap should throw NoSuchElementException.");
    }

    @Test
    void delMinSingleElement_returnsElementAndBecomesEmpty() {
        LongMinHeap heap = newHeap();
        heap.insert(42L);

        Long min = heap.delMin();
        assertEquals(42L, min, "delMin should return the only element.");
        assertEquals(0, heap.size(), "Size should be 0 after removing only element.");
        assertTrue(heap.isEmpty(), "Heap should be empty after removing only element.");
    }

    @Test
    void delMinMultipleElements_removesInAscendingOrder() {
        LongMinHeap heap = sampleHeap();

        assertEquals(1L, heap.delMin(), "First min should be 1.");
        assertEquals(3, heap.size(), "Size should be 3 after first delMin.");

        assertEquals(3L, heap.delMin(), "Second min should be 3.");
        assertEquals(2, heap.size(), "Size should be 2 after second delMin.");

        assertEquals(5L, heap.delMin(), "Third min should be 5.");
        assertEquals(1, heap.size(), "Size should be 1 after third delMin.");

        assertEquals(7L, heap.delMin(), "Fourth min should be 7.");
        assertEquals(0, heap.size(), "Size should be 0 after fourth delMin.");
        assertTrue(heap.isEmpty(), "Heap should be empty after all delMins.");
    }

    // ========== Heap Property Tests ==========

    @Test
    void heapProperty_maintainedThroughMultipleOperations() {
        LongMinHeap heap = newHeap();
        long[] values = {15L, 10L, 20L, 8L, 25L, 30L, 5L};

        for (long val : values) {
            heap.insert(val);
        }

        long prev = heap.delMin();
        while (!heap.isEmpty()) {
            long current = heap.delMin();
            assertTrue(prev <= current,
                    "Heap property violated: " + prev + " should be <= " + current);
            prev = current;
        }
    }

    @Test
    void minAlwaysReturnsSmallest_throughIncrementalInserts() {
        LongMinHeap heap = newHeap();

        heap.insert(100L);
        assertEquals(100L, heap.min(), "Min should be 100.");

        heap.insert(50L);
        assertEquals(50L, heap.min(), "Min should be 50 after inserting smaller value.");

        heap.insert(75L);
        assertEquals(50L, heap.min(), "Min should still be 50.");

        heap.insert(25L);
        assertEquals(25L, heap.min(), "Min should be 25 after inserting new smallest.");

        heap.insert(30L);
        assertEquals(25L, heap.min(), "Min should still be 25.");
    }

    // ========== Order Tests ==========

    @Test
    void elementsRemovedInSortedOrder_withMixedInsertionPattern() {
        LongMinHeap heap = newHeap();
        long[] values = {50L, 30L, 70L, 10L, 40L, 60L, 80L, 20L};

        for (long val : values) {
            heap.insert(val);
        }

        long[] expected = {10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], heap.delMin(),
                    "Element at position " + i + " should be " + expected[i]);
        }
    }

    @Test
    void reverseOrderInsertion_stillProducesSortedOutput() {
        LongMinHeap heap = newHeap();

        for (long i = 100; i > 0; i--) {
            heap.insert(i);
        }

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin(),
                    "Element should be " + i + " in sorted order.");
        }
    }

    @Test
    void ascendingOrderInsertion_stillProducesSortedOutput() {
        LongMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(i);
        }

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin(),
                    "Element should be " + i + " in sorted order.");
        }
    }

    // ========== Duplicate Values Tests ==========

    @Test
    void duplicateValues_allRetainedAndReturned() {
        LongMinHeap heap = newHeap();
        heap.insert(5L);
        heap.insert(5L);
        heap.insert(5L);

        assertEquals(3, heap.size(), "Size should be 3 with duplicates.");
        assertEquals(5L, heap.delMin(), "First duplicate should be 5.");
        assertEquals(5L, heap.delMin(), "Second duplicate should be 5.");
        assertEquals(5L, heap.delMin(), "Third duplicate should be 5.");
        assertTrue(heap.isEmpty(), "Heap should be empty after removing all duplicates.");
    }

    @Test
    void mixedDuplicates_returnedInSortedOrder() {
        LongMinHeap heap = newHeap();
        long[] values = {3L, 1L, 4L, 1L, 5L, 9L, 2L, 6L, 5L};

        for (long val : values) {
            heap.insert(val);
        }

        long[] expected = {1L, 1L, 2L, 3L, 4L, 5L, 5L, 6L, 9L};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], heap.delMin(),
                    "Element at position " + i + " should be " + expected[i]);
        }
    }

    // ========== Clear Tests ==========

    @Test
    void clearOnEmptyHeap_remainsEmpty() {
        LongMinHeap heap = newHeap();
        heap.clear();

        assertEquals(0, heap.size(), "Cleared empty heap should have size 0.");
        assertTrue(heap.isEmpty(), "Cleared empty heap should be empty.");
    }

    @Test
    void clearOnNonEmptyHeap_resetsState() {
        LongMinHeap heap = newHeap();
        heap.insert(1L);
        heap.insert(2L);
        heap.insert(3L);

        heap.clear();
        assertEquals(0, heap.size(), "Size should be 0 after clear.");
        assertTrue(heap.isEmpty(), "Heap should be empty after clear.");

        assertThrows(NoSuchElementException.class, () -> heap.min(),
                "min() should throw after clear.");
    }

    @Test
    void insertAfterClear_worksCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(1L);
        heap.insert(2L);
        heap.clear();

        heap.insert(5L);
        heap.insert(3L);

        assertEquals(2, heap.size(), "Size should be 2 after re-inserting.");
        assertEquals(3L, heap.min(), "Min should be the smallest new element.");
    }

    // ========== Size and Empty Tests ==========

    @Test
    void sizeTracking_accurateThroughOperations() {
        LongMinHeap heap = newHeap();
        assertEquals(0, heap.size(), "Initial size should be 0.");

        heap.insert(1L);
        assertEquals(1, heap.size(), "Size should be 1 after insert.");

        heap.insert(2L);
        assertEquals(2, heap.size(), "Size should be 2 after second insert.");

        heap.delMin();
        assertEquals(1, heap.size(), "Size should be 1 after delMin.");

        heap.delMin();
        assertEquals(0, heap.size(), "Size should be 0 after removing all.");
    }

    @Test
    void isEmpty_reflectsHeapState() {
        LongMinHeap heap = newHeap();
        assertTrue(heap.isEmpty(), "New heap should be empty.");

        heap.insert(1L);
        assertFalse(heap.isEmpty(), "Heap with element should not be empty.");

        heap.delMin();
        assertTrue(heap.isEmpty(), "Heap should be empty after removing only element.");
    }

    // ========== Resize Tests ==========

    @Test
    void resizeGrowth_handlesLargeInsertions() {
        LongMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(i);
        }

        assertEquals(100, heap.size(), "Size should be 100 after 100 inserts.");

        for (long i = 1; i <= 100; i++) {
            assertEquals(i, heap.delMin(),
                    "Element should be " + i + " after resize operations.");
        }
    }

    @Test
    void resizeShrink_handlesLargeDeletions() {
        LongMinHeap heap = newHeap();

        for (long i = 1; i <= 100; i++) {
            heap.insert(i);
        }

        for (int i = 1; i <= 95; i++) {
            heap.delMin();
        }

        assertEquals(5, heap.size(), "Size should be 5 after removing 95 elements.");

        for (long i = 96; i <= 100; i++) {
            assertEquals(i, heap.delMin(),
                    "Remaining elements should be correct after shrink.");
        }
    }

    // ========== Edge Case Tests ==========

    @Test
    void minDoesNotRemoveElement_canBeCalledRepeatedly() {
        LongMinHeap heap = newHeap();
        heap.insert(5L);

        assertEquals(5L, heap.min(), "First min() call should return 5.");
        assertEquals(1, heap.size(), "Size should still be 1 after min().");
        assertEquals(5L, heap.min(), "Second min() call should return 5.");
        assertEquals(1, heap.size(), "Size should still be 1 after second min().");
    }

    @Test
    void largeValues_handledCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(Long.MAX_VALUE);
        heap.insert(Long.MAX_VALUE - 1);
        heap.insert(Long.MAX_VALUE - 2);

        assertEquals(Long.MAX_VALUE - 2, heap.delMin(),
                "Smallest large value should be Long.MAX_VALUE - 2.");
        assertEquals(Long.MAX_VALUE - 1, heap.delMin(),
                "Next should be Long.MAX_VALUE - 1.");
        assertEquals(Long.MAX_VALUE, heap.delMin(),
                "Largest should be Long.MAX_VALUE.");
    }

    @Test
    void smallValues_handledCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(Long.MIN_VALUE);
        heap.insert(Long.MIN_VALUE + 1);
        heap.insert(Long.MIN_VALUE + 2);

        assertEquals(Long.MIN_VALUE, heap.delMin(),
                "Smallest should be Long.MIN_VALUE.");
        assertEquals(Long.MIN_VALUE + 1, heap.delMin(),
                "Next should be Long.MIN_VALUE + 1.");
        assertEquals(Long.MIN_VALUE + 2, heap.delMin(),
                "Largest should be Long.MIN_VALUE + 2.");
    }

    @Test
    void negativeValues_sortedCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(-5L);
        heap.insert(-10L);
        heap.insert(-1L);
        heap.insert(-7L);

        assertEquals(-10L, heap.delMin(), "Most negative should be -10.");
        assertEquals(-7L, heap.delMin(), "Next should be -7.");
        assertEquals(-5L, heap.delMin(), "Next should be -5.");
        assertEquals(-1L, heap.delMin(), "Least negative should be -1.");
    }

    @Test
    void mixedPositiveAndNegative_sortedCorrectly() {
        LongMinHeap heap = newHeap();
        long[] values = {5L, -3L, 0L, -10L, 15L, -1L, 7L};

        for (long val : values) {
            heap.insert(val);
        }

        long[] expected = {-10L, -3L, -1L, 0L, 5L, 7L, 15L};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], heap.delMin(),
                    "Element at position " + i + " should be " + expected[i]);
        }
    }

    @Test
    void zeroValue_handledCorrectly() {
        LongMinHeap heap = newHeap();
        heap.insert(0L);
        heap.insert(1L);
        heap.insert(-1L);

        assertEquals(-1L, heap.delMin(), "First should be -1.");
        assertEquals(0L, heap.delMin(), "Second should be 0.");
        assertEquals(1L, heap.delMin(), "Third should be 1.");
    }

    // ========== Stress Tests ==========

    @Test
    void largeNumberOfElements_maintainsCorrectOrder() {
        LongMinHeap heap = newHeap();
        int n = 10000;

        for (long i = n; i > 0; i--) {
            heap.insert(i);
        }

        assertEquals(n, heap.size(), "Size should be " + n + " after " + n + " inserts.");

        for (long i = 1; i <= n; i++) {
            assertEquals(i, heap.delMin(),
                    "Element should be " + i + " in sorted order.");
        }

        assertTrue(heap.isEmpty(), "Heap should be empty after removing all elements.");
    }

    @Test
    void randomInsertions_producesSortedOutput() {
        LongMinHeap heap = newHeap();
        Random rand = new Random(12345); // Fixed seed for reproducibility
        ArrayList<Long> values = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            long val = rand.nextLong();
            values.add(val);
            heap.insert(val);
        }

        Collections.sort(values);

        for (int i = 0; i < values.size(); i++) {
            assertEquals(values.get(i), heap.delMin(),
                    "Element at position " + i + " should match sorted order.");
        }
    }

    @Test
    void alternatingInsertDelete_maintainsCorrectState() {
        LongMinHeap heap = newHeap();

        heap.insert(5L);
        heap.insert(3L);
        assertEquals(3L, heap.delMin(), "First delMin should return 3.");

        heap.insert(7L);
        heap.insert(1L);
        assertEquals(1L, heap.delMin(), "Second delMin should return 1.");

        heap.insert(9L);
        assertEquals(5L, heap.delMin(), "Third delMin should return 5.");
        assertEquals(7L, heap.delMin(), "Fourth delMin should return 7.");
        assertEquals(9L, heap.delMin(), "Fifth delMin should return 9.");

        assertTrue(heap.isEmpty(), "Heap should be empty at end.");
    }

    @Test
    void repeatedClearAndInsert_maintainsCorrectness() {
        LongMinHeap heap = newHeap();

        for (int round = 0; round < 5; round++) {
            for (long i = 1; i <= 10; i++) {
                heap.insert(i);
            }
            assertEquals(10, heap.size(), "Size should be 10 in round " + round);
            heap.clear();
            assertEquals(0, heap.size(), "Size should be 0 after clear in round " + round);
        }
    }

    // ========== Complex Scenario Tests ==========

    @Test
    void buildHeapFromScratch_maintainsHeapProperty() {
        LongMinHeap heap = newHeap();
        long[] values = {23L, 17L, 14L, 6L, 13L, 10L, 1L, 5L, 7L, 12L};

        for (long val : values) {
            heap.insert(val);
        }

        ArrayList<Long> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.delMin());
        }

        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1) <= result.get(i),
                    "Result must be non-decreasing: " + result.get(i - 1) + " <= " + result.get(i));
        }
    }

    @Test
    void multipleMinPeeks_doNotAffectHeapState() {
        LongMinHeap heap = newHeap();
        heap.insert(10L);
        heap.insert(5L);
        heap.insert(15L);

        for (int i = 0; i < 10; i++) {
            assertEquals(5L, heap.min(), "Min should always be 5 on peek " + i);
        }

        assertEquals(3, heap.size(), "Size should still be 3 after 10 peeks.");
        assertEquals(5L, heap.delMin(), "delMin should return 5.");
        assertEquals(2, heap.size(), "Size should be 2 after delMin.");
    }

    @Test
    void emptyAfterMultipleOperations_behavesCorrectly() {
        LongMinHeap heap = newHeap();

        for (int i = 0; i < 3; i++) {
            heap.insert((long) i);
            heap.delMin();
        }

        assertTrue(heap.isEmpty(), "Heap should be empty after balanced insert/delMin operations.");
    }
}