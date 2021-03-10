import java.util.Comparator;

public class NurseComparator implements Comparator<Nurse> {

    @Override
    public int compare(Nurse o1, Nurse o2) {
        if (o2.getAvailableAt().isBefore(o1.getAvailableAt())){
            return -1;
        }
        if (o2.getAvailableAt().isAfter(o1.getAvailableAt())){
            return 1;
        }
        return 0;
    }
}
