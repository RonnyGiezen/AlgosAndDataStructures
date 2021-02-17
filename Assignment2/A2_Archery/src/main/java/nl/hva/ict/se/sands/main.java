package nl.hva.ict.se.sands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main class to test the efficiency of the algorithms, excited (;
 */
public class main {
    public static void main(String[] args) {
        // Get the comparator ready
        final Comparator<Archer> comparator = new ArcherComparator();


        // Generate three identical lists of archers with a length og 5 mil
        for (int i = 100; i < 5000000; i = i * 2){
            // List one for the selectionsort
            List<Archer> selectionSortArchers = Archer.generateArchers(i);
            // List two for the very quicksort
            List<Archer> quickSortArchers = new ArrayList<>(selectionSortArchers);
            // list three for the collectionsort
            List<Archer> collectionSortArchers = new ArrayList<>(selectionSortArchers);

            // check time spend for selectionsort
            long beginTimeSelect = System.currentTimeMillis();
            ChampionSelector.selInsSort(selectionSortArchers, comparator);
            long endTimeSelect = System.currentTimeMillis();

            // check time spend for quicksort
            long beginTimeQuick = System.currentTimeMillis();
            ChampionSelector.quickSort(selectionSortArchers, comparator);
            long endTimeQuick = System.currentTimeMillis();

            // check time spend for collectionsort
            long beginTimeColl = System.currentTimeMillis();
            ChampionSelector.collectionSort(selectionSortArchers, comparator);
            long endTimeColl = System.currentTimeMillis();

            // print the time spend
        }




    }

}
