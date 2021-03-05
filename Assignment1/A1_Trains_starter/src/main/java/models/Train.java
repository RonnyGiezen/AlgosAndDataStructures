package models;


import java.util.Iterator;


public class Train implements Iterable {
    private String origin;
    private String destination;
    private Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are usefull in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    // check if wagon is already attached to the train
    public boolean isOnTrain(Wagon wagon) {
        return this.findWagonById(wagon.getId()) != null;
    }

    // check if wagon is of the same type as the train
    public boolean wagonMatchesTrainType(Wagon wagon){
        return wagon instanceof PassengerWagon && this.isPassengerTrain() || wagon instanceof FreightWagon && this.isFreightTrain();
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     * @param newSequence   the new sequence of wagons (can be null)
     */
    public void setFirstWagon(Wagon newSequence) {
        this.firstWagon = null;
        this.firstWagon = newSequence;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (this.firstWagon == null) {
            return 0;
        }
        return this.firstWagon.getSequenceLength();
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (this.firstWagon == null) {
            return null;
        }
        return this.firstWagon.getLastWagonAttached();
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int seats = 0;

        if (isPassengerTrain()) {
            for (Object wagon : this) {
                seats += ((PassengerWagon) wagon).getNumberOfSeats();
            }
        }
        return seats;

    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        int weight = 0;

        if (isFreightTrain()) {
            for (Object wagon : this) {
                weight += ((FreightWagon) wagon).getMaxWeight();
            }
            return weight;
        }
        return weight;

    }

     /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon currentWagon = firstWagon;
        int numberOfWagonsFound = 1;

        if(firstWagon == null) return null;

        for (int i = 1; i < position; i++) {
            if (currentWagon.hasNextWagon()) {
                currentWagon = currentWagon.getNextWagon();
                numberOfWagonsFound++;
            }
        }

        if (numberOfWagonsFound == position) {
            return currentWagon;
        } else {
            return null;
        }

    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        int position = 1;

        if (firstWagon == null) return null;

        for (Object wagon : this) {
            if (((Wagon) wagon).getId() == wagonId) {
                return ((Wagon) wagon);
            }
            position++;
        }

        return null;

    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     * @param sequence
     * @return
     */
    public boolean canAttach(Wagon sequence) {
        if (this.hasWagons() && !this.isOnTrain(sequence) && this.wagonMatchesTrainType(sequence)){
            int maxWagons = this.getEngine().getMaxWagons();
            int waggonsAttachedToTrain = this.getFirstWagon().getSequenceLength();
            int waggonsAttachedToWagon = sequence.getSequenceLength();

            return (waggonsAttachedToTrain + waggonsAttachedToWagon) <= maxWagons;
        }
        return !this.hasWagons();
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon sequence) {
        if (this.canAttach(sequence)){
            if (!this.hasWagons()) {
                this.setFirstWagon(sequence);
            }
            else {
                this.firstWagon.getLastWagonAttached().setNextWagon(sequence);
            }
            return true;

        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon sequence) {
        if (this.canAttach(sequence)){
            if (!this.hasWagons()){
                this.setFirstWagon(sequence);
            }
            else {
                this.firstWagon.setPreviousWagon(sequence.getLastWagonAttached());
                sequence.getLastWagonAttached().setNextWagon(this.firstWagon);
                this.setFirstWagon(sequence);


            }
            return true;
        }

        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     * @param sequence
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon sequence) {
        if (this.canAttach(sequence) && (position <= this.getNumberOfWagons() || position == 1 && !this.hasWagons())){
            Wagon temp = this.findWagonAtPosition(position);

            if (!this.hasWagons()){
                this.setFirstWagon(sequence);
            }
            else if (temp != null){
                sequence.setPreviousWagon(temp.getPreviousWagon());
                temp.detachFromPrevious();
                sequence.getLastWagonAttached().setNextWagon(temp);
                temp.setPreviousWagon(sequence.getLastWagonAttached());
            }
            return true;
        }

        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO
        Wagon toRemove = this.findWagonById(wagonId);
        if (toRemove != null && toTrain.canAttach(toRemove)) {
            toRemove.removeFromSequence();
            toTrain.attachToRear(toRemove);
            return true;
        }
        return false;
     }

    /**
     * Tries to split this train and move the complete sequence of wagons from the given position
     * to the rear of toTrain
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        if (this.getNumberOfWagons() != 0) {
            Wagon toRemove = this.findWagonAtPosition(position);
            if (this.isOnTrain(toRemove) && toTrain.canAttach(toRemove)) {
                this.findWagonAtPosition(position - 1).detachTail();
                toTrain.attachToRear(toRemove);
                return true;

            }
        }

        return false;


    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    @Override
    public Iterator<Wagon> iterator() {
        return new Iterator<Wagon>() {

            Wagon current = firstWagon;

            // Will return true if the current wagon in the iteration is not null
            @Override
            public boolean hasNext() {
                return current != null;
            }

            // Sets current wagon to the next wagon and returns the current wagon
            @Override
            public Wagon next() {
                Wagon wagon = current;
                current = current.getNextWagon();
                return wagon;
            }
        };
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(engine.toString());
        Wagon next = this.getFirstWagon();
        while (next != null) {
            result.append(next.toString());
            next = next.getNextWagon();
        }
        result.append(String.format(" with %d wagons and %d seats from %s to %s", getNumberOfWagons(), getTotalNumberOfSeats(), origin, destination));
        return result.toString();
    }

}
