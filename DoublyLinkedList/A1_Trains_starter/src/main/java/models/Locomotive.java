package models;

public class Locomotive {
    private final int locNumber;
    private final int maxWagons;

    /**
     *
     * @param locNumber the number/id of a locomotive
     * @param maxWagons the amount of wagons a locomotive can take
     */
    public Locomotive(int locNumber, int maxWagons) {
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    public int getMaxWagons() {
        return maxWagons;
    }

    @Override
    public String toString() {
        return String.format("[Loc-%d]", locNumber);
    }
}
