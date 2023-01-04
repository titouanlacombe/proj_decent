package sim;

import java.util.*;

import sim.protocol.*;
import utils.FullAddress;
import utils.NormalGenerator;

public class Room {
    private SortedSet<Double> leavingTimes;
    private HashMap<String, Controller> controllers;
    private HashMap<String, FullAddress> nodes;
    private double entryRate;
    private NormalGenerator visitTimeGenerator;
    private Clock clock;

    public Room(double entryRate, NormalGenerator visitTimeGenerator, Clock clock) {
        this.leavingTimes = new TreeSet<>();
        this.nodes = null;
        this.controllers = new HashMap<>();
        this.entryRate = entryRate;
        this.visitTimeGenerator = visitTimeGenerator;
        this.clock = clock;
    }

    public void setNodes(HashMap<String, FullAddress> nodes) {
        this.nodes = nodes;

        for (String uuid : nodes.keySet()) {
            controllers.put(uuid, new Controller());
        }
    }

    private String randomUuid() {
        int index = (int) (Math.random() * nodes.size());
        return new ArrayList<>(nodes.keySet()).get(index);
    }

    // Make persons leave the room if they have to
    private void leaving() throws Exception {
        while (!leavingTimes.isEmpty() && leavingTimes.first() < clock.now()) {
            leavingTimes.remove(leavingTimes.first());

            String uuid = randomUuid();
            // Update local controller
            controllers.get(uuid).departure();
            Protocol.send(nodes.get(uuid), new DepartureRequest());
        }
    }

    // Make persons wait at the doors
    private void arriving() throws Exception {
        double average = entryRate * clock.elapsed();
        int count = (int) average;

        // Add remainder to the count on a random basis
        double remainder = average - count;
        if (Math.random() < remainder) {
            count++;
        }

        for (int i = 0; i < count; i++) {
            String uuid = randomUuid();
            // Update local controller
            controllers.get(uuid).arrival();
            Protocol.send(nodes.get(uuid), new ArrivalRequest());
        }
    }

    public void update_controller(String sender_uuid, Controller updated) throws Exception {
        System.out.println("Update from " + sender_uuid + ": " + updated);

        // Compute change in entering
        Controller old = controllers.get(sender_uuid);
        int entered = old.get_entering() - updated.get_entering();
        for (int i = 0; i < entered; i++) {
            leavingTimes.add(visitTimeGenerator.get() + clock.now());
        }

        // Update controller
        controllers.put(sender_uuid, updated);
    }

    public void init_sim() {
        clock.startTimer();
    }

    public void update_people() throws Exception {
        arriving();
        leaving();

        clock.startTimer();

        // System.out.println("Leaving times: " + leavingTimes);
    }

    public HashMap<String, Controller> getControllers() {
        return controllers;
    }

    public int getNumber() {
        return leavingTimes.size();
    }
}
