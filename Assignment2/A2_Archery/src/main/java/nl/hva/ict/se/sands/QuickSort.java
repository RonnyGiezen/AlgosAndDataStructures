package nl.hva.ict.se.sands;


import java.util.Comparator;
import java.util.List;

public class QuickSort {

    private Comparator<Archer> scoringScheme;

    public  void quickSort(List<Archer> archerList, Comparator<Archer> scoringScheme){
        this.scoringScheme = scoringScheme;
        sort(archerList, 0, (archerList.size() - 1));
    }

    private void sort(List<Archer> archerList, int low, int high) {
        int i = low;
        int j = high;

        // set pivot for quick sort, the middle of the list
        Archer pivot = archerList.get(low + (high-low) / 2);

        // while low is lower or equal to length of list we loop through it
        while (i <= j) {
            // If it was an array of ints we would compare the left int item in the list with pivot
            // but here we have to check left archer with pivot archer
            while (scoringScheme.compare(archerList.get(i), pivot) < 0) {
                i++;
            }
            // If it was an array of ints we would compare the right int item in the list with pivot
            // but here we have to check right archer with pivot archer
            while (scoringScheme.compare(archerList.get(j), pivot) > 0) {
                j--;
            }
            // If we have an archer on the left that is bigger than the pivot and an archer on the right
            // that is smaller, we need to swap them.
            if (i <= j){
                Archer temp = archerList.get(i);
                archerList.set(low, archerList.get(j));
                archerList.set(high, temp);
                i++;
                j--;
            }
        }
        // Recursively sort elements before "partitioning" and after "partitioning"
        if (low < j){
            sort(archerList, low, j);
        }
        if (high > i){
            sort(archerList, i, high);
        }

    }

}
