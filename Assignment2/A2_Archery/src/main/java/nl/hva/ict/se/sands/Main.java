package nl.hva.ict.se.sands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main class to test the efficiency of the algorithms, excited (;
 */
public class Main {
    private static List<Archer> selectionSortArchers;
    private static List<Archer> quickSortArchers;
    private static List<Archer> collectionSortArchers;

    public static void main(String[] args) {
        Comparator<Archer> comparator = new ArcherComparator();
        // create archers
        System.out.println("Creating lists of archers for you...");
        aLotOfArchers();

        System.out.println("----------------------------------------------");
        System.out.println("Let the sorting games begin!");
        // check time spend for selection sort
        long beginTimeSelect = System.currentTimeMillis();
        ChampionSelector.selInsSort(selectionSortArchers, comparator);
        long endTimeSelect = System.currentTimeMillis();

        long totalTimeSelect = (beginTimeSelect - endTimeSelect);
        System.out.println("----------------------------------------------");
        System.out.println("Selection sort took: " + totalTimeSelect  + " milliseconds" );

        // check time spend for collection sort
        long beginTimeColl = System.currentTimeMillis();
        ChampionSelector.collectionSort(collectionSortArchers, comparator);
        long endTimeColl = System.currentTimeMillis();

        long totalTimeColl = (beginTimeColl - endTimeColl);
        System.out.println("Collection sort took: " + totalTimeColl  + " milliseconds" );

        // check time spend for quicksort
        long beginTimeQuick = System.currentTimeMillis();
        ChampionSelector.quickSort(quickSortArchers, comparator);
        long endTimeQuick = System.currentTimeMillis();

        long totalTimeQuick = (beginTimeQuick - endTimeQuick);
        // print results
        System.out.println("----------------------------------------------");
        System.out.println("Quick sort took: " + totalTimeQuick  + " milliseconds" );





    }

    /**
     * create 5000000 archers
     */
    public static void aLotOfArchers(){
        // Generate three identical lists of archers with a length og 5 mil
        for (int i = 100; i < 200; i = i * 2){
            // List one for the selection sort
            selectionSortArchers = Archer.generateArchers(i);
            // List two for the very quicksort
            quickSortArchers = new ArrayList<>(selectionSortArchers);
            // list three for the collection sort
            collectionSortArchers = new ArrayList<>(selectionSortArchers);

        }
    }

}
