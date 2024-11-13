package com.DC.utilities;

import java.util.LinkedList;
import java.util.Objects;

public class DiffUtility {
    public static LinkedList<Diff> compare(String text1, String text2) {
        if (text1 == null || text2 == null) {
            throw new IllegalArgumentException("Null inputs. (diff_main)");
        }

        // Split input strings into arrays of words
        String[] originalWords = text1.split("\\s+");
        String[] newWords = text2.split("\\s+");

        // Initialize a 2D array to store the lengths of longest common subsequences
        int[][] dp = new int[originalWords.length + 1][newWords.length + 1];

        // Fill the DP array
        for (int i = 1; i <= originalWords.length; i++) {
            for (int j = 1; j <= newWords.length; j++) {
                if (originalWords[i - 1].equals(newWords[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Trace back to find differences
        LinkedList<Diff> diffs = new LinkedList<>();
        int i = originalWords.length, j = newWords.length;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && originalWords[i - 1].equals(newWords[j - 1])) {
                // Words are equal
                diffs.addFirst(new Diff(Operation.EQUAL, originalWords[i - 1]));
                i--;
                j--;
            } else if (j > 0 && (i == 0 || dp[i][j - 1] >= dp[i - 1][j])) {
                // Insertion
                diffs.addFirst(new Diff(Operation.INSERT, newWords[j - 1]));
                j--;
            } else if (i > 0 && (j == 0 || dp[i][j - 1] < dp[i - 1][j])) {
                // Deletion
                diffs.addFirst(new Diff(Operation.DELETE, originalWords[i - 1]));
                i--;
            }
        }

        return diffs;
    }

    public enum Operation {
        DELETE, INSERT, EQUAL
    }

    public static class Diff {
        public Operation operation;
        public String text;

        public Diff(Operation operation, String text) {
            this.operation = operation;
            this.text = text;
        }

        @Override
        public String toString() {
            String prettyText = this.text.replace('\n', '\u00b6');
            return "Diff(" + this.operation + ",\"" + prettyText + "\")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Diff)) return false;
            Diff diff = (Diff) o;
            return operation == diff.operation && Objects.equals(text, diff.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, text);
        }
    }
}
