public class Main {
    public static void main(String[] args){
        String operation = args.length == 0 ? "basic" : args[0].toLowerCase();
        switch (operation) {
            case "basic" -> basicMapTest();
            case "resize" -> resizeMapTest();
            case "equal-keys" -> equalKeysTest();
            case "remove-chain" -> removeChainTest();
            case "negative-hash" -> negativeHashTest();
            case "empty-null" -> emptyAndNullTest();
            default -> basicMapTest();
        };
    }

    private static void basicMapTest(){
        MiniHashMap<String, String> miniHash = new MiniHashMap<>();
        check(miniHash.put("Aa", "Value 1") == null, "First insert must return null");
        check(miniHash.put("BB", "Value 2") == null, "Second insert must return null");

        System.out.println("Key 1 " + miniHash.get("Aa"));
        System.out.println("Key 2 " + miniHash.get("BB"));
        System.out.println("Size " + miniHash.size());
        check("Value 1".equals(miniHash.get("Aa")), "Aa must return Value 1");
        check("Value 2".equals(miniHash.get("BB")), "BB must return Value 2");
        check(miniHash.size() == 2, "Size must be 2 after two inserts");
        
        String oldValue = miniHash.put("Aa", "Value 3");
        System.out.println("Old Value " + oldValue);
        System.out.println("Key 1 Updated " + miniHash.get("Aa"));
        System.out.println("Size " + miniHash.size());
        check("Value 1".equals(oldValue), "Updating Aa must return Value 1");
        check("Value 3".equals(miniHash.get("Aa")), "Aa must return Value 3 after update");
        check(miniHash.size() == 2, "Updating Aa must not change size");
        
        String removed = miniHash.remove("Aa");
        System.out.println("Removed " + removed);
        System.out.println("Key 2 " + miniHash.get("BB"));
        System.out.println("Size " + miniHash.size());
        check("Value 3".equals(removed), "Removing Aa must return Value 3");
        check(miniHash.get("Aa") == null, "Aa must be absent after removal");
        check("Value 2".equals(miniHash.get("BB")), "BB must remain accessible");
        check(miniHash.size() == 1, "Size must be 1 after removing Aa");

        String removeNull = miniHash.remove("Aa");
        System.out.println("Not Removed " + removeNull);
        System.out.println("Key 2 " + miniHash.get("BB"));
        System.out.println("Size " + miniHash.size());
        check(removeNull == null, "Removing missing Aa must return null");
        check(miniHash.size() == 1, "Removing missing Aa must not change size");
        System.out.println("Basic test passed.");
    }

    private static void resizeMapTest() {
        MiniHashMap<String, String> miniHash = new MiniHashMap<>();
        String[] keys = {
            "key-01", "key-02", "key-03", "key-04", "key-05", "key-06",
            "key-07", "key-08", "key-09", "key-10", "Aa", "BB",
            "key-13", "key-14", "key-15", "key-16", "key-17", "key-18",
            "key-19", "key-20", "key-21", "key-22", "key-23", "key-24",
            "key-25"
        };

        for (int index = 0; index < 12; index++) {
            miniHash.put(keys[index], valueFor(keys[index]));
        }
        printMapState(miniHash, keys, 12, "After inserting 12 keys");
        checkMapState(miniHash, keys, 12, false, "After inserting 12 keys");

        String oldValue = miniHash.put("Aa", "updated-Aa");
        System.out.println("Previous value of Aa: " + oldValue);
        printMapState(miniHash, keys, 12, "After updating one key");
        check("value-Aa".equals(oldValue), "Updating Aa must return its previous value");
        checkMapState(miniHash, keys, 12, true, "After updating one key");

        miniHash.put(keys[12], valueFor(keys[12]));
        printMapState(miniHash, keys, 13, "After inserting key number 13");
        checkMapState(miniHash, keys, 13, true, "After inserting key number 13");

        for (int index = 13; index < keys.length; index++) {
            miniHash.put(keys[index], valueFor(keys[index]));
        }
        printMapState(miniHash, keys, 25, "After inserting 25 keys");
        checkMapState(miniHash, keys, 25, true, "After inserting 25 keys");

        System.out.println("Resize test finished.");
    }

    private static String valueFor(String key) {
        return "value-" + key;
    }

    private static void printMapState(MiniHashMap<String, String> miniHash, String[] keys,
            int keyCount, String message) {
        System.out.println("\n" + message);
        System.out.println("Size: " + miniHash.size());
        for (int index = 0; index < keyCount; index++) {
            System.out.println(keys[index] + " -> " + miniHash.get(keys[index]));
        }
    }

        private static void checkMapState(MiniHashMap<String, String> miniHash, String[] keys,
            int keyCount, boolean aaUpdated, String message) {
        check(miniHash.size() == keyCount, message + ": unexpected size");
        for (int index = 0; index < keyCount; index++) {
            String expectedValue = keys[index].equals("Aa") && aaUpdated
                ? "updated-Aa" : valueFor(keys[index]);
            check(expectedValue.equals(miniHash.get(keys[index])),
                    message + ": incorrect value for " + keys[index]);
        }
    }

    private static void equalKeysTest() {
        MiniHashMap<String, String> miniHash = new MiniHashMap<>();
        String insertedKey = new String("Mario");
        String lookupKey = new String("Mario");

        miniHash.put(insertedKey, "first value");
        check("first value".equals(miniHash.get(lookupKey)),
            "Equal keys must retrieve the same value");
        check(miniHash.size() == 1, "Equal keys must not increase size");
        check("first value".equals(miniHash.put(lookupKey, "updated value")),
            "Updating with an equal key must return the previous value");
        check("updated value".equals(miniHash.get(insertedKey)),
            "Updating with an equal key must update the existing entry");
        check(miniHash.size() == 1, "Updating an equal key must not increase size");
        System.out.println("Equal keys test passed.");
    }

    private static void removeChainTest() {
        testRemoveFromChain(33, "first", 17, 1);
        testRemoveFromChain(17, "middle", 33, 1);
        testRemoveFromChain(1, "last", 33, 17);
        System.out.println("Remove chain test passed.");
        }

        private static void testRemoveFromChain(int removedKey, String position,
            int remainingKey1, int remainingKey2) {
        MiniHashMap<Integer, String> miniHash = new MiniHashMap<>();
        miniHash.put(1, "one");
        miniHash.put(17, "seventeen");
        miniHash.put(33, "thirty-three");

        check(valueForInteger(removedKey).equals(miniHash.remove(removedKey)),
            "Removing the " + position + " entry must return its value");
        check(miniHash.get(removedKey) == null,
            "Removed " + position + " entry must no longer be accessible");
        check(valueForInteger(remainingKey1).equals(miniHash.get(remainingKey1)),
            "First remaining entry must stay accessible");
        check(valueForInteger(remainingKey2).equals(miniHash.get(remainingKey2)),
            "Second remaining entry must stay accessible");
        check(miniHash.containsKey(remainingKey1), "Remaining key must be contained");
        check(!miniHash.containsKey(removedKey), "Removed key must not be contained");
        check(miniHash.size() == 2, "Size must be 2 after removing one entry");
        }

        private static String valueForInteger(int key) {
        return switch (key) {
            case 1 -> "one";
            case 17 -> "seventeen";
            case 33 -> "thirty-three";
            default -> throw new IllegalArgumentException("Unexpected test key");
        };
    }

    private static void negativeHashTest() {
        MiniHashMap<Integer, String> miniHash = new MiniHashMap<>();
        Integer minimumInteger = Integer.MIN_VALUE;
        Integer negativeInteger = -1;

        miniHash.put(minimumInteger, "minimum integer");
        miniHash.put(negativeInteger, "negative integer");
        System.out.println("Integer.MIN_VALUE hash: " + minimumInteger.hashCode());
        System.out.println("Integer.MIN_VALUE value: " + miniHash.get(minimumInteger));
        System.out.println("-1 hash: " + negativeInteger.hashCode());
        System.out.println("-1 value: " + miniHash.get(negativeInteger));
        System.out.println("Size: " + miniHash.size());
        check("minimum integer".equals(miniHash.get(minimumInteger)),
            "Integer.MIN_VALUE must retrieve its value");
        check("negative integer".equals(miniHash.get(negativeInteger)),
            "Negative hash key must retrieve its value");
        check(miniHash.size() == 2, "Negative hash test size must be 2");
        System.out.println("Negative hash test passed.");
    }

    private static void emptyAndNullTest() {
        MiniHashMap<String, String> miniHash = new MiniHashMap<>();
        check(miniHash.isEmpty(), "New map must be empty");
        System.out.println("Empty get: " + miniHash.get("missing"));
        System.out.println("Empty remove: " + miniHash.remove("missing"));
        System.out.println("Empty containsKey: " + miniHash.containsKey("missing"));
        System.out.println("Empty size: " + miniHash.size());
        check(miniHash.get("missing") == null, "Missing key in empty map must return null");
        check(miniHash.remove("missing") == null, "Removing missing key must return null");
        check(!miniHash.containsKey("missing"), "Missing key must not be contained");

        printNullKeyOperation("put(null, value)", () -> miniHash.put(null, "value"));
        printNullKeyOperation("get(null)", () -> miniHash.get(null));
        printNullKeyOperation("containsKey(null)", () -> miniHash.containsKey(null));
        printNullKeyOperation("remove(null)", () -> miniHash.remove(null));
        printNullKeyOperation("put(key, null)", () -> miniHash.put("key", null));
        check(miniHash.size() == 0, "Rejected operations must not change empty map size");

        miniHash.put("stored", "stored value");
        check(!miniHash.isEmpty(), "Map with an entry must not be empty");
        printNullKeyOperation("get(null) on populated map", () -> miniHash.get(null));
        printNullKeyOperation("put(stored, null)", () -> miniHash.put("stored", null));
        check("stored value".equals(miniHash.get("stored")),
                "Rejected null operation must preserve existing entries");
        check(miniHash.size() == 1, "Rejected null operation must preserve size");
        check(miniHash.containsKey("stored"), "Existing key must be contained");
        check("stored value".equals(miniHash.remove("stored")), "Stored value must be removable");
        check(miniHash.isEmpty(), "Map must be empty after removing all entries");
        System.out.println("Empty and null test passed.");
    }

    private static void printNullKeyOperation(String operation, Runnable action) {
        try {
            action.run();
            throw new AssertionError(operation + ": expected IllegalArgumentException");
        } catch (IllegalArgumentException exception) {
            System.out.println(operation + ": passed");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
