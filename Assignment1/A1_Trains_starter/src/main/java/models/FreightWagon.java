package models;

public class FreightWagon extends Wagon {
    private final int maxWeight;

    /**
     *
     * @param wagonId id of the wagon
     * @param maxWeight mag weight that the wagon can carry
     */
    public FreightWagon(int wagonId, int maxWeight) {
        super(wagonId);
        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        return this.maxWeight;
    }
}
