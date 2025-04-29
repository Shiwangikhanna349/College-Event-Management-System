import model.Event;
import model.Participant;
import service.EventService;
import service.ParticipantService;
import util.InputHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
        
        System.out.println("\nAll Events:");
        for (Event event : events) {
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
        
        System.out.println("\nAvailable Events:");
        for (Event event : events) {
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
        
        LocalDate today = LocalDate.now();
        System.out.println("\nUpcoming Events:");
        boolean found = false;
        
        for (Event event : events) {
            if (event.getDate().isAfter(today) || event.getDate().isEqual(today)) {
                found = true;
                System.out.println(event.getId() + ". " + event.getName() + 
                                 " (Date: " + event.getDate() + 
                                 ", Capacity: " + event.getCurrentRegistrations() + "/" + event.getCapacity() + ")");
            }
        }
        
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
        viewAllEventsForStudents();
        int eventId = InputHelper.getIntInput("Enter event ID to register: ");
        Event event = eventService.findEventById(eventId);
        
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }
        
        if (event.getCurrentRegistrations() >= event.getCapacity()) {
            System.out.println("Event is full!");
            return;
        }
        
        String name = InputHelper.getStringInput("Enter your name: ");
        String email = InputHelper.getStringInput("Enter your email: ");
        String phone = InputHelper.getStringInput("Enter your phone number: ");
        
        Participant participant = participantService.createParticipant(name, email, phone, eventId);
        if (participant != null) {
            eventService.incrementEventRegistrations(eventId);
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed!");
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
        
        Participant participant = participantService.findParticipantByEmail(email);
        if (participant == null) {
            System.out.println("Student not found!");
            return;
        }

        // Show events student is registered for
        List<Event> events = eventService.getAllEvents();
        List<Event> registeredEvents = new ArrayList<>();
        
        System.out.println("\nYour Event Registrations:");
        for (Event event : events) {
            List<Participant> registrations = participantService.getParticipantsByEvent(event.getId());
            if (registrations != null) {
                for (Participant p : registrations) {
                    if (p.getEmail().equals(email)) {
                        registeredEvents.add(event);
                        System.out.println(event.getId() + ". " + event.getName());
                        break;
                    }
                }
            }
        }

        if (registeredEvents.isEmpty()) {
            System.out.println("You are not registered for any events.");
            return;
        }

        int eventId = InputHelper.getIntInput("\nEnter Event ID to cancel registration: ");
        
        // Verify the event ID is valid
        boolean validEventId = false;
        for (Event event : registeredEvents) {
            if (event.getId() == eventId) {
                validEventId = true;
                break;
            }
        }

        if (!validEventId) {
            System.out.println("Invalid Event ID. Please select from your registered events.");
            return;
        }

        if (participantService.deleteParticipant(participant.getId())) {
            eventService.decrementEventRegistrations(eventId);
            System.out.println("Registration cancelled successfully!");
        }
    }
} 