/**
 * UserHistory
 * @author    Morgan Noonan
 */
package cs1501_p2;

import java.util.*;

public class UserHistory implements Dict {
    private String currentPrefix = new String();
    private Node root;
    private int wordCount = 0;
    private FreqString[] top5 = new FreqString[5];

    public UserHistory() {
        wordCount = 0;
        currentPrefix = "";
        clearTop5();
    }

    /**
     * getRoot
     * 
     * @return root
     */
    public Node getRoot() {
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
            root = new Node(key.charAt(0));
            Node curr = root;
            for (int k = 1; k < key.length(); k++) {
                Node addChar = new Node(key.charAt(k));
                curr.down = addChar;
                curr = curr.down;
            }
            Node addChar = new Node('!');
            wordCount++;
            curr.down = addChar;
        } else {// DLB not empty
            if (contains(key)) {
                Node curr = root;
                for (int i = 0; i < key.length(); i++) {
                    curr = checkR(curr, key.charAt(i));
                    curr = curr.down;
                }
                if (curr != null && curr.let == '!') {
                    curr.freq += 1;
                }
            }
            Node index = root;
            Node prev = null;
            for (int k = 0; k < key.length(); k++) {
                index = nextR(index, key.charAt(k), prev); // go through LL getRight, if not there set right
                if (index.down == null) {// if down doesn't exist add down new char
                    Node addChar;
                    if (k == key.length() - 1) {
                        addChar = new Node('!');
                        wordCount++;
                    } else {
                        addChar = new Node(key.charAt(k));
                    }
                    index.down = addChar;
                }
                prev = index;
                index = index.down; // move down
            }
        }
    }

    private Node nextR(Node curr, char k, Node prev) {
        if (prev != null && prev.down.let > k) {
            if (curr == root) {
                Node temp = new Node(k);
                temp.right = curr;
                root = temp;
                return temp;
            }
            Node temp = new Node(k);
            temp.right = prev.down;
            prev.down = temp;
            return temp;
        }
        if (curr.let == k) {
            return curr;
        }
        if (curr.right != null && curr.right.let > k) {
            Node temp = new Node(k);
            temp.right = curr.right;
            curr.right = temp;
            return temp;
        }
        if (curr.right == null) {
            Node temp = new Node(k);
            curr.right = temp;
            return temp;
        } else {
            return nextR(curr.right, k, prev);
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
        Node curr = root;
        for (int k = 0; k < key.length(); k++) {
            curr = checkR(curr, key.charAt(k));
            if (curr == null || curr.down == null) {
                return false;
            }
            curr = curr.down;
        }
        if (curr != null && curr.down != null && curr.down.let != '!') {
            return false;
        }
        return true;
    }

    private Node checkR(Node curr, char k) {
        if (curr == null) {
            return null;
        }
        if (curr.let == k) {
            return curr;
        }
        if (curr.right == null) {
            return null;
        }
        return checkR(curr.right, k);
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
        Node curr = root;
        for (int k = 0; k < pre.length(); k++) {
            curr = checkR(curr, pre.charAt(k));
            if (curr == null || curr.down == null) {
                return false;
            }
            curr = curr.down;
        }
        if (curr != null && curr.down != null && curr.down.let == '!') {
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
        Node curr = root;
        currentPrefix = currentPrefix + next;
        boolean word = false;
        boolean pre = false;
        for (int k = 0; k < currentPrefix.length(); k++) {
            curr = checkR(curr, currentPrefix.charAt(k));
            if (curr == null) {
                return -1;
            }
            if (curr.down == null) {
                break;
            }
            curr = curr.down;
            if (k == currentPrefix.length() - 1) {
                if (curr != null) {
                    curr = checkR(curr, '!');
                    if (curr != null) {
                        word = true;
                    } else {
                        pre = true;
                    }
                    if (curr != null && curr.right != null) {
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
        clearTop5();
        ArrayList<String> list = new ArrayList<>();
        if (root == null || currentPrefix == null) {
            return list;
        }
        Node curr = root;
        for (int i = 0; i < currentPrefix.length(); i++) {
            curr = checkR(curr, currentPrefix.charAt(i));
            if (curr == null) {
                return list;
            }
            if (curr.down != null) {
                curr = curr.down;
                if (i == currentPrefix.length() - 1) {
                    possible5(curr, currentPrefix);
                }
            }
        }
        Arrays.sort(top5, Comparator.comparingInt((FreqString fs) -> fs.freq).reversed());
        for (int i = 0; i < top5.length; i++) {
            if (top5[i].freq != 0)
                list.add(top5[i].word);
        }
        return list;
    }

    private void possible5(Node curr, String word) {
        if (curr == null)
            return;
        if (curr.let == '!' && notInTop5(word)) {
            if (curr.freq > top5[4].freq) {
                if (curr.freq > top5[3].freq) {
                    top5[4] = top5[3];
                    if (curr.freq > top5[2].freq) {
                        top5[3] = top5[2];
                        if (curr.freq > top5[1].freq) {
                            top5[2] = top5[1];
                            if (curr.freq > top5[0].freq) {
                                top5[1] = top5[0];
                                top5[0] = new FreqString(word, curr.freq);
                            } else
                                top5[1] = new FreqString(word, curr.freq);
                        } else
                            top5[2] = new FreqString(word, curr.freq);
                    } else
                        top5[3] = new FreqString(word, curr.freq);
                } else
                    top5[4] = new FreqString(word, curr.freq);
            }
        }
        if (curr.down != null) {
            possible5(curr.down, word + curr.let);
        }
        if (curr.right != null) {
            possible5(curr.right, word);
        }
    }

    /**
     * traverse
     * List all of the words currently stored in the dictionary
     * 
     * @return ArrayList<String> List of all valid words in the dictionary
     */
    public ArrayList<String> traverse() {
        ArrayList<String> list = new ArrayList<String>();
        Node curr = root;
        String str = "";
        list = tRec(curr, list, str);
        return list;
    }

    private ArrayList<String> tRec(Node curr, ArrayList<String> list, String str) {
        if (curr == null) {
            return list;
        }
        if (curr.let == '!') {
            for (int k = 0; k < curr.freq; k++) {
                list.add(str);
            }
        }
        if (curr.down != null) {
            tRec(curr.down, list, str + curr.let);
        }
        if (curr.right != null) {
            tRec(curr.right, list, str);
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

    private static class Node {
        private char let;
        private Node right;
        private Node down;
        private int freq;

        private Node(char let) {
            this.let = let;
            this.right = null;
            this.down = null;
            if (let == '!') {
                this.freq = 1;
            } else {
                this.freq = 0;
            }
        }

    }

    private static class FreqString {
        private String word;
        private int freq;

        private FreqString(String str, int i) {
            this.word = str;
            this.freq = i;
        }
    }

    private void clearTop5() {
        FreqString placeholder = new FreqString("", 0);
        top5[0] = placeholder;
        top5[1] = placeholder;
        top5[2] = placeholder;
        top5[3] = placeholder;
        top5[4] = placeholder;
    }

    private boolean notInTop5(String str) {
        for (int i = 0; i < 5; i++) {
            if (top5[i].word.equals(str))
                return false;
        }
        return true;
    }
}
