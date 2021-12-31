package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChampionSelectorTest {
    protected Comparator<Archer> comparator;

    @BeforeEach
    public void createComparator() {
        // Instantiate your own comparator here...
        // comparator = new .....();
        comparator = new ArcherComparator();
    }

    @Test
    public void selInsSortQuickSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersForSelIns = Archer.generateArchers(23);
        List<Archer> unsortedArchersForCollection = new ArrayList<>(unsortedArchersForSelIns);
        List<Archer> unsortedArchersForQuickSort = new ArrayList<>(unsortedArchersForSelIns);

        List<Archer> sortedArchersSelIns = ChampionSelector.selInsSort(unsortedArchersForSelIns, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(unsortedArchersForCollection, comparator);
        List<Archer> sortedArchersQuick = ChampionSelector.collectionSort(unsortedArchersForQuickSort, comparator);

        assertEquals(sortedArchersCollection, sortedArchersSelIns);
        assertEquals(sortedArchersCollection, sortedArchersQuick);
        assertEquals(sortedArchersSelIns, sortedArchersQuick);
    }


}
