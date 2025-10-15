# A3 – Priority Queues & Greedy Algorithms (CS 2341, Fall 2025)

* **Points:** Q1 (50) + Q2 (50) = **100**
* **Due:** **Sun, Nov 1nd, 2025, 11:59 PM**
* **Late policy:** 1 day: −10%; 2 days: −20%; 3 days: −30%; >10 days: no credit.

---

## Assignment Focus

Implement **min-heap priority queues** to solve two problems: cookie sweetness optimization and pizza shop scheduling.

* **Q1 (Cookies):** Build `LongMinHeap` extending `AbstractMinHeap<Long>` to solve the cookie mixing problem.
* **Q2 (Pizza):** Create `PizzaJob` and `PizzaJobMinHeap` extending `AbstractMinHeap<PizzaJob>` to minimize average customer waiting time.

> Use Princeton I/O (`StdIn`, `StdOut`) if provided in your template. Autograders rely on strict I/O.

---

## Global Rules

* **Allowed:** Core Java; Princeton `algs4` I/O (`StdIn`, `StdOut`) if included; standard Java utilities.
* **Forbidden for heap implementation:** Java's `PriorityQueue` or any collection-based heaps for your `AbstractMinHeap` implementations. You must implement the heap from scratch.
* **File layout (one public class per file):**
    * `AbstractMinHeap.java` (provided - do not modify)
    * `LongMinHeap.java` (Q1)
    * `CookieSolver.java` (Q1)
    * `PizzaJob.java` (Q2)
    * `PizzaJobMinHeap.java` (Q2)
    * `PizzaSolver.java` (Q2)
* **I/O contract:** Follow each question's **Input/Output** exactly.

---

## Project Layout

```
a3-priority-queues/
├─ src/
│  └─ main/java/com/student_work/
│     ├─ AbstractMinHeap.java       (provided)
│     ├─ LongMinHeap.java           (Q1 - implement)
│     ├─ CookieSolver.java          (Q1 - implement)
│     ├─ PizzaJob.java              (Q2 - implement)
│     ├─ PizzaJobMinHeap.java       (Q2 - implement)
│     └─ PizzaSolver.java           (Q2 - implement)
├─ build.gradle, gradlew, gradlew.bat
└─ README.md
```

---

## Build & Run

```bash
# Build & tests
./gradlew clean build

# Run Q1 (macOS/Linux)
java -cp build/classes/java/main:algs4.jar com.student_work.CookieSolver < input.txt

# Run Q2 (macOS/Linux)
java -cp build/classes/java/main:algs4.jar com.student_work.PizzaSolver < input.txt

# Run (Windows)
java -cp build\classes\java\main;algs4.jar com.student_work.CookieSolver < input.txt
java -cp build\classes\java\main;algs4.jar com.student_work.PizzaSolver < input.txt
```
---

# Q1 – Cookie Sweetness Problem (30 pts)

### Introduction

Jesse loves cookies and wants all cookies to have sweetness ≥ k. To achieve this, repeatedly mix the two **least sweet** cookies using the formula:

```
new_sweetness = (1 × least_sweet) + (2 × second_least_sweet)
```

This process continues until all cookies have sweetness ≥ k. Determine the **minimum number of operations** required, or return **-1** if impossible.

### Example

**Input:**
```
k = 9
A = [2, 7, 3, 6, 4, 6]
```

**Process:**
1. Mix 2 and 3: `2 + 2×3 = 8` → A = [8, 7, 6, 4, 6]
2. Mix 4 and 6: `4 + 2×6 = 16` → A = [16, 8, 7, 6]
3. Mix 6 and 7: `6 + 2×7 = 20` → A = [20, 16, 8]
4. Mix 8 and 16: `8 + 2×16 = 40` → A = [40, 20]

All values ≥ 9, so **4 operations** required.

### Function/Class Description

**Classes to implement:**

1. **`LongMinHeap extends AbstractMinHeap<Long>`**
    - Implement all abstract methods from `AbstractMinHeap`
    - Use **1-indexed array** representation (index 0 unused)
    - Parent of node k is at k/2
    - Children of node k are at 2k and 2k+1
    - Resize by doubling when full, halving when 1/4 full

2. **`CookieSolver`**
    - `public static int cookies(long k, long[] A)` - solver algorithm
    - `private static long mix(long a, long b)` - mixing formula
    - `public static void main(String[] args)` - reads from StdIn, and prints result

### Returns

* `cookies()` returns the number of operations (int), or **-1** if impossible

### Input Format

```
Line 1: n k    (array size and threshold)
Line 2: A[0] A[1] ... A[n-1]    (space-separated sweetness values)
```

### Constraints

* 1 ≤ n ≤ 10^6
* 0 ≤ k ≤ 10^9
* 0 ≤ A[i] ≤ 10^6

### Sample Input

```
6 7
1 2 3 9 10 12
```

### Sample Output

```
2
```

### Explanation

1. Initial: [1, 2, 3, 9, 10, 12]
2. Mix 1 and 2: `1 + 2×2 = 5` → [3, 5, 9, 10, 12]
3. Mix 3 and 5: `3 + 2×5 = 13` → [9, 10, 12, 13]
4. All cookies ≥ 7, so **2 operations**

### Edge Cases to Handle

* If all cookies already ≥ k, return 0
* If only one cookie remains with sweetness < k, return -1
* If k = 0, return 0 immediately

**Key Implementation Notes for `LongMinHeap`:**

```java
public class LongMinHeap extends AbstractMinHeap<Long> {
    private Long[] heap;  // 1-indexed: heap[0] is unused
    private int n;        // number of elements (not including heap[0])

    public LongMinHeap();
    public LongMinHeap(int capacity);
    public int size();
    public boolean isEmpty();
    public void clear();
    public void insert(Long x);
    public PizzaJob min();
    public PizzaJob delMin();
    protected void swim(int k);
    protected void sink(int k);
    protected boolean greater(int i, int j);
    protected void exch(int i, int j);
    protected void resize(int newCap);
    protected int compare(Long a, Long b);
}
```

---

# Q2 – Pizza Shop Scheduling (50 pts)

### Introduction

Tieu owns a pizza restaurant where he minimizes the **average waiting time** of customers rather than following first-come-first-served. Once he starts cooking a pizza, he cannot start another until it's done. The cook does not know about future orders. 

**Waiting time** = (time served) - (time ordered)

### Example

**Input:**
```
3 customers:
Customer A: arrives t=0, cook time=3
Customer B: arrives t=1, cook time=9  
Customer C: arrives t=2, cook time=6
```

**First-Come-First-Served:**
- A served at t=3, wait=3
- B served at t=12, wait=11
- C served at t=18, wait=16
- Average = (3+11+16)/3 = 10

**Optimal:**
- A served at t=3, wait=3
- C served at t=9, wait=7 (start at t=3, finish at t=9)
- B served at t=18, wait=17 (start at t=9, finish at t=18)
- Average = (3+7+17)/3 = 9

### Function/Class Description

**Classes to implement:**

1. **`PizzaJob`**
    - class representing a pizza order
    - Fields: `arrivalTime` (long), `cookTime` (long)
    - Constructor: `PizzaJob(long arrivalTime, long cookTime)`
    - Getters: `getArrivalTime()`, `getCookTime()`
    - Override `toString()` for debugging

2. **`PizzaJobMinHeap extends AbstractMinHeap<PizzaJob>`**
    - Min-heap ordered by **cook time**
    - Implement all abstract methods from `AbstractMinHeap`
    - Use 1-indexed array representation
    - `compare()` should order by cook time

3. **`PizzaSolver`**
    - `public static long minimumAverageWaitTime(List<PizzaJob> jobs)` - solver algorithm
    - `public static void main(String[] args)` - reads from StdIn, prints result


### Returns

* `minimumAverageWaitTime()` returns the **integer part** of minimum average waiting time (long)

### Input Format

```
Line 1: N    (number of customers)
Next N lines: Ti Li    (arrival time and cook time for customer i)
```

**Note:** The ith customer is NOT necessarily the customer arriving at the ith arrival time.

### Constraints

* 1 ≤ N ≤ 10^5
* 0 ≤ Ti ≤ 10^9
* 1 ≤ Li ≤ 10^9

### Sample Input #00

```
3
0 3
1 9
2 6
```

### Sample Output #00

```
9
```

### Sample Input #01

```
3
0 3
1 9
2 5
```

### Sample Output #01

```
8
```

### Explanation #01

Let A = person at t=0, B = person at t=1, C = person at t=2.

**Optimal order: A, C, B**
- A: served at t=3, wait = 3-0 = 3
- C: served at t=8, wait = 8-2 = 6
- B: served at t=17, wait = 17-1 = 16

Average = (3+6+16)/3 = 25/3 = 8.33... → **8**

### Starter Code

See provided files `PizzaJob.java`, `PizzaJobMinHeap.java`, and `PizzaSolver.java` with method stubs.


```java
public class PizzaJob {
    private final long arrivalTime;
    private final long cookTime;

    public PizzaJob(long arrivalTime, long cookTime);
    public long getArrivalTime();
    public long getCookTime();
    public String toString();
}
```

```java
public class PizzaJobMinHeap extends AbstractMinHeap<Long> {
    private PizzaJob[] heap;    // 1-indexed: heap[0] is unused
    private int n;              // number of elements in heap

    public PizzaJobMinHeap();
    public PizzaJobMinHeap(int capacity);
    public int size();
    public boolean isEmpty();
    public void clear();
    public void insert(PizzaJob x);
    public PizzaJob min();
    public PizzaJob delMin();
    protected void swim(int k);
    protected void sink(int k);
    protected boolean greater(int i, int j);
    protected void exch(int i, int j);
    protected void resize(int newCap);
    protected int compare(PizzaJob a, PizzaJob b);
}
```
