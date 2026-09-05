import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Mini Hospital Emergency Management System
 * -------------------------------------------
 * Console application that ties together four required data structures:
 *
 *   1. PatientBST          - Binary Search Tree of patient records (keyed by Patient ID)
 *   2. EmergencyQueue       - FIFO queue of patients waiting for treatment
 *   3. TreatmentHistoryStack - LIFO stack of completed treatment records
 *   4. VisitHistory          - Singly linked list of past visits, one per patient
 *
 * Typical flow:
 *   Register patient -> stored in BST AND placed in the Emergency Queue.
 *   Call next patient -> dequeued from the Emergency Queue for treatment.
 *   Complete treatment -> pushed onto the Treatment History stack AND
 *                         appended to that patient's Visit History linked list.
 */
public class HospitalSystem {

    private static final PatientBST patientRecords = new PatientBST();
    private static final EmergencyQueue emergencyQueue = new EmergencyQueue();
    private static final TreatmentHistoryStack treatmentHistory = new TreatmentHistoryStack();

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Simple auto-incrementing counter used to generate Visit IDs.
    private static int nextVisitId = 1;

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("=========================================");
        System.out.println(" MINI HOSPITAL EMERGENCY MANAGEMENT SYSTEM");
        System.out.println("=========================================");

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> searchPatient();
                case 3 -> deletePatient();
                case 4 -> patientRecords.inorderTraversal();
                case 5 -> emergencyQueue.displayQueue();
                case 6 -> callNextPatient();
                case 7 -> treatmentHistory.displayStack();
                case 8 -> undoLastTreatment();
                case 9 -> addVisitToPatient();
                case 10 -> removeVisitFromPatient();
                case 11 -> searchVisitForPatient();
                case 12 -> displayPatientVisitHistory();
                case 0 -> {
                    running = false;
                    System.out.println("Exiting Hospital Emergency Management System. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println(" Patient Records (BST)");
        System.out.println("  1. Register new patient");
        System.out.println("  2. Search patient by ID");
        System.out.println("  3. Delete patient");
        System.out.println("  4. Display all patients (in-order)");
        System.out.println(" Emergency Queue");
        System.out.println("  5. Display waiting queue");
        System.out.println("  6. Call next patient for treatment (dequeue)");
        System.out.println(" Treatment History (Stack)");
        System.out.println("  7. Display treatment history");
        System.out.println("  8. Undo last completed treatment (pop)");
        System.out.println(" Patient Visit History (Linked List)");
        System.out.println("  9. Add a visit record to a patient");
        System.out.println(" 10. Remove a visit record from a patient");
        System.out.println(" 11. Search a visit record for a patient");
        System.out.println(" 12. Display a patient's visit history");
        System.out.println("  0. Exit");
        System.out.println("--------------------------------------------");
    }

    // ---------------------------------------------------------------
    // 1-4: Patient Records (BST)
    // ---------------------------------------------------------------

    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        int id = readInt("Patient ID: ");

        if (patientRecords.search(id) != null) {
            System.out.println("A patient with ID " + id + " already exists.");
            return;
        }

        String name = readString("Patient Name: ");
        int age = readInt("Age: ");
        String contact = readString("Contact Number: ");
        String condition = readString("Medical Condition: ");

        Patient patient = new Patient(id, name, age, contact, condition);
        patientRecords.insert(patient);
        emergencyQueue.enqueue(patient);

        System.out.println("Patient registered and added to the emergency queue.");
    }

    private static void searchPatient() {
        System.out.println("\n-- Search Patient --");
        int id = readInt("Enter Patient ID to search: ");
        Patient patient = patientRecords.search(id);
        if (patient == null) {
            System.out.println("No patient found with ID " + id);
        } else {
            System.out.println("Found: " + patient);
        }
    }

    private static void deletePatient() {
        System.out.println("\n-- Delete Patient --");
        int id = readInt("Enter Patient ID to delete: ");
        boolean removed = patientRecords.delete(id);
        if (removed) {
            System.out.println("Patient with ID " + id + " was deleted.");
        } else {
            System.out.println("No patient found with ID " + id);
        }
    }

    // ---------------------------------------------------------------
    // 5-6: Emergency Queue
    // ---------------------------------------------------------------

    private static void callNextPatient() {
        System.out.println("\n-- Call Next Patient --");
        Patient patient = emergencyQueue.dequeue();
        if (patient == null) {
            return; // dequeue() already printed the "empty queue" message
        }

        System.out.println("Now treating: " + patient);

        String choice = readString("Mark this patient's treatment as completed now? (y/n): ");
        if (choice.equalsIgnoreCase("y")) {
            completeTreatment(patient);
        } else {
            System.out.println("Patient removed from queue without completing treatment.");
        }
    }

    // ---------------------------------------------------------------
    // 7-8: Treatment History (Stack)
    // ---------------------------------------------------------------

    private static void completeTreatment(Patient patient) {
        String treatmentDetails = readString("Enter treatment details for " + patient.getName() + ": ");
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);

        // 1. Push the completed treatment onto the Treatment History stack.
        TreatmentRecord record = new TreatmentRecord(
                patient.getPatientId(), patient.getName(), treatmentDetails, timestamp);
        treatmentHistory.push(record);

        // 2. Append this visit to the patient's own Visit History linked list.
        String doctorName = readString("Attending doctor's name: ");
        String diagnosis = readString("Diagnosis: ");
        Visit visit = new Visit(nextVisitId++, timestamp, doctorName, diagnosis, treatmentDetails);
        patient.getVisitHistory().addVisit(visit);

        System.out.println("Treatment completed and recorded for " + patient.getName() + ".");
    }

    private static void undoLastTreatment() {
        System.out.println("\n-- Undo Last Completed Treatment --");
        TreatmentRecord record = treatmentHistory.pop();
        if (record != null) {
            System.out.println("Removed most recent treatment record: " + record);
        }
    }

    // ---------------------------------------------------------------
    // 9-12: Patient Visit History (Singly Linked List)
    // ---------------------------------------------------------------

    private static void addVisitToPatient() {
        System.out.println("\n-- Add Visit Record --");
        Patient patient = findPatientOrPrintError();
        if (patient == null) return;

        String date = readString("Visit Date (e.g. 2026-09-05): ");
        String doctorName = readString("Doctor Name: ");
        String diagnosis = readString("Diagnosis: ");
        String treatment = readString("Treatment: ");

        Visit visit = new Visit(nextVisitId++, date, doctorName, diagnosis, treatment);
        patient.getVisitHistory().addVisit(visit);
        System.out.println("Visit record added for " + patient.getName() + " (Visit ID: " + visit.getVisitId() + ")");
    }

    private static void removeVisitFromPatient() {
        System.out.println("\n-- Remove Visit Record --");
        Patient patient = findPatientOrPrintError();
        if (patient == null) return;

        int visitId = readInt("Enter Visit ID to remove: ");
        boolean removed = patient.getVisitHistory().removeVisit(visitId);
        if (removed) {
            System.out.println("Visit " + visitId + " removed from " + patient.getName() + "'s history.");
        } else {
            System.out.println("No visit found with ID " + visitId + " for this patient.");
        }
    }

    private static void searchVisitForPatient() {
        System.out.println("\n-- Search Visit Record --");
        Patient patient = findPatientOrPrintError();
        if (patient == null) return;

        int visitId = readInt("Enter Visit ID to search: ");
        Visit visit = patient.getVisitHistory().searchVisit(visitId);
        if (visit == null) {
            System.out.println("No visit found with ID " + visitId + " for this patient.");
        } else {
            System.out.println("Found: " + visit);
        }
    }

    private static void displayPatientVisitHistory() {
        System.out.println("\n-- Patient Visit History --");
        Patient patient = findPatientOrPrintError();
        if (patient == null) return;

        System.out.println("Visit history for " + patient.getName() + ":");
        patient.getVisitHistory().displayHistory();
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static Patient findPatientOrPrintError() {
        int id = readInt("Enter Patient ID: ");
        Patient patient = patientRecords.search(id);
        if (patient == null) {
            System.out.println("No patient found with ID " + id);
        }
        return patient;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
