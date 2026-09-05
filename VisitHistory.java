/**
 * Singly Linked List that stores a patient's previous hospital visits.
 * Each Patient object owns exactly one VisitHistory.
 *
 * Supported operations:
 *  - addVisit(Visit)      : append a new visit to the end of the list
 *  - removeVisit(visitId) : remove a visit by its ID
 *  - searchVisit(visitId) : find and return a visit by its ID
 *  - displayHistory()     : print all visits in order
 */
public class VisitHistory {

    // Internal node of the singly linked list.
    private static class Node {
        Visit visit;
        Node next;

        Node(Visit visit) {
            this.visit = visit;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public VisitHistory() {
        head = null;
        size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    /** Adds a new visit to the end of the linked list. */
    public void addVisit(Visit visit) {
        Node newNode = new Node(visit);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /** Removes a visit with the given visitId. Returns true if removed. */
    public boolean removeVisit(int visitId) {
        if (head == null) {
            return false;
        }

        // Special case: removing the head node.
        if (head.visit.getVisitId() == visitId) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.visit.getVisitId() == visitId) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false; // not found
    }

    /** Searches for a visit by visitId. Returns the Visit, or null if not found. */
    public Visit searchVisit(int visitId) {
        Node current = head;
        while (current != null) {
            if (current.visit.getVisitId() == visitId) {
                return current.visit;
            }
            current = current.next;
        }
        return null;
    }

    /** Displays every visit in this patient's history, in insertion order. */
    public void displayHistory() {
        if (head == null) {
            System.out.println("   No visit history available.");
            return;
        }
        Node current = head;
        while (current != null) {
            System.out.println("   " + current.visit);
            current = current.next;
        }
    }
}
