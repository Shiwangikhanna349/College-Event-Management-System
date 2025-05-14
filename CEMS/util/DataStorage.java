package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Event;

public class DataStorage {
    private static final String EVENTS_FILE = "events.txt";
    private static final String PARTICIPANTS_FILE = "participants.txt";

    public static void saveEvent(String eventData) {
        try (FileWriter writer = new FileWriter(EVENTS_FILE, true)) {
            writer.write(eventData + "\n");
        } catch (IOException e) {
            System.err.println("Error saving event: " + e.getMessage());
        }
    }

    public static List<String> loadEvents() {
        List<String> events = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                events.add(line);
            }
        } catch (IOException e) {
            
        }
        return events;
    }

    public static void saveParticipant(String participantData) {
        try (FileWriter writer = new FileWriter(PARTICIPANTS_FILE, true)) {
            writer.write(participantData + "\n");
        } catch (IOException e) {
            System.err.println("Error saving participant: " + e.getMessage());
        }
    }

    public static List<String> loadParticipants() {
        List<String> participants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PARTICIPANTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                participants.add(line);
            }
        } catch (IOException e) {
            
        }
        return participants;
    }

    public static void clearEvents() {
        try (FileWriter writer = new FileWriter(EVENTS_FILE)) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing events: " + e.getMessage());
        }
    }

    public static void clearParticipants() {
        try (FileWriter writer = new FileWriter(PARTICIPANTS_FILE)) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing participants: " + e.getMessage());
        }
    }

    public static void saveEvents(List<Event> events) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                writer.println(event.getId() + "," + event.getName() + "," + 
                             event.getDate() + "," + event.getVenue() + "," + 
                             event.getOrganizer() + "," + event.getType() + "," + 
                             event.getCapacity() + "," + event.getCurrentRegistrations());
            }
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
        }
    }
} 