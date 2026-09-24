# Mini HashMap

A small Java hash map built from scratch to understand generics, hashing, collision handling, the `equals()` / `hashCode()` contract, and dynamic resizing.

This is an educational implementation with no external dependencies. It implements a focused set of map operations; it does not implement the full `java.util.Map` interface and is not intended as a replacement for `java.util.HashMap` in production.

## Features

- Generic keys and values: `MiniHashMap<K, V>`.
- An array of buckets with an initial capacity of 16.
- Separate chaining using a custom singly linked `Entry<K, V>` structure.
- Hash spreading with `h ^ (h >>> 16)`.
- Bucket selection using `hash & (capacity - 1)`.
- A load factor of 0.75 and automatic capacity doubling.
- Redistribution that reuses existing entries without changing the number of mappings.
- Rejection of null keys and values.
- Six executable test scenarios with checks that throw `AssertionError` on failure.

## Requirements

- JDK 17 or later with `java` and `javac` on your PATH. Verified with JDK 23.0.2.
- Optional: VS Code with Java language and debugger extensions for `.vscode/launch.json`.

There is no Maven or Gradle setup. The source files are in the repository root and use the default package.

## Run in VS Code

1. Open the repository folder in VS Code.
2. Let the Java extensions load the project.
3. Open **Run and Debug**, select **Run Main**, and start it.
4. Choose one of the six test scenarios from the dropdown.

The launch configuration is shared in [`.vscode/launch.json`](.vscode/launch.json).

## Compile and run from a terminal

From the repository root, using PowerShell:

```powershell
New-Item -ItemType Directory -Force .build | Out-Null
javac -Xlint:all -d .build Entry.java MiniHashMap.java Main.java
java -cp .build Main basic
```

Run all scenarios:

```powershell
$cases = @("basic", "resize", "equal-keys", "remove-chain", "negative-hash", "empty-null")
foreach ($case in $cases) {
    java -cp .build Main $case
    if ($LASTEXITCODE -ne 0) {
        throw "Test failed: $case"
    }
}
```

Checks use explicit `AssertionError` instances, so the `-ea` flag is not required. With no argument, `Main` runs `basic`. An unrecognized argument currently also falls back to `basic`; use the scenario names below to avoid accidentally running a different test.

## Public API

| Method | Behavior |
| --- | --- |
| `V put(K key, V value)` | Inserts a new mapping and returns `null`, or replaces an existing value and returns the previous value. |
| `V get(K key)` | Returns the associated value, or `null` when absent. |
| `boolean containsKey(K key)` | Reports whether the key exists. |
| `V remove(K key)` | Removes a mapping and returns its previous value, or `null` when absent. |
| `int size()` | Returns the number of mappings, not the bucket count. |
| `boolean isEmpty()` | Reports whether the map contains no mappings. |

Passing a null key to a key-based operation, or a null value to `put`, throws `IllegalArgumentException` before modifying the map. Since stored values cannot be null, a null result from `get` unambiguously means the key is absent.

```java
MiniHashMap<String, Integer> scores = new MiniHashMap<>();

scores.put("Ada", 10);              // null: new mapping
Integer previous = scores.put("Ada", 20); // 10: existing value replaced
Integer current = scores.get("Ada");     // 20
boolean present = scores.containsKey("Ada"); // true
int count = scores.size();          // 1
Integer removed = scores.remove("Ada");  // 20
boolean empty = scores.isEmpty();   // true
```

## How it works

### Hashing and bucket selection

The map obtains the key's `hashCode()` and mixes its upper bits into its lower bits:

```java
int h = key.hashCode();
int hash = h ^ (h >>> 16);
int index = hash & (buckets.length - 1);
```

Capacity begins at 16 and doubles, keeping it a power of two. The mask therefore produces a valid index even when the hash is negative. Spreading can improve distribution for this masking scheme, but it does not eliminate collisions.

### Collisions and equality

Each bucket stores the first entry in a singly linked chain:

```text
bucket -> Entry -> Entry -> null
```

The map searches that chain using key equality. If the key is found, `put` replaces its value without changing `size`. Otherwise, a new entry is inserted at the beginning of the chain.

Different keys can share a hash or bucket. Equal keys must have equal hash codes. Keys must not change the fields used by `equals()` or `hashCode()` while stored in the map; a final key reference does not make the key object immutable.

### Resizing

After insertion, the map grows when `size > capacity * 0.75`:

| Number of mappings | Capacity |
| --- | --- |
| 0–12 | 16 |
| 13–24 | 32 |
| 25–48 | 64 |

Updating an existing key does not increase the size. During growth, each entry gets a new bucket index based on the new capacity. The old `next` reference is saved before relinking, so the rest of the original chain is not lost. Entry objects are reused and `size` stays unchanged.

### Generic array creation

Java does not allow `new Entry<K, V>[capacity]`. The implementation creates an `Entry<?, ?>[]` and casts it to `Entry<K, V>[]`, with a localized `@SuppressWarnings("unchecked")`. The array stays private and the implementation stores entries using the map's type parameters.

## Tests

| Scenario | Coverage |
| --- | --- |
| `basic` | Colliding string keys (`"Aa"` and `"BB"`), insertion, replacement, return values, removal, and size. |
| `resize` | Preserved values across the 13th and 25th insertions, including colliding keys and an updated value. |
| `equal-keys` | Lookup and replacement using distinct string instances that are equal. |
| `remove-chain` | Independent removal of the head, middle, and tail of a three-entry chain. |
| `negative-hash` | Keys with negative hash codes, including `Integer.MIN_VALUE`. |
| `empty-null` | Empty-map behavior, null rejection, preservation of existing data after rejection, and empty state after removal. |

The resize test checks public behavior and size. To inspect the internal capacity directly, place a breakpoint in `resize()` and watch `buckets.length` in the debugger.

## Complexity

Assuming constant-time key hashing/equality and reasonably distributed hashes:

| Operation | Expected cost | Worst case |
| --- | --- | --- |
| `get`, `containsKey`, `remove` | O(1) | O(n) with a long collision chain |
| `put` | Amortized O(1) | O(n) for a long chain or a resize |
| `size`, `isEmpty` | O(1) | O(1) |
| Resize | O(capacity + n) | Visits the old buckets and every entry |

Space usage is O(capacity + n), where `n` is the number of mappings. Unlike the JDK implementation, collision chains are never converted to trees.

## Scope and limitations

- Not thread-safe; no synchronization or concurrent operations.
- No iteration API, collection views, ordering guarantee, or automatic shrinking.
- No configurable initial capacity or load factor.
- No null keys or values, unlike `java.util.HashMap`.
- No maximum-capacity or allocation-failure recovery logic; this is a small learning implementation.
- Hash codes are recomputed during resizing rather than cached in entries.

## Project layout

```text
Entry.java             Linked entry containing a key, value, and next reference
MiniHashMap.java       Hash table implementation
Main.java              Executable test scenarios
.vscode/launch.json    VS Code scenario selection
```

Compiled classes and build output are ignored by Git. Review `git status` before publishing; `.gitignore` does not remove files already tracked in history.

## License

[MIT](LICENSE).
