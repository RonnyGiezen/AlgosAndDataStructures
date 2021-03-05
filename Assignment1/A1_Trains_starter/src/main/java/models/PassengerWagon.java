package models;

public class PassengerWagon extends Wagon {
    private final int numberOfSeats;

    /**
     *
     * @param wagonId id of a wagon
     * @param numberOfSeats number of seats in the wagon
     */
    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return this.numberOfSeats;
    }
}
