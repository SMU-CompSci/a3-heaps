package com.student_work;

import java.util.NoSuchElementException;

public abstract class AbstractMinHeap<T> {

    /**
     * Returns the number of elements currently in the heap.
     *
     * @return the size of the heap
     */
    public abstract int size();

    /**
     * Checks whether the heap contains any elements.
     *
     * @return true if the heap is empty, false otherwise
     */
    public abstract boolean isEmpty();

    /**
     * Removes all elements from the heap, resetting it to an empty state.
     */
    public abstract void clear();

    /**
     * Inserts a new element into the heap while maintaining the min-heap property.
     * The element is added at the end and then "swum" up to its correct position.
     *
     * @param x the element to insert
     */
    public abstract void insert(T x);

    /**
     * Returns the minimum element in the heap without removing it.
     * In a min-heap, this is always the root element (at index 1 or 0).
     *
     * @return the minimum element
     * @throws NoSuchElementException if the heap is empty
     */
    public abstract T min();

    /**
     * Removes and returns the minimum element from the heap.
     * The last element is moved to the root and then "sunk" down to maintain
     * the min-heap property.
     *
     * @return the minimum element that was removed
     * @throws NoSuchElementException if the heap is empty
     */
    public abstract T delMin();

    /**
     * Restores the heap property by moving the element at position k upward
     * in the heap tree (toward the root). Used after insertion.
     * The element is repeatedly exchanged with its parent while it is smaller
     * than its parent.
     *
     * @param k the index of the element to swim up
     */
    protected abstract void swim(int k);

    /**
     * Restores the heap property by moving the element at position k downward
     * in the heap tree (toward the leaves). Used after deletion.
     * The element is repeatedly exchanged with its smaller child while it is
     * greater than at least one of its children.
     *
     * @param k the index of the element to sink down
     */
    protected abstract void sink(int k);

    /**
     * Compares the elements at positions i and j in the heap.
     *
     * @param i the index of the first element
     * @param j the index of the second element
     * @return true if element at i is greater than element at j, false otherwise
     */
    protected abstract boolean greater(int i, int j);

    /**
     * Exchanges (swaps) the elements at positions i and j in the heap array.
     *
     * @param i the index of the first element
     * @param j the index of the second element
     */
    protected abstract void exch(int i, int j);

    /**
     * Resizes the internal array that stores the heap elements.
     * Typically called when the heap needs to grow (when full) or shrink
     * (when mostly empty) to maintain efficiency.
     *
     * @param newCap the new capacity for the heap array
     */
    protected abstract void resize(int newCap);

    /**
     * Compares two elements to determine their ordering.
     *
     * @param a the first element
     * @param b the second element
     * @return a negative integer if a < b, zero if a == b,
     *         or a positive integer if a > b
     */
    protected abstract int compare(T a, T b);
}