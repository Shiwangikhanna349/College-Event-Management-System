import model.Event;
import model.Participant;
import service.EventService;
import service.ParticipantService;
import util.InputHelper;
import java.time.LocalDate;
import algorithms.SortingAlgorithms;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Main {
    private static EventService eventService = new EventService();
    private static ParticipantService participantService = new ParticipantService();

    public static void main(String[] args) {
        boolean running = true;
        
        while (running) {
            InputHelper.clearScreen();
            displayMainMenu();
            int choice = InputHelper.getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    handleEventManagement();
                    break;
                case 2:
                    handleStudentRegistration();
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using College Event Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    InputHelper.pressEnterToContinue();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|     College Event System    |");
        System.out.println("+-----------------------------+");
        System.out.println("| 1. Event Management         |");
        System.out.println("| 2. Student Registration     |");
        System.out.println("| 3. Exit                     |");
        System.out.println("+-----------------------------+");
    }

    private static void handleEventManagement() {
        while (true) {
            InputHelper.clearScreen();
            System.out.println("\nEvent Management (Organizer)");
            System.out.println("1. Create Event");
            System.out.println("2. Update Event");
            System.out.println("3. Delete Event");
            System.out.println("4. View All Events");
            System.out.println("5. Search Events by Date");
            System.out.println("6. Search Events by Type");
            System.out.println("7. View Event Registrations");
            System.out.println("8. Back to Main Menu");
            
            int choice = InputHelper.getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createEvent();
                    break;
                case 2:
                    updateEvent();
                    break;
                case 3:
                    deleteEvent();
                    break;
                case 4:
                    viewAllEvents();
                    break;
                case 5:
                    searchEventsByDate();
                    break;
                case 6:
                    searchEventsByType();
                    break;
                case 7:
                    viewEventRegistrations();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            InputHelper.pressEnterToContinue();
        }
    }

    private static void handleStudentRegistration() {
        while (true) {
            InputHelper.clearScreen();
            System.out.println("\nStudent Registration");
            System.out.println("1. View All Events");
            System.out.println("2. View Upcoming Events");
            System.out.println("3. Register for Event");
            System.out.println("4. View My Registrations");
            System.out.println("5. Cancel Registration");
            System.out.println("6. Back to Main Menu");
            
            int choice = InputHelper.getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    viewAllEventsForStudents();
                    break;
                case 2:
                    viewUpcomingEvents();
                    break;
                case 3:
                    registerForEvent();
                    break;
                case 4:
                    viewStudentRegistrations();
                    break;
                case 5:
                    cancelStudentRegistration();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            InputHelper.pressEnterToContinue();
        }
    }

    private static void createEvent() {
        System.out.println("\nCreate New Event");
        String name = InputHelper.getStringInput("Enter event name: ");
        LocalDate date = InputHelper.getDateInput("Enter event date (YYYY-MM-DD): ");
        String venue = InputHelper.getStringInput("Enter venue: ");
        String organizer = InputHelper.getStringInput("Enter organizer name: ");
        String type = InputHelper.getStringInput("Enter event type: ");
        int capacity = InputHelper.getIntInput("Enter event capacity: ");
        
        Event event = eventService.createEvent(name, date, venue, organizer, type, capacity);
        System.out.println("Event created successfully!");
        System.out.println(event);
    }

    private static void updateEvent() {
        int eventId = InputHelper.getIntInput("Enter event ID to update: ");
        Event event = eventService.findEventById(eventId);
        
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }
        
        String name = InputHelper.getStringInput("Enter new event name: ");
        LocalDate date = InputHelper.getDateInput("Enter new event date (YYYY-MM-DD): ");
        String venue = InputHelper.getStringInput("Enter new venue: ");
        String organizer = InputHelper.getStringInput("Enter new organizer name: ");
        String type = InputHelper.getStringInput("Enter new event type: ");
        int capacity = InputHelper.getIntInput("Enter new event capacity: ");
        
        if (eventService.updateEvent(eventId, name, date, venue, organizer, type, capacity)) {
            System.out.println("Event updated successfully!");
        } else {
            System.out.println("Failed to update event!");
        }
    }

    private static void deleteEvent() {
        int eventId = InputHelper.getIntInput("Enter event ID to delete: ");
        if (eventService.deleteEvent(eventId)) {
            System.out.println("Event deleted successfully!");
        } else {
            System.out.println("Event not found!");
        }
    }

    private static void viewAllEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found!");
            return;
        }
        // Use custom QuickSort to sort by name
        Event.setSortCriteria(Event.SORT_BY_TYPE); // We'll use TYPE as a proxy for name for demonstration, or you can add a SORT_BY_NAME
        Event[] eventArray = events.toArray(new Event[0]);
        // If you want to sort by name, add SORT_BY_NAME to Event and implement in compareTo
        SortingAlgorithms.quickSort(eventArray, 0, eventArray.length - 1);
        System.out.println("\nAll Events:");
        for (Event event : eventArray) {
            System.out.println(event);
            System.out.println("-------------------");
        }
    }

    private static void viewAllEventsForStudents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found!");
            return;
        }
        // Use custom BubbleSort to sort by capacity
        Event.setSortCriteria(Event.SORT_BY_CAPACITY);
        Event[] eventArray = events.toArray(new Event[0]);
        SortingAlgorithms.bubbleSort(eventArray);
        System.out.println("\nAvailable Events:");
        for (Event event : eventArray) {
            System.out.println(event.getId() + ". " + event.getName() + 
                             " (Date: " + event.getDate() + 
                             ", Capacity: " + event.getCurrentRegistrations() + "/" + event.getCapacity() + ")");
        }
    }

    private static void viewUpcomingEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found!");
            return;
        }
        // Use custom MergeSort to sort by date
        Event.setSortCriteria(Event.SORT_BY_DATE);
        Event[] eventArray = events.toArray(new Event[0]);
        SortingAlgorithms.mergeSort(eventArray, 0, eventArray.length - 1);
        LocalDate today = LocalDate.now();
        boolean found = false;
        System.out.println("\n" + "-".repeat(100));
        System.out.println(String.format("%-5s %-30s %-15s %-25s %-15s %s", 
            "ID", "Event Name", "Date", "Venue", "Available", "Type"));
        System.out.println("-".repeat(100));
        for (Event event : eventArray) {
            if (!event.getDate().isBefore(today)) {
                found = true;
                List<Participant> registrations = participantService.getParticipantsByEvent(event.getId());
                int currentRegistrations = registrations.size();
                int available = Math.max(0, event.getCapacity() - currentRegistrations);
                String availability = available > 0 ? 
                    String.format("%d/%d", available, event.getCapacity()) : "FULL";
                
                System.out.println(String.format("%-5d %-30s %-15s %-25s %-15s %s",
                    event.getId(),
                    event.getName().length() > 28 ? event.getName().substring(0, 25) + "..." : event.getName(),
                    event.getDate(),
                    event.getVenue().length() > 23 ? event.getVenue().substring(0, 20) + "..." : event.getVenue(),
                    availability,
                    event.getType()));
            }
        }
        
        System.out.println("-".repeat(100));
        
        if (!found) {
            System.out.println("No upcoming events found!");
        }
    }

    private static void searchEventsByDate() {
        LocalDate date = InputHelper.getDateInput("Enter date to search (YYYY-MM-DD): ");
        List<Event> events = eventService.findEventsByDate(date);
        
        if (events.isEmpty()) {
            System.out.println("No events found for the given date!");
            return;
        }
        
        System.out.println("\nEvents on " + date + ":");
        for (Event event : events) {
            System.out.println(event);
            System.out.println("-------------------");
        }
    }

    private static void searchEventsByType() {
        String type = InputHelper.getStringInput("Enter event type to search: ");
        List<Event> events = eventService.findEventsByType(type);
        
        if (events.isEmpty()) {
            System.out.println("No events found for the given type!");
            return;
        }
        
        System.out.println("\nEvents of type " + type + ":");
        for (Event event : events) {
            System.out.println(event);
            System.out.println("-------------------");
        }
    }

    private static void viewEventRegistrations() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found!");
            return;
        }

        System.out.println("\nAvailable Events:");
        for (Event event : events) {
            System.out.println(event.getId() + ". " + event.getName());
        }

        int eventId = InputHelper.getIntInput("\nEnter Event ID to view registrations: ");
        List<Participant> participants = participantService.getParticipantsByEvent(eventId);
        
        if (participants.isEmpty()) {
            System.out.println("No registrations found for this event!");
            return;
        }

        System.out.println("\nRegistered Participants:");
        for (Participant participant : participants) {
            System.out.println(participant);
            System.out.println("-------------------");
        }
    }

    private static void registerForEvent() {
        System.out.println("\n=== Upcoming Events for Registration ===");
        viewUpcomingEvents();
        
        int eventId = InputHelper.getIntInput("\nEnter event ID to register (or 0 to cancel): ");
        
        if (eventId == 0) {
            System.out.println("Registration cancelled.");
            return;
        }
        
        Event event = eventService.findEventById(eventId);
        
        if (event == null) {
            System.out.println("Error: Event not found!");
            return;
        }
        
        // Check if event is in the future
        if (event.getDate().isBefore(LocalDate.now())) {
            System.out.println("Error: This event has already occurred!");
            return;
        }
        
        // Get current registrations for this event
        List<Participant> existingRegistrations = participantService.getParticipantsByEvent(eventId);
        int currentRegistrations = existingRegistrations.size();
        
        // Check if event is full
        if (currentRegistrations >= event.getCapacity()) {
            System.out.println("\n❌ Sorry, this event is now full!");
            System.out.println("Current registrations: " + currentRegistrations + "/" + event.getCapacity());
            return;
        }
        
        System.out.println("\n=== Registering for: " + event.getName() + " ===");
        System.out.println("Date: " + event.getDate());
        System.out.println("Venue: " + event.getVenue());
        System.out.println("Available spots: " + (event.getCapacity() - currentRegistrations) + "/" + event.getCapacity());
        
        String name = InputHelper.getStringInput("\nEnter your full name: ");
        String email = InputHelper.getStringInput("Enter your email: ");
        String phone = InputHelper.getStringInput("Enter your phone number: ");
        
        // Check if email is already registered for this event
        for (Participant p : existingRegistrations) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("\n❌ Error: This email is already registered for this event!");
                return;
            }
        }
        
        try {
            // Create the participant
            Participant participant = participantService.createParticipant(name, email, phone, eventId);
            if (participant != null) {
                // Update the event's registration count
                event.setCurrentRegistrations(currentRegistrations + 1);
                eventService.saveEvent(event);
                
                System.out.println("\n✅ Registration successful!");
                System.out.println("Event: " + event.getName());
                System.out.println("Date: " + event.getDate());
                System.out.println("Your registration ID: " + participant.getId());
                System.out.println("Total registrations for this event: " + (currentRegistrations + 1) + "/" + event.getCapacity());
            } else {
                System.out.println("\n❌ Registration failed! Please try again.");
            }
        } catch (Exception e) {
            System.out.println("\n❌ An error occurred during registration: " + e.getMessage());
        }
    }

    private static void viewStudentRegistrations() {
        System.out.println("\nView My Registrations");
        String email = InputHelper.getStringInput("Enter your email: ");
        
        Participant participant = participantService.findParticipantByEmail(email);
        if (participant == null) {
            System.out.println("Student not found!");
            return;
        }

        List<Event> events = eventService.getAllEvents();
        boolean hasRegistrations = false;
        
        System.out.println("\nYour Event Registrations:");
        for (Event event : events) {
            List<Participant> registrations = participantService.getParticipantsByEvent(event.getId());
            if (registrations != null) {
                for (Participant p : registrations) {
                    if (p.getEmail().equals(email)) {
                        hasRegistrations = true;
                        System.out.println(event.getId() + ". " + event.getName() + 
                                         " (Date: " + event.getDate() + 
                                         ", Capacity: " + event.getCurrentRegistrations() + "/" + event.getCapacity() + ")");
                        break;
                    }
                }
            }
        }

        if (!hasRegistrations) {
            System.out.println("You are not registered for any events.");
        }
    }

    private static void cancelStudentRegistration() {
        System.out.println("\nCancel Registration");
        String email = InputHelper.getStringInput("Enter your email: ");
        
        // Find all participants with this email (user might be registered for multiple events)
        List<Participant> userRegistrations = new ArrayList<>();
        List<Event> allEvents = eventService.getAllEvents();
        
        for (Event event : allEvents) {
            List<Participant> participants = participantService.getParticipantsByEvent(event.getId());
            for (Participant p : participants) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    userRegistrations.add(p);
                }
            }
        }
        
        if (userRegistrations.isEmpty()) {
            System.out.println("No registrations found for this email!");
            return;
        }
        
        // Group registrations by event
        Map<Integer, List<Participant>> registrationsByEvent = new HashMap<>();
        for (Participant p : userRegistrations) {
            registrationsByEvent.computeIfAbsent(p.getEventId(), k -> new ArrayList<>()).add(p);
        }
        
        // Show events with registration IDs
        System.out.println("\nYour Registrations:");
        System.out.println("-------------------");
        
        for (Map.Entry<Integer, List<Participant>> entry : registrationsByEvent.entrySet()) {
            int eventId = entry.getKey();
            Event event = eventService.findEventById(eventId);
            if (event != null) {
                System.out.println("Event: " + event.getName() + " (ID: " + eventId + ")");
                System.out.println("Date: " + event.getDate());
                System.out.println("Your Registration IDs:");
                for (Participant p : entry.getValue()) {
                    System.out.println("- " + p.getId());
                }
                System.out.println("-------------------");
            }
        }
        
        int registrationId = InputHelper.getIntInput("\nEnter Registration ID to cancel: ");
        
        // Find the registration
        Participant registrationToCancel = null;
        for (Participant p : userRegistrations) {
            if (p.getId() == registrationId) {
                registrationToCancel = p;
                break;
            }
        }
        
        if (registrationToCancel == null) {
            System.out.println("Invalid Registration ID. Please select from your registrations.");
            return;
        }
        
        // Delete the registration
        if (participantService.deleteParticipant(registrationToCancel.getId())) {
            // The deleteParticipant method now handles the event registration count update
            System.out.println("\n✅ Registration cancelled successfully!");
            
            // Show updated event capacity
            Event cancelledEvent = eventService.findEventById(registrationToCancel.getEventId());
            if (cancelledEvent != null) {
                List<Participant> remainingRegistrations = participantService.getParticipantsByEvent(cancelledEvent.getId());
                System.out.println("Remaining capacity for " + cancelledEvent.getName() + ": " + 
                                 (cancelledEvent.getCapacity() - remainingRegistrations.size()) + 
                                 "/" + cancelledEvent.getCapacity());
            }
        } else {
            System.out.println("\n❌ Failed to cancel registration. Please try again.");
        }
    }
}