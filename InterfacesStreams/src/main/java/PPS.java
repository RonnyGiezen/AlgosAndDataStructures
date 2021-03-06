import utils.SLF4J;
import utils.XMLParser;

import javax.xml.stream.XMLStreamConstants;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PPS {

    private static Random randomizer = new Random(06112020);

    private String name;                // the name of the planning system refers to its xml source file
    private int planningYear;                   // the year indicates the period of start and end dates of the projects
    private Set<Employee> employees;
    private Set<Project> projects;

    @Override
    public String toString() {
        return String.format("PPS_e%d_p%d", this.employees.size(), this.projects.size());
    }

    private PPS() {
        name = "none";
        planningYear = 2000;
        projects = new TreeSet<>();
        employees = new TreeSet<>();
    }

    private PPS(String resourceName, int year) {
        this();
        name = resourceName;
        planningYear = year;
    }

    /**
     * Reports the statistics of the project planning year
     */
    public void printPlanningStatistics() {
        System.out.printf("%nProject Statistics of '%s' in the year %d%n", name, planningYear);
        if (employees == null || projects == null || employees.isEmpty() || projects.isEmpty()) {
            System.out.println("No employees or projects have been set up...");
            return;
        }

        System.out.printf("%d employees have been assigned to %d projects:%n%n",
                employees.size(), projects.size());

        // calculate and display statistics
        // Print average hourly wage
        System.out.println("1. The average hourly wage of all employees is " + this.calculateAverageHourlyWage() + ".\n");

        // print longest project with the amount of working days of that project
        System.out.printf("2. %s with %d available working days. %n", this.calculateLongestProject().toString(),
                this.calculateLongestProject().getNumWorkingDays());

        // print hardest working employees
        // we also want to know the amount of projects the employee(s) is in.
        int projectsAmount = this.calculateMostInvolvedEmployees().stream()
                .mapToInt(e -> e.getAssignedProjects().size())
                .sum();
        System.out.printf("3. The following employees have the broadest assignment in no less than %d different projects:%n %s %n",
                projectsAmount, this.calculateMostInvolvedEmployees());

        // print total budget of all projects
        System.out.printf("4. The total budget of committed project manpower is %d %n", this.calculateTotalManpowerBudget());

        // print managed budget by junior employees
        // create predicate to get managed budget for only junior employees (as in example output assignment)
        Predicate<Employee> juniorEmps = e -> e.getHourlyWage() <= Employee.MAX_JUNIOR_WAGE;
        System.out.printf("5. Below is an overview of total managed budget by junior employees (hourly wage <= 26):%n%s%n",
                this.calculateManagedBudgetOverview(juniorEmps));

        // print employees working at least 8 hours
        System.out.printf("6. Below is an overview of employees working at least 8 hours per day:%n%s%n", this.getFulltimeEmployees());

        // print the map of monthly spends
        System.out.println("7. Below is an overview of cumulative monthly project spends:\n" + this.calculateCumulativeMonthlySpends());
        // pretty print for more than one test
        System.out.println("\n-----------------------------------------------------------------------");
    }

    /**
     * calculates the average hourly wage of all known employees in this system
     *
     * @return the average hourly wage of all known employees
     */
    public double calculateAverageHourlyWage() {
        // count all average wages of all employees
        // by collecting all employees from the set and using the Collectors method to calculate average
        return this.employees.stream()
                .collect(Collectors.averagingDouble(Employee::getHourlyWage));
    }

    /**
     * finds the project with the highest number of available working days.
     * (if more than one project with the highest number is found, any one is returned)
     *
     * @return project with longest working days
     */
    public Project calculateLongestProject() {
        // calculate longest project by comparing the projects on the amount of working days
        return projects.stream()
                .max(Comparator.comparing(Project::getNumWorkingDays))
                .orElse(null);
    }

    /**
     * calculates the total budget for assigned employees across all projects and employees in the system
     * based on the registration of committed hours per day per employee,
     * the number of working days in each project
     * and the hourly rate of each employee
     *
     * @return the integer of the total man power budget
     */
    public int calculateTotalManpowerBudget() {
        // calculate total budget of all projects by calculating the budget for each project and adding it all up
        return projects.stream()
                .mapToInt(Project::calculateManpowerBudget)
                .sum();
    }

    /**
     * finds the employees that are assigned to the highest number of different projects
     * (if multiple employees are assigned to the same highest number of projects,
     * all these employees are returned in the set)
     *
     * @return a new set with employees whi are most involved
     */
    public Set<Employee> calculateMostInvolvedEmployees() {
        // First we find the employee with the longest size of assigned projects (most involved)
        int longest = employees.stream()
                .mapToInt(e -> e.getAssignedProjects().size())
                .max()
                .orElse(0);
        // then we just filter the set of employees on wich employees also have the longest size
        // and we return the outcome in a new set
        return employees.stream()
                .filter(e -> e.getAssignedProjects().size() == longest)
                .collect(Collectors.toSet());
    }

    /**
     * Calculates an overview of total managed budget per employee that complies with the filter predicate
     * The total managed budget of an employee is the sum of all man power budgets of all projects
     * that are being managed by this employee
     *
     * @param filter predicate of employee type
     */
    public Map<Employee, Integer> calculateManagedBudgetOverview(Predicate<Employee> filter) {
        // first we filter the list then we add the employee to the map and the managed budget as value
        return employees.stream()
                .filter(filter)
                .collect(Collectors.toMap(Function.identity(), Employee::calculateManagedBudget));
    }

    /**
     * Calculates and overview of total monthly spends across all projects in the system
     * The monthly spend of a single project is the accumulated manpower cost of all employees assigned to the
     * project across all working days in the month.
     *
     * @return map od all months with spends for all projects
     */
    public Map<Month, Integer> calculateCumulativeMonthlySpends() {
        // create new tree map to get all months in order
        Map<Month, Integer> monthlySpends = new TreeMap<>();

        for (Project p : this.projects) {
            // set start of the loop to the start date of the project
            LocalDate current = p.getStartDate();
            // while loop for the time period of the project, stops when last month (end date) is done
            while (current.isBefore(p.getEndDate()) || current.isEqual(p.getEndDate())) {
                // total working days for the "first" month of the project
                int days = utils.Calendar.getNumWorkingDays(current, current.withDayOfMonth(current.lengthOfMonth()));
                // loop over map of employees in Project to calculate total spend on all employees per day
                int spends = p.getCommittedHoursPerDay().entrySet().stream()
                        .mapToInt(e -> e.getKey().getHourlyWage() * e.getValue())
                        .sum();
                // add to map, if month already in map update the value
                monthlySpends.computeIfPresent(current.getMonth(), (key, value) -> value += spends * days);
                // otherwise set month in map and the spends for this project for the month
                monthlySpends.putIfAbsent(current.getMonth(), spends * days);
                // go to next month
                current = current.plusMonths(1);
            }
        }
        // return the map
        return monthlySpends;

    }

    /**
     * Returns a set containing all the employees that work at least fulltime for at least one day per week on a project.
     *
     * @return set of all employees who are working more than 7 hours a day
     */
    public Set<Employee> getFulltimeEmployees() {
        // use flatmap to stream over committed hours map and filter the map values (hours per day of employee)
        // we filter on everything above 7 (will always be 8 or higher because integer)
        // then collect them in a new set and return the set
        return projects.stream()
                .flatMap(p -> p.getCommittedHoursPerDay().entrySet().stream())
                .filter(e -> e.getValue() > 7)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    /**
     * A builder helper class to compose a small PPS using method-chaining of builder methods
     */
    public static class Builder {
        PPS pps;

        public Builder() {
            pps = new PPS();
        }

        /**
         * Add another employee to the PPS being build
         *
         * @param employee employee to add
         * @return this builder
         */
        public Builder addEmployee(Employee employee) {
            // add employee to the set
            this.pps.employees.add(employee);
            return this;
        }

        /**
         * Add another project to the PPS
         * register the specified manager as the manager of the new
         *
         * @param project project to add
         * @param manager manager of the project
         * @return the builder
         */
        public Builder addProject(Project project, Employee manager) {
            // first we add new project
            this.pps.projects.add(project);
            // then we update the employees list with the manager because a manager is also a employee
            // but only if the manager does not exist yet
            if (!this.pps.employees.contains(manager)) {
                this.addEmployee(manager);
            }
            // then we set and find the manager in the set, otherwise we get lost objects
            Employee man = this.pps.employees.stream()
                    .filter(m -> m.getNumber() == manager.getNumber())
                    .findAny().orElse(null);
            // assert not null to make sonarlint happy
            assert man != null;
            // add the project to the manager as well
            man.getManagedProjects().add(project);
            return this;
        }

        /**
         * Add a commitment to work hoursPerDay on the project that is identified by projectCode
         * for the employee who is identified by employeeNr
         * This commitment is added to any other commitment that the same employee already
         * has got registered on the same project,
         *
         * @param projectCode the code of the project (UI)
         * @param employeeNr  the number of the employee (UI)
         * @param hoursPerDay the hours per day the employee is committing
         * @return the builder
         */
        public Builder addCommitment(String projectCode, int employeeNr, int hoursPerDay) {
            // get project on project code
            Project project = this.pps.projects.stream()
                    .filter(p -> p.getCode().equals(projectCode))
                    .findAny()
                    .orElse(null);
            // get employee on number, if not exists we create a new one
            Employee employee = this.pps.employees.stream()
                    .filter(e -> e.getNumber() == employeeNr)
                    .findAny()
                    .orElse(new Employee(employeeNr, hoursPerDay));
            // assert not null to make sonarlint happy
            assert project != null;
            // add commitment to the project with the employee found or created
            project.addCommitment(employee, hoursPerDay);

            return this;
        }

        /**
         * Complete the PPS being build
         *
         * @return the pps build
         */
        public PPS build() {
            return pps;
        }
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    /**
     * Loads a complete configuration from an XML file
     *
     * @param resourceName the XML file name to be found in the resources folder
     * @return pps
     */
    public static PPS importFromXML(String resourceName) {
        XMLParser xmlParser = new XMLParser(resourceName);

        try {
            xmlParser.nextTag();
            xmlParser.require(XMLStreamConstants.START_ELEMENT, null, "projectPlanning");
            int year = xmlParser.getIntegerAttributeValue(null, "year", 2000);
            xmlParser.nextTag();

            PPS pps = new PPS(resourceName, year);

            Project.importProjectsFromXML(xmlParser, pps.projects);
            Employee.importEmployeesFromXML(xmlParser, pps.employees, pps.projects);

            return pps;

        } catch (Exception ex) {
            SLF4J.logException("XML error in '" + resourceName + "'", ex);
        }

        return null;
    }
}
