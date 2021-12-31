package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ArcherComparatorTest {
    protected Comparator<Archer> comparator;
    protected ArcherComparator testComparator;
    protected Archer winner;
    protected Archer loser;
    protected int[] pointsWinner;
    protected int[] pointsLoser;

    @BeforeEach
    public void createComparator() {
        // Instantiate your own comparator here...
        // comparator = new .....();
        comparator = new ArcherComparator();
    }

    @BeforeEach
    public void createTestObjects() {
        testComparator = new ArcherComparator();
        winner = new Archer("Winner", "Winner chickendinner");
        loser = new Archer("Loser", "Lost");
        pointsWinner = new int[3];
        pointsLoser = new int[3];
    }

    @Test
    void compare() {
        int[] points = new int[3];
        int[] tens = new int[3];
        int[] nines = new int[3];

        points[0] = 8;
        tens[0] = 10;
        nines[0] = 9;

        // insert points to winners and compare on point
        winner.registerScoreForRound(1, tens);
        loser.registerScoreForRound(1, points);
        assertEquals(2, comparator.compare(loser, winner));

        points[1] = 2;

        winner.registerScoreForRound(1, tens);
        loser.registerScoreForRound(1, points);
        assertEquals(1, comparator.compare(loser, winner));

        points[2] = 8;
        nines[1] = 9;

        winner.registerScoreForRound(1, nines);
        loser.registerScoreForRound(1, points);
        assertEquals(2, comparator.compare(loser, winner));

        winner.registerScoreForRound(1, tens);
        winner.registerScoreForRound(2, nines);
        loser.registerScoreForRound(1, tens);
        loser.registerScoreForRound(2, nines);
        assertEquals(1, comparator.compare(loser, winner));



    }

    @Test
    void totalScore() {
        pointsWinner[0] = 5;
        pointsLoser[0] = 4;
        winner.registerScoreForRound(1, pointsWinner);
        loser.registerScoreForRound(1, pointsLoser);
        assertEquals(1, testComparator.totalScore(loser, winner));
    }

    @Test
    void checkTens() {
        pointsWinner[0] = 5;
        pointsWinner[1] = 10;
        pointsLoser[0] = 9;
        pointsLoser[1] = 6;
        winner.registerScoreForRound(1, pointsWinner);
        loser.registerScoreForRound(1, pointsLoser);
        assertEquals(1, testComparator.checkTens(loser, winner));
    }

    @Test
    void checkNines() {
        pointsWinner[0] = 9;
        pointsWinner[1] = 9;
        pointsLoser[0] = 5;
        pointsLoser[1] = 4;
        pointsLoser[2] = 9;
        winner.registerScoreForRound(1, pointsWinner);
        loser.registerScoreForRound(1, pointsLoser);
        assertEquals(1, testComparator.checkNines(loser, winner));
    }

    @Test
    void checkId() {
        assertEquals(1, testComparator.checkId(loser, winner));
    }
}
