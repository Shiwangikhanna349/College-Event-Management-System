package service;

import model.Participant;
import util.DataStorage;
import java.util.ArrayList;
import java.util.List;

public class ParticipantService {
    private List<Participant> participants;
    private int nextParticipantId;

    public ParticipantService() {
        this.participants = new ArrayList<>();
        this.nextParticipantId = 1;
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
                participants.add(new Participant(id, name, email, phone, eventId));
                if (id >= nextParticipantId) {
                    nextParticipantId = id + 1;
                }
            }
        }
    }

    public Participant createParticipant(String name, String email, String phone, int eventId) {
        Participant participant = new Participant(nextParticipantId++, name, email, phone, eventId);
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
            participant.getEventId()
        );
        DataStorage.saveParticipant(participantData);
    }

    public boolean deleteParticipant(int id) {
        Participant participant = findParticipantById(id);
        if (participant != null) {
            participants.remove(participant);
            // Save all participants again
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
        List<Participant> result = new ArrayList<>();
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
        return new ArrayList<>(participants);
    }
} 