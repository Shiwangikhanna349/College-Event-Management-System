package service;

import model.Event;
import model.Participant;
import algorithms.BinarySearchTree;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import algorithms.BinarySearch;
import util.DataStorage;

public class EventService {
    private List<Event> events;  // Using LinkedList for events
    private BinarySearchTree<String> categoryTree;  // Tree for managing event categories
    private int nextEventId;
    private Map<Integer, List<Participant>> eventRegistrations;
    private Map<Integer, Queue<Participant>> eventWaitlists;

    public EventService() {
        this.events = new LinkedList<>();
        this.categoryTree = new BinarySearchTree<>();
        this.nextEventId = 1;
        this.eventRegistrations = new HashMap<>();
        this.eventWaitlists = new HashMap<>();
        loadEvents();
    }

    private void loadEvents() {
        // First load all events
        List<String> eventData = DataStorage.loadEvents();
        for (String data : eventData) {
            String[] parts = data.split(",");
            if (parts.length >= 7) {  // Changed to >= to handle both old and new formats
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                LocalDate date = LocalDate.parse(parts[2]);
                String venue = parts[3];
                String organizer = parts[4];
                String type = parts[5].toLowerCase();  // Normalize to lowercase
                int capacity = Integer.parseInt(parts[6]);
                Event event = new Event(id, name, date, venue, organizer, type, capacity);
                
                // If the data includes currentRegistrations (new format), set it
                int currentRegistrations = 0;
                if (parts.length >= 8) {
                    currentRegistrations = Integer.parseInt(parts[7]);
                }
                event.setCurrentRegistrations(currentRegistrations);
                
                // Initialize the eventRegistrations map with empty linked lists for each event
                eventRegistrations.putIfAbsent(id, new LinkedList<>());
                
                events.add(event);
                categoryTree.insert(type);  // First insert the category if it doesn't exist
                categoryTree.incrementCount(type);  // Then increment the count
                if (id >= nextEventId) {
                    nextEventId = id + 1;
                }
            }
        }
    }

    public void addEvent(String name, LocalDate date, String venue, String organizer, String type, int capacity) {
        Event event = new Event(nextEventId++, name, date, venue, organizer, type.toLowerCase(), capacity);
        events.add(event);
        categoryTree.insert(type.toLowerCase());  // First ensure the category exists
        categoryTree.incrementCount(type.toLowerCase());  // Then increment its count
        DataStorage.saveEvents(events);
    }

    public Event createEvent(String name, LocalDate date, String venue, String organizer, String type, int capacity) {
        Event event = new Event(nextEventId++, name, date, venue, organizer, type.toLowerCase(), capacity);  // Normalize to lowercase
        events.add(event);
        categoryTree.insert(type.toLowerCase());  // Normalize to lowercase
        categoryTree.incrementCount(type.toLowerCase());  // Increment the count for this category
        saveEvent(event);
        return event;
    }

    public void saveEvent(Event event) {
        // Update the event in the events list
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId() == event.getId()) {
                events.set(i, event);
                break;
            }
        }
        
        // Save all events to file
        DataStorage.saveEvents(events);
    }

    public boolean updateEvent(int id, String name, LocalDate date, String venue, String organizer, String type, int capacity) {
        Event event = findEventById(id);
        if (event != null) {
            event.setName(name);
            event.setDate(date);
            event.setVenue(venue);
            event.setOrganizer(organizer);
            event.setType(type);
            event.setCapacity(capacity);
            
            // Save all events again
            DataStorage.clearEvents();
            for (Event e : events) {
                saveEvent(e);
            }
            return true;
        }
        return false;
    }

    public boolean deleteEvent(int id) {
        Event event = findEventById(id);
        if (event != null) {
            events.remove(event);
            categoryTree.decrementCount(event.getType());  // Decrement category count
            // Save all events again
            DataStorage.clearEvents();
            for (Event e : events) {
                saveEvent(e);
            }
            return true;
        }
        return false;
    }

    public List<Event> getAllEvents() {
        return new LinkedList<>(events);
    }

    // Uses binary search to find Event by ID in a sorted array (for project requirement)
    public Event findEventById(int id) {
        if (events.isEmpty()) return null;
        // Sort events by ID
        events.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        Event[] eventArray = events.toArray(new Event[0]);
        // Create a dummy Event with the target ID for comparison
        Event target = new Event(id, "", null, "", "", "", 0);
        int idx = BinarySearch.binarySearch(eventArray, target);
        if (idx >= 0) return eventArray[idx];
        return null;
    }

    public List<Event> findEventsByDate(LocalDate date) {
        List<Event> result = new LinkedList<>();
        for (Event event : events) {
            if (event.getDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<Event> findEventsByType(String type) {
        List<Event> result = new LinkedList<>();
        String normalizedType = type.toLowerCase();
        for (Event event : events) {
            if (event.getType().toLowerCase().equals(normalizedType)) {
                result.add(event);
            }
        }
        return result;
    }

    public boolean incrementEventRegistrations(int eventId) {
        Event event = findEventById(eventId);
        if (event != null) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
            saveEvent(event); // Save the updated event to file
            return true;
        }
        return false;
    }

    public boolean decrementEventRegistrations(int eventId) {
        Event event = findEventById(eventId);
        if (event != null) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() - 1);
            saveEvent(event); // Save the updated event to file
            return true;
        }
        return false;
    }

    public boolean registerParticipantForEvent(int eventId, Participant participant) {
        Event event = findEventById(eventId);
        if (event == null) {
            return false;
        }

        // Get current registrations for this event
        List<Participant> participants = eventRegistrations.computeIfAbsent(eventId, k -> new LinkedList<>());
        
        // Check if participant is already registered for this event
        for (Participant p : participants) {
            if (p.getEmail().equals(participant.getEmail())) {
                System.out.println("You are already registered for this event!");
                return false;
            }
        }

        // Check if event is full
        if (participants.size() >= event.getCapacity()) {
            // Add to waitlist
            Queue<Participant> waitlist = eventWaitlists.computeIfAbsent(eventId, k -> new LinkedList<>());
            waitlist.add(participant);
            System.out.println("Event is full! You have been added to the waitlist.");
            return false;
        }

        // Register participant
        participants.add(participant);
        
        // Update current registrations count
        event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
        saveEvent(event);  // Save the updated event with new registration count
        
        DataStorage.saveEvents(events);
        
        System.out.println("Successfully registered for the event!");
        return true;
    }

    public boolean cancelRegistration(int eventId, Participant participant) {
        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found!");
            return false;
        }

        List<Participant> registrations = eventRegistrations.get(eventId);
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No registrations found for this event!");
            return false;
        }

        // Check if participant is registered
        boolean isRegistered = false;
        for (Participant p : registrations) {
            if (p.getEmail().equals(participant.getEmail())) {
                isRegistered = true;
                break;
            }
        }

        if (!isRegistered) {
            System.out.println("You are not registered for this event!");
            return false;
        }

        // Remove the participant
        registrations.removeIf(p -> p.getEmail().equals(participant.getEmail()));
        event.cancelRegistration();

        // If there's someone in waitlist, add them to the event
        Queue<Participant> waitlist = eventWaitlists.get(eventId);
        if (waitlist != null && !waitlist.isEmpty()) {
            Participant nextParticipant = waitlist.poll();
            registrations.add(nextParticipant);
            event.registerParticipant();
            System.out.println("A participant from the waitlist has been automatically registered.");
        }
        return true;
    }

    public List<Participant> getEventParticipants(int eventId) {
        return eventRegistrations.getOrDefault(eventId, new LinkedList<>());
    }

    public Queue<Participant> getEventWaitlist(int eventId) {
        return eventWaitlists.getOrDefault(eventId, new LinkedList<>());
    }

    public void displayEventRegistrations(int eventId) {
        Event event = findEventById(eventId);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        List<Participant> registrations = getEventParticipants(eventId);
        Queue<Participant> waitlist = getEventWaitlist(eventId);

        System.out.println("\nEvent: " + event.getName());
        System.out.println("Registered Participants (" + registrations.size() + "/" + event.getCapacity() + "):");
        for (Participant participant : registrations) {
            System.out.println("- " + participant.getName() + " (" + participant.getEmail() + ")");
        }

        if (!waitlist.isEmpty()) {
            System.out.println("\nWaitlist (" + waitlist.size() + "):");
            for (Participant participant : waitlist) {
                System.out.println("- " + participant.getName() + " (" + participant.getEmail() + ")");
            }
        }
    }

    // Method to display event categories with their counts
    public void displayEventCategories() {
        // First, collect all categories and their counts in a case-insensitive way
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Event event : events) {
            String category = event.getType().toLowerCase();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }
        
        // Display the categories with their counts
        System.out.println("\nEvent Categories (with event counts):");
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " events)");
        }
        System.out.println();
    }
} 