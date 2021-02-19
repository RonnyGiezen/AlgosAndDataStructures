package nl.hva.ict.se.sands;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArcherTest {

    @Test
    void archerIdsIncreaseCorrectly() {
        List<Archer> archers = Archer.generateArchers(3);
        assertEquals(archers.get(0).getId() + 1, archers.get(1).getId());
        assertEquals(archers.get(1).getId() + 1, archers.get(2).getId());
    }

    @Test
    void visibilityOfConstructorsShouldBeUnchanged() {
        for (Constructor constructor : Archer.class.getDeclaredConstructors()) {
            assertTrue((constructor.getModifiers() & 0x00000004) != 0);
        }
    }

    @Test
    void idFieldShouldBeUnchangeable() throws NoSuchFieldException {
        assertTrue((Archer.class.getDeclaredField("id").getModifiers() & 0x00000010) != 0);
    }

    @Test
    void registerScoreForRound() {
        Archer archer = new Archer("Ronny", "Giezen");
        int[] points = new int[3];
        // before points added should be 0
        assertEquals(0, archer.getTotalScore());
        // add points
        points[0] = 10;
        archer.registerScoreForRound(1,points);
        // should be 10
        assertEquals(10, archer.getTotalScore());

    }

    /**
     * The test above al
     */
    @Test
    void getTotalScore() {
        Archer archer = new Archer("Ronny", "Giezen");
        int[] points = new int[3];
        // before points added should be 0
        assertEquals(0, archer.getTotalScore());
        // add points
        points[0] = 10;
        archer.registerScoreForRound(1,points);
        // should be 10
        assertEquals(10, archer.getTotalScore());
        // add more point to see if it adds up
        archer.registerScoreForRound(2, points);
        assertEquals(20, archer.getTotalScore());
    }

    @Test
    void getTens() {
        Archer archer = new Archer("Ronny", "Giezen");
        int[] points = new int[3];
        points[0] = 10;
        archer.registerScoreForRound(1,points); // points is 10
        // should be 10
        assertEquals(1, archer.getTens());

        // add more point to see if it adds up
        archer.registerScoreForRound(2, points); // points is 20
        assertEquals(2, archer.getTens());

        points[1] = 9;
        archer.registerScoreForRound(3, points); // points is 39
        assertEquals(3, archer.getTens());
    }

    @Test
    void getNines() {
        Archer archer = new Archer("Ronny", "Giezen");
        int[] points = new int[3];
        points[0] = 9;
        archer.registerScoreForRound(1,points); // points is 9
        // should be 10
        assertEquals(1, archer.getNines());

        // add more point to see if it adds up
        archer.registerScoreForRound(2, points); // points is 18
        assertEquals(2, archer.getNines());

        points[1] = 10;
        archer.registerScoreForRound(3, points); // points is 38
        assertEquals(3, archer.getNines());
    }

    @Test
    void testToString() {
    }

    // TODO write more test to cover whole Archer class

}
