package nl.hva.ict.se.sands;

import java.util.Comparator;
import java.util.List;

/**
 * Given a list of Archer's this class can be used to sort the list using one of three sorting algorithms.
 * Note that you are NOT allowed to change the signature of these methods! Adding method is perfectly fine.
 */
public class ChampionSelector {
    /**
     * This method uses either selection sort or insertion sort for sorting the archers.
     * I used selection sort
     */
    public static List<Archer> selInsSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        int archersLength = archers.size();

        for (int i = 0; i < archersLength - 1; i++) {
            // Find minimum element in unsorted array
            int minIndex = i;
            // loop through the list of archers
            for (int j = i + 1; j < archersLength; j++) {
                // check current archer against next archer in the list
                if (scoringScheme.compare(archers.get(minIndex), archers.get(j)) > 0) {
                    // if archer o2 is better set new index
                    minIndex = j;
                }
            }
            // swap the found minimum element with the first element
            Archer tempBest = archers.get(minIndex);
            archers.set(minIndex, archers.get(i));
            archers.set(i, tempBest);
        }
        return archers;
    }

    /**
     * This method uses quick sort for sorting the archers.
     */
    public static List<Archer> quickSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        // create instance of quicksort class and use the quickSort method to sort the list
        QuickSort quickSort = new QuickSort();
        quickSort.quickSort(archers, scoringScheme);
        return archers;
    }

    /**
     * This method uses the Java collections sort algorithm for sorting the archers.
     */
    public static List<Archer> collectionSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        // using the Collections.sort() method and passing in an comparator
        archers.sort(scoringScheme);
        return archers;
    }
}
