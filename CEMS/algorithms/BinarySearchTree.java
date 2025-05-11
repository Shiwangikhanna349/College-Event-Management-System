package algorithms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Event;

public class BinarySearchTree<T extends Comparable<T>> {
    private class Node {
        T data;
        Node left;
        Node right;
        int count;  // Number of events in this category
        List<Event> events;  // List of events in this category

        Node(T data) {
            this.data = data;
            left = right = null;
            count = 1;  // Initialize count to 1 for new nodes
            events = new ArrayList<>();
        }
    }

    private Node root;

    public BinarySearchTree() {
        root = null;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node insertRec(Node root, T data) {
        if (root == null) {
            return new Node(data);  // New node will have count = 1
        }

        int comparison;
        if (data instanceof String) {
            comparison = ((String) data).compareToIgnoreCase((String) root.data);
        } else {
            comparison = data.compareTo(root.data);
        }

        if (comparison < 0)
            root.left = insertRec(root.left, data);
        else if (comparison > 0)
            root.right = insertRec(root.right, data);
        // No need to increment count here as it's handled by incrementCount

        return root;
    }

    public boolean search(T data) {
        return searchRec(root, data);
    }

    private boolean searchRec(Node root, T data) {
        if (root == null) return false;
        
        int comparison;
        if (data instanceof String) {
            comparison = ((String) data).compareToIgnoreCase((String) root.data);
        } else {
            comparison = data.compareTo(root.data);
        }

        if (comparison == 0) return true;
        if (comparison < 0) return searchRec(root.left, data);
        return searchRec(root.right, data);
    }

    public int getCount(T data) {
        Node node = findNode(root, data);
        return node != null ? node.count : 0;
    }

    private Node findNode(Node root, T data) {
        if (root == null) return null;
        
        int comparison;
        if (data instanceof String) {
            comparison = ((String) data).compareToIgnoreCase((String) root.data);
        } else {
            comparison = data.compareTo(root.data);
        }

        if (comparison == 0) return root;
        if (comparison < 0) return findNode(root.left, data);
        return findNode(root.right, data);
    }

    public void incrementCount(T data) {
        Node node = findNode(root, data);
        if (node != null) {
            node.count++;
        }
    }

    public void decrementCount(T data) {
        Node node = findNode(root, data);
        if (node != null && node.count > 0) {
            node.count--;
        }
    }

    public void displayCategories() {
        System.out.println("\nEvent Categories (with event counts):");
        displayCategoriesRec(root);
        System.out.println();
    }

    private void displayCategoriesRec(Node root) {
        if (root != null) {
            displayCategoriesRec(root.left);
            System.out.println("- " + root.data + " (" + root.count + " events)");
            displayCategoriesRec(root.right);
        }
    }

    public void addEvent(T category, Event event) {
        Node node = findNode(root, category);
        if (node != null) {
            node.events.add(event);
            node.count++;
        }
    }

    public List<Event> getEventsByCategory(T category) {
        Node node = findNode(root, category);
        return node != null ? new ArrayList<>(node.events) : new ArrayList<>();
    }

    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        getAllEventsRec(root, allEvents);
        return allEvents;
    }

    private void getAllEventsRec(Node root, List<Event> events) {
        if (root != null) {
            getAllEventsRec(root.left, events);
            events.addAll(root.events);
            getAllEventsRec(root.right, events);
        }
    }

    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Event> events = new ArrayList<>();
        getEventsByDateRangeRec(root, startDate, endDate, events);
        return events;
    }

    private void getEventsByDateRangeRec(Node root, LocalDate startDate, LocalDate endDate, List<Event> events) {
        if (root != null) {
            getEventsByDateRangeRec(root.left, startDate, endDate, events);
            for (Event event : root.events) {
                if (!event.getDate().isBefore(startDate) && !event.getDate().isAfter(endDate)) {
                    events.add(event);
                }
            }
            getEventsByDateRangeRec(root.right, startDate, endDate, events);
        }
    }

    public Event findEventById(int eventId) {
        return findEventByIdRec(root, eventId);
    }

    private Event findEventByIdRec(Node root, int eventId) {
        if (root == null) return null;
        
        // Check current node's events
        for (Event event : root.events) {
            if (event.getId() == eventId) {
                return event;
            }
        }
        
        // Search in left subtree
        Event leftResult = findEventByIdRec(root.left, eventId);
        if (leftResult != null) return leftResult;
        
        // Search in right subtree
        return findEventByIdRec(root.right, eventId);
    }
} 