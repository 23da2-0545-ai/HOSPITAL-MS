/**
 * Binary Search Tree that stores Patient records, keyed by patientId.
 *
 * Supported operations:
 *  - insert(Patient)        : add a new patient
 *  - search(patientId)      : find a patient by ID
 *  - delete(patientId)      : remove a patient by ID
 *  - inorderTraversal()     : print patients in ascending order of patientId
 */
public class PatientBST {

    // Internal BST node.
    private static class Node {
        Patient patient;
        Node left, right;

        Node(Patient patient) {
            this.patient = patient;
            left = null;
            right = null;
        }
    }

    private Node root;

    public PatientBST() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    // ---------------- INSERT ----------------

    public void insert(Patient patient) {
        root = insertRec(root, patient);
    }

    private Node insertRec(Node node, Patient patient) {
        if (node == null) {
            return new Node(patient);
        }
        if (patient.getPatientId() < node.patient.getPatientId()) {
            node.left = insertRec(node.left, patient);
        } else if (patient.getPatientId() > node.patient.getPatientId()) {
            node.right = insertRec(node.right, patient);
        } else {
            // Duplicate ID - update the existing record instead of inserting again.
            System.out.println("A patient with ID " + patient.getPatientId() +
                    " already exists. Record was not duplicated.");
        }
        return node;
    }

    // ---------------- SEARCH ----------------

    public Patient search(int patientId) {
        Node result = searchRec(root, patientId);
        return (result == null) ? null : result.patient;
    }

    private Node searchRec(Node node, int patientId) {
        if (node == null || node.patient.getPatientId() == patientId) {
            return node;
        }
        if (patientId < node.patient.getPatientId()) {
            return searchRec(node.left, patientId);
        }
        return searchRec(node.right, patientId);
    }

    // ---------------- DELETE ----------------

    /** Deletes a patient by ID. Returns true if a patient was found and removed. */
    public boolean delete(int patientId) {
        if (search(patientId) == null) {
            return false;
        }
        root = deleteRec(root, patientId);
        return true;
    }

    private Node deleteRec(Node node, int patientId) {
        if (node == null) {
            return null;
        }

        if (patientId < node.patient.getPatientId()) {
            node.left = deleteRec(node.left, patientId);
        } else if (patientId > node.patient.getPatientId()) {
            node.right = deleteRec(node.right, patientId);
        } else {
            // Node found - handle the three standard BST deletion cases.

            // Case 1: no children (leaf node)
            if (node.left == null && node.right == null) {
                return null;
            }

            // Case 2: one child
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            // Case 3: two children -> replace with the in-order successor
            // (the smallest value in the right subtree).
            Node successor = findMin(node.right);
            node.patient = successor.patient;
            node.right = deleteRec(node.right, successor.patient.getPatientId());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // ---------------- IN-ORDER TRAVERSAL ----------------

    /** Prints all patients in ascending order of Patient ID. */
    public void inorderTraversal() {
        if (root == null) {
            System.out.println("   No patient records available.");
            return;
        }
        inorderRec(root);
    }

    private void inorderRec(Node node) {
        if (node != null) {
            inorderRec(node.left);
            System.out.println("   " + node.patient);
            inorderRec(node.right);
        }
    }
}
