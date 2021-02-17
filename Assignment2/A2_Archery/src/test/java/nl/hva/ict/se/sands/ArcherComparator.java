package nl.hva.ict.se.sands;

import java.util.Comparator;

public class ArcherComparator implements Comparator<Archer> {

    @Override
    public int compare(Archer o1, Archer o2) {
        // check if score is the same, if not we can directly return the score
        int score = o2.getTotalScore() - o1.getTotalScore();

        // Now we really check it, and check if the amount of 10's is the same
        if (score == 0) {
            score = o2.getTens() - o1.getTens();
            // If the amount of 10's is the same we need to check the 9's
            if (score == 0) {
                score = o2.getNines() - o1.getNines();
                // then if the 9's are also the same we check experience based on ID
                if (score == 0) {
                    score = o2.getId() - o1.getId();
                }
            }
        }
        return score;
    }
}
