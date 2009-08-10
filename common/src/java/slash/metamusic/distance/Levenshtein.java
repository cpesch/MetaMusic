/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.distance;

/**
 * Taken from: http://www.merriampark.com/ld.htm
 * <p/>
 * Levenshtein distance (distance) is a measure of the similarity between
 * two strings, which we will refer to as the source string (s) and
 * the target string (t). The distance is the number of deletions,
 * insertions, or substitutions required to transform s into t.
 * <p/>
 * For example,
 * If s is "test" and t is "test", then distance(s,t) = 0, because no
 * transformations are needed. The strings are already identical.
 * If s is "test" and t is "tent", then distance(s,t) = 1, because one
 * substitution (change "s" to "n") is sufficient to transform s
 * into t.
 * <p/>
 * The greater the Levenshtein distance, the more different the
 * strings are.
 * <p/>
 * Levenshtein distance is named after the Russian scientist
 * Vladimir Levenshtein, who devised the algorithm in 1965. If
 * you can't spell or pronounce Levenshtein, the metric is also
 * sometimes called edit distance.
 * <p/>
 * The algorithm:
 * <p/>
 * Step 	Description
 * 1 	        Set n to be the length of s.
 * Set m to be the length of t.
 * If n = 0, return m and exit.
 * If m = 0, return n and exit.
 * Construct a matrix containing 0..m rows and 0..n columns.
 * 2 	        Initialize the first row to 0..n.
 * Initialize the first column to 0..m.
 * 3 	        Examine each character of s (i from 1 to n).
 * 4       	Examine each character of t (j from 1 to m).
 * 5      	If s[i] equals t[j], the cost is 0.
 * If s[i] doesn't equals t[j], the cost is 1.
 * 6     	Set cell d[i,j] of the matrix equals to the minimum of:
 * a.         The cell immediately above plus 1: d[i-1,j] + 1.
 * b.         The cell immediately to the left plus 1: d[i,j-1] + 1.
 * c.         The cell diagonally above and to the left plus the cost: d[i-1,j-1] + cost.
 * 7 	        After the iteration steps (3, 4, 5, 6) are complete, the distance is found in cell d[n,m].
 * <p/>
 * Example
 * <p/>
 * This section shows how the Levenshtein distance is computed when the
 * source string is "GUMBO" and the target string is "GAMBOL".
 * <p/>
 * Steps 1 and 2
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1
 * A 	2
 * M 	3
 * B 	4
 * O 	5
 * L 	6
 * <p/>
 * Steps 3 to 6 When i = 1
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1 	0
 * A 	2 	1
 * M 	3 	2
 * B 	4 	3
 * O 	5 	4
 * L 	6 	5
 * <p/>
 * Steps 3 to 6 When i = 2
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1 	0 	1
 * A 	2 	1 	1
 * M 	3 	2 	2
 * B 	4 	3 	3
 * O 	5 	4 	4
 * L 	6 	5 	5
 * <p/>
 * Steps 3 to 6 When i = 3
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1 	0 	1 	2
 * A 	2 	1 	1 	2
 * M 	3 	2 	2 	1
 * B 	4 	3 	3 	2
 * O 	5 	4 	4 	3
 * L 	6 	5 	5 	4
 * <p/>
 * Steps 3 to 6 When i = 4
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1 	0 	1 	2 	3
 * A 	2 	1 	1 	2 	3
 * M 	3 	2 	2 	1 	2
 * B 	4 	3 	3 	2 	1
 * O 	5 	4 	4 	3 	2
 * L 	6 	5 	5 	4 	3
 * <p/>
 * Steps 3 to 6 When i = 5
 * G 	U 	M 	B 	O
 * 0 	1 	2 	3 	4 	5
 * G 	1 	0 	1 	2 	3 	4
 * A 	2 	1 	1 	2 	3 	4
 * M 	3 	2 	2 	1 	2 	3
 * B 	4 	3 	3 	2 	1 	2
 * O 	5 	4 	4 	3 	2 	1
 * L 	6 	5 	5 	4 	3 	2
 * <p/>
 * Step 7
 * The distance is in the lower right hand corner of the matrix,
 * i.e. 2. This corresponds to our intuitive realization that
 * "GUMBO" can be transformed into "GAMBOL" by substituting "A"
 * for "U" and adding "L" (one substitution and 1 insertion =
 * 2 changes).
 *
 * @author Christian Pesch
 * @version $Id: Levenshtein.java 911 2006-12-23 17:25:04Z cpesch $
 */

public class Levenshtein {

    /**
     * Calculate the minimum of three values
     */
    private static int minimum(int a, int b, int c) {
        int mi;

        mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {
            mi = c;
        }
        return mi;
    }

    /**
     * Compute the Levenshtein distance between s and t.
     */
    public static int distance(String s, String t) {
        // Step 1
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        // Step 2
        int[][] matrix = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            matrix[0][j] = j;
        }

        // Step 3
        int cost;
        for (int i = 1; i <= n; i++) {
            char s_i = s.charAt(i - 1);

            // Step 4
            for (int j = 1; j <= m; j++) {
                char t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                matrix[i][j] = minimum(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return matrix[n][m];
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(Levenshtein.class + " <string1> <string2>");
            System.exit(5);
        }

        System.out.println(Levenshtein.class + "#distance: " + distance(args[0], args[1]));
        System.exit(0);
    }
}

