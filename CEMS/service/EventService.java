package service;

import model.Event;
import model.Participant;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import util.DataStorage;


public class EventService {
    private List<Event> events;
    private int nextEventId;
    private Map<Integer, List<Participant>> eventRegistrations;
    private Map<Integer, Queue<Participant>> eventWaitlists;

    public EventService() {
        this.events = new ArrayList<>();
        this.nextEventId = 1;
        this.eventRegistrations = new HashMap<>();
        this.eventWaitlists = new HashMap<>();
        loadEvents();
    }

    private void loadEvents() {
        List<String> eventData = DataStorage.loadEvents();
        for (String data : eventData) {
            String[] parts = data.split(",");
            if (parts.length == 7) {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                LocalDate date = LocalDate.parse(parts[2]);
                String venue = parts[3];
                String organizer = parts[4];
                String type = parts[5];
                int capacity = Integer.parseInt(parts[6]);
                events.add(new Event(id, name, date, venue, organizer, type, capacity));
                if (id >= nextEventId) {
                    nextEventId = id + 1;
                }
            }
        }
    }

    public Event createEvent(String name, LocalDate date, String venue, String organizer, String type, int capacity) {
        Event event = new Event(nextEventId++, name, date, venue, organizer, type, capacity);
        events.add(event);
        saveEvent(event);
        return event;
    }

    private void saveEvent(Event event) {
        String eventData = String.format("%d,%s,%s,%s,%s,%s,%d",
            event.getId(),
            event.getName(),
            event.getDate().toString(),
            event.getVenue(),
            event.getOrganizer(),
            event.getType(),
            event.getCapacity()
        );
        DataStorage.saveEvent(eventData);
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
        return new ArrayList<>(events);
    }

    public Event findEventById(int id) {
        for (Event event : events) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }

    public List<Event> findEventsByDate(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    public List<Event> findEventsByType(String type) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (event.getType().equalsIgnoreCase(type)) {
                result.add(event);
            }
        }
        return result;
    }

    public boolean incrementEventRegistrations(int eventId) {
        Event event = findEventById(eventId);
        if (event != null) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
            return true;
        }
        return false;
    }

    public boolean decrementEventRegistrations(int eventId) {
        Event event = findEventById(eventId);
        if (event != null) {
            event.setCurrentRegistrations(event.getCurrentRegistrations() - 1);
            return true;
        }
        return false;
    }

    public boolean registerParticipantForEvent(int eventId, Participant participant) {
        Event event = findEventById(eventId);
        if (event == null) {
            return false;
        }

        // Initialize registration list and waitlist if not exists
        eventRegistrations.putIfAbsent(eventId, new ArrayList<>());
        eventWaitlists.putIfAbsent(eventId, new LinkedList<>());

        List<Participant> registrations = eventRegistrations.get(eventId);
        Queue<Participant> waitlist = eventWaitlists.get(eventId);

        // Check if participant is already registered
        if (registrations.contains(participant)) {
            System.out.println("Participant is already registered for this event.");
            return false;
        }

        // Check if event is full
        if (registrations.size() < event.getCapacity()) {
            registrations.add(participant);
            event.registerParticipant();
            System.out.println("Successfully registered for the event!");
            return true;
        } else {
            // Add to waitlist
            waitlist.add(participant);
            System.out.println("Event is full. You have been added to the waitlist.");
            return false;
        }
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
        return eventRegistrations.getOrDefault(eventId, new ArrayList<>());
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
} 