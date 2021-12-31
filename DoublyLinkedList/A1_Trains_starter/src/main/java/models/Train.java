package models;


import java.util.Iterator;
import java.util.NoSuchElementException;


public class Train implements Iterable {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    /**
     * @param engine      the engine/locomotive of the train
     * @param origin      the origin of the journey
     * @param destination the destination of the journey
     */
    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
        this.firstWagon = null;
    }

    /* three helper methods that are useful in other methods */
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

    /**
     * @param wagon the wagon to check
     * @return true if the wagon is on the train by checking if we can find it
     */
    public boolean isOnTrain(Wagon wagon) {
        return this.findWagonById(wagon.getId()) != null;
    }

    /**
     * @param wagon the wagon to check
     * @return true if the wagon type matches the type of the train
     */
    public boolean wagonMatchesTrainType(Wagon wagon) {
        return wagon instanceof PassengerWagon && this.isPassengerTrain() || wagon instanceof FreightWagon && this.isFreightTrain();
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param newSequence the new sequence of wagons (can be null)
     */
    public void setFirstWagon(Wagon newSequence) {
        this.firstWagon = null;
        this.firstWagon = newSequence;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (this.firstWagon == null) {
            return 0;
        }
        return this.firstWagon.getSequenceLength();
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        // if first wagon is null there are no wagons
        if (this.firstWagon == null) {
            return null;
        }
        return this.firstWagon.getLastWagonAttached();
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int seats = 0;
        // check if its a passenger wagon
        if (isPassengerTrain()) {
            // for each object of this count the number of seats
            for (Object wagon : this) {
                seats += ((PassengerWagon) wagon).getNumberOfSeats();
            }
        }
        return seats;

    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        int weight = 0;
        // check if its a freight train
        if (isFreightTrain()) {
            // for each object of this count the maximum weight
            for (Object wagon : this) {
                weight += ((FreightWagon) wagon).getMaxWeight();
            }
        }
        return weight;

    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position the position of the wagon you want to find
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        // set first wagon to set first position
        Wagon wagon = firstWagon;
        int numberOfWagonsFound = 1;

        if (firstWagon == null) return null;

        // get next wagon until position is reached
        for (int i = 1; i < position; i++) {
            if (wagon.hasNextWagon()) {
                wagon = wagon.getNextWagon();
                numberOfWagonsFound++;
            }
        }
        // if you find the wagon at the position return it
        if (numberOfWagonsFound == position) {
            return wagon;
        } else {
            return null;
        }

    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId the id of the wagon you want to find
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        if (firstWagon == null) return null;

        // loop through all wagons of this until the id matches and return the wagon
        for (Object wagon : this) {
            if (((Wagon) wagon).getId() == wagonId) {
                return ((Wagon) wagon);
            }
        }

        return null;

    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verifies of the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to pull the additional wagons
     *
     * @param sequence a wagon with possible sequence attached
     * @return true if the wagon can attach false if not
     */
    public boolean canAttach(Wagon sequence) {
        // check if the wagon is legible to attach to the train
        if (this.hasWagons() && !this.isOnTrain(sequence) && this.wagonMatchesTrainType(sequence)) {
            int maxWagons = this.getEngine().getMaxWagons();
            int waggonsAttachedToTrain = this.getFirstWagon().getSequenceLength();
            int waggonsAttachedToWagon = sequence.getSequenceLength();

            return (waggonsAttachedToTrain + waggonsAttachedToWagon) <= maxWagons;
        }
        // if the train does not have wagons return true it can be attached as the first
        return !this.hasWagons();
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param sequence a wagon with possible sequence attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon sequence) {
        // check if sequence is legible to attach
        if (this.canAttach(sequence)) {
            // if it does not have wagons set as first
            if (!this.hasWagons()) {
                this.setFirstWagon(sequence);
            } else {
                // add sequence to the last wagon
                this.firstWagon.getLastWagonAttached().setNextWagon(sequence);
                // set last wagon of train to previous of sequence wagon
                sequence.detachFromPrevious();
                sequence.setPreviousWagon(this.firstWagon.getLastWagonAttached());
            }
            return true;

        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param sequence a wagon with possible sequence attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon sequence) {
        // check if sequence is legible to attach
        if (this.canAttach(sequence)) {
            // check if the train already has wagons
            if (this.hasWagons()) {
                // set previous of first wagon to last wagon of sequence
                this.firstWagon.setPreviousWagon(sequence.getLastWagonAttached());
                // set first wagon as next wagon of the last of sequence
                sequence.getLastWagonAttached().setNextWagon(this.firstWagon);


            }
            // if the train does not have any wagons set sequence as first
            this.setFirstWagon(sequence);
            return true;
        }

        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     *
     * @param sequence a wagon with possible sequence attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon sequence) {
        // check if wagon can be attached and the position is possible in the length of the train
        if (this.canAttach(sequence) && (position <= this.getNumberOfWagons() || position == 1 && !this.hasWagons())) {
            // set temp wagon to find wagon you have to move
            Wagon temp = this.findWagonAtPosition(position);
            // if there are no wagons set as first
            if (!this.hasWagons()) {
                this.setFirstWagon(sequence);
            } else if (temp != null) {
                // set previous of "pivot" wagon to previous of new sequence
                sequence.setPreviousWagon(temp.getPreviousWagon());
                // detach the temp wagon from previous
                temp.detachFromPrevious();
                // set the temp back at the back of the sequence
                sequence.getLastWagonAttached().setNextWagon(temp);
                // set previous of the temp to last wagon of the new sequence
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
     *
     * @param wagonId id of the wagon you want to move
     * @param toTrain the train you want to move the wagon to
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // first find the wagon to remove
        Wagon toRemove = this.findWagonById(wagonId);
        // check if the wagon is found (not null) and is legible to attach to new train
        if (toRemove != null && toTrain.canAttach(toRemove)) {
            // remove the wagon
            toRemove.removeFromSequence();
            // attach at the back of the new train
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
     *
     * @param position the position where you want to split the sequence from the train
     * @param toTrain  the train where you want to add the new sequence
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // check if there are wagons on the train
        if (this.getNumberOfWagons() != 0) {
            // get the wagon from where you want to split
            Wagon toRemove = this.findWagonAtPosition(position);
            // check if the wagon is there and if it can attach to the new train
            if (this.isOnTrain(toRemove) && toTrain.canAttach(toRemove)) {
                // remove the tail of the previous wagon, detach it
                this.findWagonAtPosition(position - 1).detachTail();
                // attach at the rear of the new train
                toTrain.attachToRear(toRemove);
                return true;

            }
        }
        return false;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // check if there are wagon and if there are more than one
        if (this.firstWagon != null && this.firstWagon.hasNextWagon()) {
            // reverse the sequence and attach new "head" as first wagon
            this.setFirstWagon(this.firstWagon.reverseSequence());
        }
    }

    @Override
    public Iterator<Wagon> iterator() {
        // return the iterator of wagon
        return new Iterator<>() {
            // set current wagon as the first wagon of the iteration
            Wagon current = firstWagon;

            // Return true if the current wagon has a next wagon
            @Override
            public boolean hasNext() {
                return current != null;
            }

            // Sets current wagon to the next wagon and returns the current wagon
            @Override
            public Wagon next() throws NoSuchElementException {
                Wagon wagon = current;
                current = current.getNextWagon();
                return wagon;
            }
        };
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(engine.toString());
        Wagon nextWagon = this.getFirstWagon();
        while (nextWagon != null) {
            builder.append(nextWagon.toString());
            nextWagon = nextWagon.getNextWagon();
        }
        builder.append(String.format(" with %d wagons from %s to %s", getNumberOfWagons(), origin, destination));
        return builder.toString();
    }

}
