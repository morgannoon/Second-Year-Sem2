/**
 * DLB
 * @author    Morgan Noonan
 */
package cs1501_p2;

import java.util.*;

public class DLB implements Dict {
    private DLBNode root;
    private String currentPrefix = new String();
    private int wordCount = 0;

    public DLB() {
    }

    /**
     * getRoot
     * 
     * @return root
     */
    public DLBNode getRoot() {
        return root;
    }

    /**
     * getCurrentPrefix
     * 
     * @return currentPrefix
     */
    public String getCurrentPrefix() {
        return currentPrefix;
    }

    /**
     * add
     * Add a new word to the dictionary
     * 
     * @param key New word to be added to the dictionary
     */
    public void add(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        if (root == null) { // DLB is empty
            root = new DLBNode(key.charAt(0));
            DLBNode curr = root;
            for (int k = 1; k < key.length(); k++) {
                DLBNode addChar = new DLBNode(key.charAt(k));
                curr.setDown(addChar);
                curr = curr.getDown();
            }
            DLBNode addChar = new DLBNode('!');
            wordCount++;
            curr.setDown(addChar);
        } else {// DLB not empty
            DLBNode index = root;
            DLBNode prev = null;
            for (int k = 0; k < key.length(); k++) {
                index = nextR(index, key.charAt(k), prev); // go through LL getRight, if not there set right
                if (index.getDown() == null) {// if down doesn't exist add down new char
                    DLBNode addChar;
                    if (k == key.length() - 1) {
                        addChar = new DLBNode('!');
                        wordCount++;
                    } else {
                        addChar = new DLBNode(key.charAt(k));
                    }
                    index.setDown(addChar);
                }
                prev = index;
                index = index.getDown(); // move down
            }
        }
    }

    private DLBNode nextR(DLBNode curr, char k, DLBNode prev) {
        if (prev != null && prev.getDown().getLet() > k) {
            if (curr == root) {
                DLBNode temp = new DLBNode(k);
                temp.setRight(curr);
                root = temp;
                return temp;
            }
            DLBNode temp = new DLBNode(k);
            temp.setRight(prev.getDown());
            prev.setDown(temp);
            return temp;
        }
        if (curr.getLet() == k) {
            return curr;
        }
        if (curr.getRight() != null && curr.getRight().getLet() > k) {
            DLBNode temp = new DLBNode(k);
            temp.setRight(curr.getRight());
            curr.setRight(temp);
            return temp;
        }
        if (curr.getRight() == null) {
            DLBNode temp = new DLBNode(k);
            curr.setRight(temp);
            return temp;
        } else {
            return nextR(curr.getRight(), k, prev);
        }
    }

    /**
     * contains
     * Check if the dictionary contains a word
     * 
     * @param key Word to search the dictionary for
     * @return true if key is in the dictionary, false otherwise
     */
    public boolean contains(String key) {
        if (root == null || key == null || key.isEmpty()) {
            return false;
        }
        DLBNode curr = root;
        for (int k = 0; k < key.length(); k++) {
            curr = checkR(curr, key.charAt(k));
            if (curr == null || curr.getDown() == null) {
                return false;
            }
            curr = curr.getDown();
        }
        if (curr != null && curr.getDown() != null && curr.getDown().getLet() != '!') {
            return false;
        }
        return true;
    }

    private DLBNode checkR(DLBNode curr, char k) {
        if (curr == null) {
            return null;
        }
        if (curr.getLet() == k) {
            return curr;
        }
        if (curr.getRight() == null) {
            return null;
        }
        return checkR(curr.getRight(), k);
    }

    /**
     * containsPrefix
     * Check if a String is a valid prefix to a word in the dictionary
     * 
     * @param pre Prefix to search the dictionary for
     * @return true if prefix is valid, false otherwise
     */
    public boolean containsPrefix(String pre) {
        if (root == null || pre == null || pre.isEmpty()) {
            return false;
        }
        DLBNode curr = root;
        for (int k = 0; k < pre.length(); k++) {
            curr = checkR(curr, pre.charAt(k));
            if (curr == null || curr.getDown() == null) {
                return false;
            }
            curr = curr.getDown();
        }
        if (curr != null && curr.getDown() != null && curr.getDown().getLet() == '!') {
            return false;
        }
        return true;
    }

    /**
     * searchByChar
     * Search for a word one character at a time
     * 
     * @param next Next character to search for
     * @return int value indicating result for current by-character search:
     *         -1: not a valid word or prefix
     *         0: valid prefix, but not a valid word
     *         1: valid word, but not a valid prefix to any other words
     *         2: both valid word and a valid prefix to other words
     */
    public int searchByChar(char next) {
        if (root == null) {
            return -1;
        }
        DLBNode curr = root;
        currentPrefix = currentPrefix + next;
        boolean word = false;
        boolean pre = false;
        for (int k = 0; k < currentPrefix.length(); k++) {
            curr = checkR(curr, currentPrefix.charAt(k));
            if (curr == null) {
                return -1;
            }
            if (curr.getDown() == null) {
                break;
            }
            curr = curr.getDown();

            if (k == currentPrefix.length() - 1) {
                if (curr != null) {
                    curr = checkR(curr, '!');
                    if (curr != null) {
                        word = true;
                    } else {
                        pre = true;
                    }
                    if (curr != null && curr.getRight() != null) {
                        pre = true;
                    }
                }
            }
        }
        if (pre && !word) {
            return 0;
        } else if (!pre && word) {
            return 1;
        } else if (pre && word) {
            return 2;
        } else {
            return -1;
        }
    }

    /**
     * resetByChar
     * Reset the state of the current by-character search
     */
    public void resetByChar() {
        currentPrefix = "";
    }

    /**
     * suggest
     * Suggest up to 5 words from the dictionary based on the current
     * by-character search. Ordering should depend on the implementation.
     * 
     * @return ArrayList<String> List of up to 5 words that are prefixed by
     *         the current by-character search
     */
    public ArrayList<String> suggest() {
        ArrayList<String> list = new ArrayList<>();
        if (root == null || currentPrefix == null) {
            return list;
        }
        DLBNode curr = root;
        for (int i = 0; i < currentPrefix.length(); i++) {
            curr = checkR(curr, currentPrefix.charAt(i));
            if (curr == null) {
                return list;
            }
            if (curr.getDown() != null) {
                curr = curr.getDown();
                if (i == currentPrefix.length() - 1) {
                    list = possible5(curr, list, currentPrefix);
                }
            }
        }
        return list;
    }

    private ArrayList<String> possible5(DLBNode curr, ArrayList<String> list, String word) {
        if (curr == null || list.size() == 5)
            return list;
        if (curr.getLet() == '!') {
            list.add(word);
        }
        if (curr.getDown() != null) {
            list = possible5(curr.getDown(), list, word + curr.getLet());
        }
        if (curr.getRight() != null) {
            list = possible5(curr.getRight(), list, word);
        }
        return list;
    }

    /**
     * traverse
     * List all of the words currently stored in the dictionary
     * 
     * @return ArrayList<String> List of all valid words in the dictionary
     */
    public ArrayList<String> traverse() {
        ArrayList<String> list = new ArrayList<String>();
        DLBNode curr = root;
        String str = "";
        list = tRec(curr, list, str);
        return list;
    }

    private ArrayList<String> tRec(DLBNode curr, ArrayList<String> list, String str) {
        if (curr == null) {
            return list;
        }
        if (curr.getLet() == '!') {
            list.add(str);
        }
        if (curr.getDown() != null) {
            tRec(curr.getDown(), list, str + curr.getLet());
        }
        if (curr.getRight() != null) {
            tRec(curr.getRight(), list, str);
        }
        return list;
    }

    /**
     * count
     * Count the number of words in the dictionary
     * 
     * @return int, the number of (distinct) words in the dictionary
     */
    public int count() {
        return wordCount;
    }
}
