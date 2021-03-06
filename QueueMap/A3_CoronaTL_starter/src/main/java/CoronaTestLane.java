import java.time.LocalTime;
import java.util.*;

public class CoronaTestLane {

    private List<Patient> patients;     // all patients visiting the test lane today
    private List<Nurse> nurses;         // all nurses working at the test lane today
    private final LocalTime openingTime;      // start time of sampling at the test lane today
    private final LocalTime closingTime;      // latest time of possible arrivals of patients
    // hereafter, nurses will continue work until the queue is empty

    // simulation statistics for reporting
    private int maxQueueLength;             // the maximum queue length of waiting patients at any time today
    private int maxRegularWaitTime;         // the maximum wait time of regular patients today
    private int maxPriorityWaitTime;        // the maximum wait time of priority patients today
    private double averageRegularWaitTime;  // the average wait time of regular patients today
    private double averagePriorityWaitTime; // the average wait time of priority patients today
    private LocalTime workFinished;         // the time when all nurses have finished work with no more waiting patients

    // added for stats by Ronny
    private double totalRegularWaitTime; // the total queue length for regular patients
    private double totalPriorityWaitTime; // the total queue length for prio patients
    private int totalRegularPatients; // total amount of regular patients
    private int totalPriorityPatients; // total amount of priority patients

    private Random randomizer;              // used for generation of test data and to produce reproducible simulation results

    private final Comparator<Patient> timeComparator = new PatientTimeComparator();
    private final Comparator<Patient> prioComparator = new PatientPrioComparator();
    private final Comparator<Nurse> nurseComparator = new NurseComparator();

    /**
     * Instantiates a corona test line for a given day of work
     *
     * @param openingTime start time of sampling at the test lane today
     * @param closingTime latest time of possible arrivals of patients
     */
    public CoronaTestLane(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.workFinished = openingTime;
        this.randomizer = new Random(0);
        System.out.printf("%nCorona test lane simulation between %s and %s%n%n", openingTime, closingTime);
    }

    /**
     * Simulate a day at the Test Lane
     *
     * @param numNurses        the number of nurses that shall be scheduled to work in parallel
     * @param numPatients      the number of patient profiles that shall be generated to visit the Test Lane today
     * @param priorityFraction the fraction of patients that shall be given priority
     *                         and will be allowed to skip non-priority patients on the waiting queue
     * @param seed             used to initialize a randomizer to generate reproducible semi-random data
     */
    public void configure(int numNurses, int numPatients, double priorityFraction, long seed) {
        randomizer = new Random(seed);
        System.out.printf("Configuring test lane with %d nurse(s) and %d patients (%.0f%% priority); seed=%d.%n",
                numNurses, numPatients, 100 * priorityFraction, seed);

        // Configure the nurses
        nurses = new ArrayList<>();
        for (int n = 0; n < numNurses; n++) {
            nurses.add(new Nurse("Nurse-" + (n + 1), openingTime, randomizer));
        }

        // Generate the full list of patients that will be arriving at the test lane (and show a few)
        patients = new ArrayList<>();
        for (int p = 0; p < numPatients; p++) {
            patients.add(new Patient(openingTime, closingTime, priorityFraction, randomizer));
        }

        // echo some patients for runtime confirmation
        if (patients.size() > 2) {
            System.out.printf("   a few patients: %s - %s - %s - ...%n", patients.get(0), patients.get(1), patients.get(2));
        }
    }

    /**
     * Simulate a day at the Test Lane and calculate the relevant statistics from this simulation
     */
    public void simulate() {

        System.out.printf("Simulating the sampling of %d patients by %d nurse(s).%n",
                patients.size(), nurses.size());

        // interleaved by nurses inviting patients from the waiting queue to have their sample taken from their nose...

        // maintain the patients queue by priority and arrival time
        // This priority queue needs a proper way of determining the priority for the patients
        // prioritizes on the available time of the nurse, first available is first in queue
        Queue<Patient> waitingPatients = new PriorityQueue<>(this.prioComparator);

        // reset availability of the nurses
        for (Nurse nurse : nurses) {
            nurse.setAvailableAt(openingTime);
            nurse.setNumPatientsSampled(0);
            nurse.setTotalSamplingTime(0);
        }

        // resetting statistics data for every test run (added by Ronny)
        this.totalRegularPatients = 0;
        this.totalPriorityPatients = 0;
        this.totalRegularWaitTime = 0.0;
        this.totalPriorityWaitTime = 0.0;
        this.maxRegularWaitTime = 0;
        this.maxPriorityWaitTime = 0;

        // maintain a queue of nurses ordered by earliest time of availability
        // This priority queue needs a proper way of determining the next available nurse
        Queue<Nurse> availableNurses = new PriorityQueue<>(this.nurseComparator);
        availableNurses.addAll(nurses);

        // ensure patients are processed in order of arrival
        // Ensure that the patients are ordered by arrival time (added by Ronny)
        this.patients.sort(this.timeComparator);

        // track the max queueing as part of the simulation
        maxQueueLength = 0;

        // determine the first available nurse
        Nurse nextAvailableNurse = availableNurses.poll();

        // process all patients in order of arrival at the Test Lane
        for (Patient patient : patients) {
            // let nurses handle patients on the queue, if any
            // until the time of the next available nurse is later than the patient who just arrived
            while (!waitingPatients.isEmpty() && nextAvailableNurse.getAvailableAt().compareTo(patient.getArrivedAt()) <= 0) {
                // handle the next patient from the queue
                Patient nextPatient = waitingPatients.poll();

                LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                        nextAvailableNurse.getAvailableAt() :
                        nextPatient.getArrivedAt();
                nextAvailableNurse.samplePatient(nextPatient, startTime);

                // reorder the current nurse into the queue of nurses as per her next availability
                // (after completing the current patient)
                availableNurses.add(nextAvailableNurse);

                // get the next available nurse for handling of the next patient
                nextAvailableNurse = availableNurses.poll();
            }

            // add the patient that just arrived to the queue before letting the nurses proceed
            waitingPatients.add(patient);

            // keep track of the maximum queue length
            maxQueueLength = Integer.max(maxQueueLength, waitingPatients.size());


        }

        // process the remaining patients on the queue, same as above
        while (!waitingPatients.isEmpty()) {
            Patient nextPatient = waitingPatients.poll();
            LocalTime startTime = nextAvailableNurse.getAvailableAt().isAfter(nextPatient.getArrivedAt()) ?
                    nextAvailableNurse.getAvailableAt() :
                    nextPatient.getArrivedAt();
            nextAvailableNurse.samplePatient(nextPatient, startTime);
            availableNurses.add(nextAvailableNurse);
            nextAvailableNurse = availableNurses.poll();

        }

        // all patients are underway

        // calculate the aggregated statistics from the simulation
        //  i.e. time the work was finished
        //       average and maximum waiting times

        // set new available time same for all nurses (added by Ronny)
        for (Nurse nurse : this.nurses) {
            nurse.setAvailableAt(nextAvailableNurse.getAvailableAt());
        }

        // set end time of te testing late (added by Ronny)
        this.workFinished = nextAvailableNurse.getAvailableAt();

        // Loop through all patients to get the maximum waiting time for both prio patients and non prio patients (added by Ronny)
        for (Patient patient : this.patients) {
            // for prio patients
            if (patient.isHasPriority()) {
                // for calculating stats
                this.totalPriorityWaitTime += patient.totalWaitTimeSeconds();
                this.totalPriorityPatients++;
                // Get longest waiting prio patient
                if (patient.totalWaitTimeSeconds() > this.maxPriorityWaitTime) {
                    this.maxPriorityWaitTime = (int) patient.totalWaitTimeSeconds();
                }
            }
            // for reg patients
            else if (!patient.isHasPriority()) {
                // for calculating stats
                this.totalRegularWaitTime += patient.totalWaitTimeSeconds();
                this.totalRegularPatients++;
                // Get longest waiting reg patient
                if (patient.totalWaitTimeSeconds() > this.maxRegularWaitTime) {
                    this.maxRegularWaitTime = (int) patient.totalWaitTimeSeconds();
                }
            }
        }
        // calculate average waiting time for prio and reg patient.
        this.averageRegularWaitTime = this.totalRegularWaitTime / this.totalRegularPatients;
        this.averagePriorityWaitTime = this.totalPriorityWaitTime / this.totalPriorityPatients;

    }

    /**
     * Report the statistics of the simulation
     */
    public void printSimulationResults() {
        System.out.println("Simulation results per nurse:");
        System.out.println("    Name: #Patients:    Avg. sample time: Workload:");

        //  report per nurse:
        //  numPatients,
        //  average sample time for taking the nose sample,
        //  and percentage of opening hours of the Test Lane actually spent on taking samples
        for (Nurse nurse : nurses) {
            // the total time the lane is open
            double timeOpen = this.closingTime.toSecondOfDay() - this.openingTime.toSecondOfDay();
            // calculate the workload for total time open
            double workLoad = (nurse.getTotalSamplingTime() / timeOpen) * 100;
            System.out.println(nurse.toString() + String.format("%.0f", workLoad) + "%");
        }


        // report the time all nurses had finished all sampling work
        System.out.println("Work finished at " + this.workFinished);
        // report the maximum length of the queue at any time
        System.out.println("Maximum patient queue length " + this.maxQueueLength);

        // report average and maximum wait times for regular and priority patients (if any)
        System.out.printf("Wait times:        Average:  Maximum:%n");
        System.out.printf("Regular patients:      %.2f        %d%n", this.averageRegularWaitTime, this.maxRegularWaitTime);
        // if there are any prio patients print their stats
        if (this.totalPriorityPatients > 0) {
            System.out.printf("Priority patients:      %.2f        %d%n", this.averagePriorityWaitTime, this.maxPriorityWaitTime);
        }

        // make the printing a little bit prettier
        System.out.println("\n");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("\n");

    }

    /**
     * Report the statistics of the patients
     */
    public void printPatientStatistics() {

        System.out.println("\nPatient counts by zip area:");
        Map<String, Integer> patientCounts = patientsByZipArea();
        System.out.println(patientsByZipArea());

        System.out.println("\nZip area with highest patient percentage per complaint:");
        Map<Patient.Symptom, String> zipAreasPerSymptom =
                zipAreasWithHighestPatientPercentageBySymptom(patientCounts);
        System.out.println(zipAreasPerSymptom);
    }

    /**
     * Calculate the number of patients per zip-area code (i.e. the digits of a zipcode)
     *
     * @return a map of patient counts per zip-area code
     */
    public Map<String, Integer> patientsByZipArea() {
        // create, populate and return the result map
        // create treemap, in test it is in order...
        Map<String, Integer> patientsByZip = new TreeMap<>();

        // loop through all patients to add 1 to the zip area
        for (Patient patient : patients) {
            // we only want the numbers so first 4 chars of string
            String code = patient.getZipCode().substring(0, 4);
            // check if the code (key) is already in the map
            patientsByZip.computeIfPresent(code, (key, value) -> value += 1);
            // if code (key) is absent insert into map
            patientsByZip.putIfAbsent(code, 1);

        }
        // return the filled map
        return patientsByZip;
    }

    /**
     * @param patientsByZipArea map of amount of patient per zip area
     * @return a map with symptom and wich zip area has the highest percentage of that symptom
     */
    public Map<Patient.Symptom, String> zipAreasWithHighestPatientPercentageBySymptom(Map<String, Integer> patientsByZipArea) {
        // create, populate and return the result map
        // create tree map, so we get it in order
        Map<Patient.Symptom, String> highestPatientBySymptom = new TreeMap<>();

        // to get highest % zip area for all symptoms we loop through all symptoms
        for (Patient.Symptom symptom : Patient.Symptom.values()) {
            // keep track of the highest percentage and the zip area that has the highest %
            double zipHighestPercentage = 0;
            String zipHighest = "";
            // We loop through all zip areas in "ByZip" map to check for each zip area what the % is for the symptom
            for (String key : patientsByZipArea.keySet()) {
                // keep track of the patients that have the symptom
                double countOfSymptom = 0;
                // loop through all patients to check wich patient has the symptom
                for (Patient patient : patients) {
                    // only check for current zip area
                    if (key.equals(patient.getZipCode().substring(0, 4))) {
                        // check if the patient has this symptom
                        boolean hasSymptom = patient.getSymptoms()[symptom.ordinal()];
                        // if patient has the symptom we update the counter
                        if (hasSymptom) {
                            countOfSymptom += 1;
                        }
                    }
                }
                // We calculate the percentage of patients who have the symptom in the zip area
                double percentageAmount = (countOfSymptom / patientsByZipArea.get(key)) * 100;
                // If this zip area had a higher percentage we update the old % and zip area
                if (percentageAmount > zipHighestPercentage) {
                    zipHighestPercentage = percentageAmount;
                    zipHighest = key;
                }


            }
            // create a string to add with the symptom key
            String codeWithAmount = String.format(" %s %.0f%%", zipHighest, zipHighestPercentage);
            // add the symptom as key with the string value of zip code with highest % of the symptom
            highestPatientBySymptom.put(symptom, codeWithAmount);
        }

        return highestPatientBySymptom;
    }

    // Getters and Setters

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Nurse> getNurses() {
        return nurses;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public int getMaxRegularWaitTime() {
        return maxRegularWaitTime;
    }

    public int getMaxPriorityWaitTime() {
        return maxPriorityWaitTime;
    }

    public double getAverageRegularWaitTime() {
        return averageRegularWaitTime;
    }

    public double getAveragePriorityWaitTime() {
        return averagePriorityWaitTime;
    }

    public LocalTime getWorkFinished() {
        return workFinished;
    }
}
