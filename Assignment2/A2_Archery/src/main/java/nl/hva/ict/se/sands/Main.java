package nl.hva.ict.se.sands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main class to test the efficiency of the algorithms, excited (;
 */
public class Main {

    public static void main(String[] args) {
        // set comparator
        Comparator<Archer> comparator = new ArcherComparator();

        System.out.println("Let the sorting games begin!");
        System.out.println("-----------------------------------------");
        //  create 3 identical list of archers
        // loop over them, check sorting by starting at 200
        // duplicate every time till 5mil
        for (int i = 10; i < 20; i = i*2) {
            List<Archer> archerListSelection = Archer.generateArchers(i);
            List<Archer> archerListQuick = new ArrayList<>(archerListSelection);
            List<Archer> archerListCollection = new ArrayList<>(archerListSelection);

            long startTimeSel = System.currentTimeMillis();
            ChampionSelector.selInsSort(archerListSelection, comparator);
            long endTimeSel = System.currentTimeMillis();

            long startTimeQuick = System.currentTimeMillis();
            ChampionSelector.quickSort(archerListQuick, comparator);
            long endTimeQuick = System.currentTimeMillis();

            long startTimeCol = System.currentTimeMillis();
            ChampionSelector.collectionSort(archerListCollection, comparator);
            long endTimeCol = System.currentTimeMillis();

            // calculating the time for each sorting alg.
            long seleTime = startTimeSel - endTimeSel ;
            long quickTime = startTimeQuick - endTimeQuick;
            long sortTime = startTimeCol - endTimeCol;

            // print the soring games
            System.out.println("Amount of archers to sort: " + i
                    + "\n" + "Selection sort in milliseconds: " + seleTime
                    + "\n" + "Quicksort in milliseconds: " + quickTime
                    + "\n" + "collection sort in milliseconds: " + sortTime);
            System.out.println("-----------------------------------------");
        }


    }

}
