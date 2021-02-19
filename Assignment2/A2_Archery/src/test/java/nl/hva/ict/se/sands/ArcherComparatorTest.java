package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ArcherComparatorTest {
    protected Comparator<Archer> comparator;

    @BeforeEach
    public void createComparator() {
        // Instantiate your own comparator here...
        // comparator = new .....();
        comparator = new ArcherComparator();
    }

    @Test
    void compare() {
        // create archers
        Archer loserOnPoints = new Archer("Loser", "Points");
        Archer winnerOnPoints = new Archer("Winner", "Points");
        Archer loserOnTens = new Archer("Loser", "Tens");
        Archer winnerOnTens = new Archer("Winner", "Tens");
        Archer winnerOnNines = new Archer("Winner", "Nines");
        Archer loserOnNines = new Archer("Winner", "Nines");
        Archer winnerOnID = new Archer("Winner", "Id");
        Archer loserOnID = new Archer("Loser", "Id");

        int[] points = new int[3];
        int[] tens = new int[3];
        int[] nines = new int[3];

        points[0] = 8;
        tens[0] = 10;
        nines[0] = 9;

        // insert points to winners and compare on point
        winnerOnPoints.registerScoreForRound(1, tens);
        loserOnPoints.registerScoreForRound(1, points);
        assertEquals(2, comparator.compare(loserOnPoints, winnerOnPoints));

        points[1] = 2;

        winnerOnTens.registerScoreForRound(1, tens);
        loserOnTens.registerScoreForRound(1, points);
        assertEquals(1, comparator.compare(loserOnTens, winnerOnTens));

        points[2] = 8;
        nines[1] = 9;

        winnerOnNines.registerScoreForRound(1, nines);
        loserOnNines.registerScoreForRound(1, points);
        assertEquals(2, comparator.compare(loserOnNines, winnerOnNines));

        winnerOnID.registerScoreForRound(1, tens);
        winnerOnID.registerScoreForRound(2, nines);
        loserOnID.registerScoreForRound(1, tens);
        loserOnID.registerScoreForRound(2, nines);
        assertEquals(1, comparator.compare(loserOnID, winnerOnID));



    }
}
