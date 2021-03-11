import java.util.Comparator;

public class PatientPrioComparator implements Comparator<Patient> {

    @Override
    public int compare(Patient o1, Patient o2) {
        if (o2.isHasPriority() && !o1.isHasPriority()) {
            return 1;
        }
        if (!o2.isHasPriority() && o1.isHasPriority()) {
            return -1;
        }
        return 0;
    }
}
