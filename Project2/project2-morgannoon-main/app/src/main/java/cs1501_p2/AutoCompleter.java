/**
 * AutoCompleter
 * @author    Morgan Noonan
 */
package cs1501_p2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class AutoCompleter implements AutoComplete_Inter {
    private DLB dTrie;
    private UserHistory rTrie;

    public AutoCompleter(String dict) {
        dTrie = new DLB();
        rTrie = new UserHistory();
        try {
            File dictFile = new File(dict);
            Scanner reader = new Scanner(dictFile);

            while (reader.hasNextLine()) {
                String word = reader.nextLine();
                dTrie.add(word);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AutoCompleter(String dict, String history) {
        dTrie = new DLB();
        rTrie = new UserHistory();

        try {
            File dictFile = new File(dict);
            Scanner reader = new Scanner(dictFile);

            while (reader.hasNextLine()) {
                String word = reader.nextLine();
                dTrie.add(word);
            }
            reader.close();

            File userFile = new File(history);
            Scanner userReader = new Scanner(userFile);

            while (userReader.hasNextLine()) {
                String userWord = userReader.nextLine();
                rTrie.add(userWord);
            }
            userReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Produce up to 5 suggestions based on the current word the user has
     * entered These suggestions should be pulled first from the user history
     * dictionary then from the initial dictionary. Any words pulled from user
     * history should be ordered by frequency of use. Any words pulled from
     * the initial dictionary should be in ascending order by their character
     * value ("ASCIIbetical" order).
     *
     * @param next char the user just entered
     *
     * @return ArrayList<String> List of up to 5 words prefixed by cur
     */
    public ArrayList<String> nextChar(char next) {
        ArrayList<String> finalList = new ArrayList<>();

        if (rTrie != null) {
            int rResult = rTrie.searchByChar(next);
            if (rResult != -1) {
                ArrayList<String> rList = rTrie.suggest();
                if (rList != null) {
                    int i = 0;
                    while (i < rList.size() && finalList.size() < 5) { // add rTrie suggestions to finalList
                        String user = rList.get(i);
                        finalList.add(user);
                        i++;
                    }
                }
            }
        }
        if (dTrie != null) {
            int dResult = dTrie.searchByChar(next);
            if (dResult == -1 || (rTrie != null && rTrie.count() == 0)) { // nothing in rTrie
                ArrayList<String> dList = dTrie.suggest();
                if (dList != null) {
                    finalList.addAll(dList);
                }
            }
        }

        if (finalList.size() >= 5)
            return finalList;

        if (dTrie != null) {
            ArrayList<String> dList = dTrie.suggest(); // if finalList !=5 words
            if (dList != null) {
                int i = 0;
                while (i < dList.size() && finalList.size() < 5) {
                    // add dList to finalList until final has enough || end of dList
                    String word = dList.get(i);
                    if (!finalList.contains(word)) {
                        finalList.add(word);
                    }
                    i++;
                }
            }
        }
        return finalList;
    }

    /**
     * Process the user having selected the current word
     *
     * @param cur String representing the text the user has entered so far
     */
    public void finishWord(String cur) {
        rTrie.resetByChar();
        dTrie.resetByChar();
        rTrie.add(cur);
    }

    /**
     * Save the state of the user history to a file
     *
     * @param fname String filename to write history state to
     */
    public void saveUserHistory(String fname) {
        ArrayList<String> traversal = rTrie.traverse();
        try {
            FileWriter fileWriter = new FileWriter(fname);
            for (int i = 0; i < traversal.size(); i++) {
                String word = traversal.get(i);
                fileWriter.write(word + System.lineSeparator());
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rTrie.resetByChar();
        dTrie.resetByChar();
    }

}
