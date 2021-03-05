package models;

public abstract class Wagon {
    protected int id;                 // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    /**
     * @param wagonId the if of a wagon
     */
    protected Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return this.nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return this.previousWagon != null;
    }

    /**
     * finds the last wagon of the sequence of wagons attached to this wagon
     * if no wagons are attached return this wagon selves
     *
     * @return the wagon found
     */
    public Wagon getLastWagonAttached() {
        Wagon lastWagon = this;
        // loop while wagon has next, stops if it does not
        while (lastWagon.hasNextWagon()) {
            lastWagon = lastWagon.nextWagon;
        }
        return lastWagon;
    }

    /**
     * @return the length of the sequence of wagons starting with this wagon
     * return 1 if no wagons have been attached to this wagon.
     */
    public int getSequenceLength() {
        int sequence = 1;
        // loop through until wagon has no next anymore count all wagons
        if (this.hasNextWagon()) {
            sequence += this.nextWagon.getSequenceLength();
        }
        return sequence;
    }

    /**
     * attaches this wagon at the tail of a given prevWagon.
     *
     * @param newPreviousWagon the new previousWagon
     * @throws RuntimeException if this wagon already has been appended to a wagon.
     * @throws RuntimeException if prevWagon already has got a wagon appended.
     */
    public void attachTo(Wagon newPreviousWagon) {
        // verify the exceptions
        if (newPreviousWagon.hasNextWagon()) {
            String msg = "this wagon has already been appended to another wagon";
            throw new IllegalArgumentException(msg);
        }
        if (this.hasPreviousWagon()) {
            String msg = "This wagon already has a predecessor";
            throw new IllegalArgumentException(msg);
        }
        // attach this wagon to its new predecessor (sustaining the invariant propositions).
        this.previousWagon = newPreviousWagon;
        this.previousWagon.setNextWagon(this);
    }

    /**
     * detaches this wagon from its previous wagons.
     * no action if this wagon has no previous wagon attached.
     */
    public void detachFromPrevious() {
        // detach this wagon from its predecessors (sustaining the invariant propositions).
        if (this.hasPreviousWagon()) {
            this.previousWagon.nextWagon = null;
            this.previousWagon = null;
        }
    }

    /**
     * detaches this wagon from its tail wagons.
     * no action if this wagon has no succeeding next wagon attached.
     */
    public void detachTail() {
        // detach this wagon from its successors (sustaining the invariant propositions).
        if (this.hasNextWagon()) {
            this.nextWagon.previousWagon = null;
            this.nextWagon = null;
        }
    }

    /**
     * attaches this wagon at the tail of a given newPreviousWagon.
     * if required, first detaches this wagon from its current predecessor
     * and/or detaches the newPreviousWagon from its current successor
     *
     * @param newPreviousWagon reattach the wagon to the new previous wagon
     */
    public void reAttachTo(Wagon newPreviousWagon) {
        // detach previous wagon from this en detach this from previous wagon
        this.previousWagon.nextWagon = null;
        this.previousWagon = null;
        // attach this wagon to its new predecessor (sustaining the invariant propositions).
        newPreviousWagon.nextWagon = this;
        this.previousWagon = newPreviousWagon;
    }

    /**
     * Removes this wagon from the sequence that it is part of, if any.
     * Reconnect the subsequence of its predecessors with the subsequence of its successors, if any.
     */
    public void removeFromSequence() {
        if (this.nextWagon != null) {
            this.nextWagon.previousWagon = this.previousWagon;
        }
        if (this.previousWagon != null) {
            this.previousWagon.nextWagon = this.nextWagon;
        }
        this.previousWagon = null;
        this.nextWagon = null;
    }


    /**
     * reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the predecessor of this Wagon, if any.
     * no action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // only reverses all wagons....
        if (this.previousWagon != null) {
            this.previousWagon.nextWagon = reverseRecursive(this);
            reverseRecursive(this).previousWagon = this.previousWagon;
        }
        return reverseRecursive(this);
    }

    /**
     * @param sequence the sequence of wagons you want to reverse
     * @return the new "head" of the sequence
     */
    public static Wagon reverseRecursive(Wagon sequence) {
        if (sequence == null) {
            return null;
        }

        if (sequence.nextWagon == null) {
            sequence.previousWagon = null;
            return sequence;
        }

        Wagon newHead = reverseRecursive(sequence.nextWagon);
        sequence.nextWagon.nextWagon = sequence;
        sequence.previousWagon = sequence.nextWagon;
        sequence.nextWagon = null;

        return newHead;
    }

    @Override
    public String toString() {
        return String.format("[Wagon-%d]", id);
    }
}
