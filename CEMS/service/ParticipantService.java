package service;

import model.Participant;
import util.DataStorage;
import java.util.LinkedList;
import java.util.List;

import model.Event;

public class ParticipantService {
    private List<Participant> participants;
    private int nextParticipantId;
    private EventService eventService;

    public ParticipantService() {
        this.participants = new LinkedList<>(); 
        this.nextParticipantId = 1;
        this.eventService = new EventService();
        loadParticipants();
    }

    private void loadParticipants() {
        List<String> participantData = DataStorage.loadParticipants();
        for (String data : participantData) {
            String[] parts = data.split(",");
            if (parts.length == 5) {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                String phone = parts[3];
                int eventId = Integer.parseInt(parts[4]);

                // Create and add participant
                Participant participant = new Participant(id, name, email, phone, eventId);
                participants.add(participant);

                // Update the event's registration count
                Event event = eventService.findEventById(eventId);
                if (event != null) {
                    event.setCurrentRegistrations(event.getCurrentRegistrations() + 1);
                    eventService.saveEvent(event);
                }

                // Update nextParticipantId to be max ID + 1
                if (id >= nextParticipantId) {
                    nextParticipantId = id + 1;
                }
            }
        }
    }

    public Participant createParticipant(String name, String email, String phone, int eventId) {
        // Always use the next available ID
        int newId = nextParticipantId++;
        Participant participant = new Participant(newId, name, email, phone, eventId);
        participants.add(participant);
        saveParticipant(participant);
        return participant;
    }

    private void saveParticipant(Participant participant) {
        String participantData = String.format("%d,%s,%s,%s,%d",
                participant.getId(),
                participant.getName(),
                participant.getEmail(),
                participant.getPhone(),
                participant.getEventId());
        DataStorage.saveParticipant(participantData);
    }

    public boolean deleteParticipant(int id) {
        Participant participantToDelete = findParticipantById(id);
        if (participantToDelete != null) {
            // Decrement the event's registration count
            Event event = eventService.findEventById(participantToDelete.getEventId());
            if (event != null) {
                event.setCurrentRegistrations(Math.max(0, event.getCurrentRegistrations() - 1));
                eventService.saveEvent(event);
            }

            // Remove the participant
            participants.remove(participantToDelete);

            // Create a new list with updated IDs
            List<Participant> updatedParticipants = new LinkedList<>();
            int currentId = 1;

            // Add all participants with updated IDs
            for (Participant p : participants) {
                // Create a new participant with the new ID
                Participant updated = new Participant(
                        currentId++,
                        p.getName(),
                        p.getEmail(),
                        p.getPhone(),
                        p.getEventId());
                updatedParticipants.add(updated);
            }

            // Update the participants list and nextParticipantId
            participants = updatedParticipants;
            nextParticipantId = currentId;

            // Save all participants with their new IDs
            DataStorage.clearParticipants();
            for (Participant p : participants) {
                saveParticipant(p);
            }

            return true;
        }
        return false;
    }

    public Participant findParticipantById(int id) {
        for (Participant participant : participants) {
            if (participant.getId() == id) {
                return participant;
            }
        }
        return null;
    }

    public List<Participant> getParticipantsByEvent(int eventId) {
        List<Participant> result = new LinkedList<>();
        for (Participant participant : participants) {
            if (participant.getEventId() == eventId) {
                result.add(participant);
            }
        }
        return result;
    }

    public Participant findParticipantByEmail(String email) {
        for (Participant participant : participants) {
            if (participant.getEmail().equals(email)) {
                return participant;
            }
        }
        return null;
    }

    public List<Participant> getAllParticipants() {
        return new LinkedList<>(participants);
    }
}