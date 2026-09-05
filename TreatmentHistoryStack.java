/**
 * A custom LIFO Stack that stores completed treatment records.
 * Implemented internally using a singly linked list so it demonstrates
 * the underlying stack mechanics rather than relying on java.util.Stack.
 *
 * Supported operations:
 *  - push(TreatmentRecord) : add a newly completed treatment record
 *  - pop()                 : remove and return the most recent treatment record
 *  - displayStack()        : list every treatment record, most recent first
 *  - isEmpty()              : check whether the stack has no records
 */
public class TreatmentHistoryStack {

    private static class Node {
        TreatmentRecord record;
        Node next;

        Node(TreatmentRecord record) {
            this.record = record;
        }
    }

    private Node top;
    private int size;

    public TreatmentHistoryStack() {
        top = null;
        size = 0;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /** Pushes a newly completed treatment record onto the top of the stack. */
    public void push(TreatmentRecord record) {
        Node newNode = new Node(record);
        newNode.next = top;
        top = newNode;
        size++;
    }

    /** Removes and returns the most recently completed treatment record. */
    public TreatmentRecord pop() {
        if (isEmpty()) {
            System.out.println("The treatment history stack is empty.");
            return null;
        }
        TreatmentRecord record = top.record;
        top = top.next;
        size--;
        return record;
    }

    /** Displays all completed treatment records, most recent first. */
    public void displayStack() {
        if (isEmpty()) {
            System.out.println("   No completed treatment records available.");
            return;
        }
        Node current = top;
        while (current != null) {
            System.out.println("   " + current.record);
            current = current.next;
        }
    }
}
