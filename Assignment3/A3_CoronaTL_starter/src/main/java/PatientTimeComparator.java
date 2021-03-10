import java.util.Comparator;

public class PatientTimeComparator implements Comparator<Patient> {

    @Override
    public int compare(Patient o1, Patient o2) {
        if (o2.getArrivedAt().isBefore(o1.getArrivedAt())) {
            return 1;
        }
        if (o2.getArrivedAt().isAfter(o1.getArrivedAt())) {
            return -1;
        }
        return 0;
    }
}
