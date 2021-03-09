import java.util.Comparator;

public class PatientComparator implements Comparator<Patient> {

    @Override
    public int compare(Patient o1, Patient o2) {
        if (o2.getArrivedAt().isBefore(o2.getArrivedAt())) {
            return -1;
        }
        if (o2.isHasPriority() && !o1.isHasPriority()){
            return -1;
        }
        return 0;
    }
}
