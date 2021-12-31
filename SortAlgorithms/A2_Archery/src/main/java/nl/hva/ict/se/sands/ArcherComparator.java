package nl.hva.ict.se.sands;

import java.util.Comparator;

public class ArcherComparator implements Comparator<Archer> {

    /**
     *
     * @param o1 Archer one
     * @param o2 Archer two
     * @return score
     */
    @Override
    public int compare(Archer o1, Archer o2) {
        // check if score is the same, if not we can directly return the score
        int score = totalScore(o1, o2);

        // Now we really check it, and check if the amount of 10's is the same
        if (score == 0) {
            score = checkTens(o1, o2);
            // If the amount of 10's is the same we need to check the 9's
            if (score == 0) {
                score = checkNines(o1, o2);
                // then if the 9's are also the same we check experience based on ID
                if (score == 0) {
                    score = checkId(o1, o2);
                }
            }
        }
        return score;
    }

    /**
     *
     * @param o1 Archer one
     * @param o2 Archer two
     * @return difference between scores
     */
    public int totalScore(Archer o1, Archer o2){
        return o2.getTotalScore() - o1.getTotalScore();
    }

    /**
     *
     * @param o1 Archer one
     * @param o2 Archer two
     * @return the difference between amount of tens
     */
    public int checkTens(Archer o1, Archer o2) {
        return o2.getTens() - o1.getTens();
    }

    /**
     *
     * @param o1 Archer one
     * @param o2 Archer two
     * @return the difference between amount of nines
     */
    public int checkNines(Archer o1, Archer o2) {
        return o2.getNines() - o1.getNines();
    }

    /**
     *
     * @param o1 Archer one
     * @param o2 Archer two
     * @return 1 if ID of archer 1 is lower that archer 2 (older) else -1
     */
    public int checkId(Archer o1, Archer o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }

}
