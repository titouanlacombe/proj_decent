package sim;

import java.util.*;

import sim.protocol.*;
import utils.FullAddress;
import utils.NormalGenerator;
import utils.Timer;

public class Room {
	private SortedSet<Double> leavingTimes;
	private HashMap<String, Controller> controllers;
	private HashMap<String, FullAddress> nodes;
	private double entryRate;
	private NormalGenerator visitTimeGenerator;

	private Timer timer;
	private double lastUpdate;
	private double elapsed;
	private double now;

	public Room(double timeScale, double entryRate, NormalGenerator visitTimeGenerator) {
		this.leavingTimes = new TreeSet<>(); // TODO verify that this is the right type
		this.nodes = null;
		this.controllers = new HashMap<>();
		this.entryRate = entryRate;
		this.visitTimeGenerator = visitTimeGenerator;

		this.timer = new Timer(timeScale);
		this.lastUpdate = timer.now();
	}

	public void setNodes(HashMap<String, FullAddress> nodes) {
		this.nodes = nodes;
	}

	private String randomUuid() {
		int index = (int) (Math.random() * nodes.size());
		return new ArrayList<>(nodes.keySet()).get(index);
	}

	// Make persons leave the room if they have to
	public void leaving() throws Exception {
		while (!leavingTimes.isEmpty() && leavingTimes.first() < now) {
			leavingTimes.remove(leavingTimes.first());

			FullAddress node = nodes.get(randomUuid());
			Protocol.send(node, new DepartureRequest());
		}
	}

	// Make persons wait at the doors
	public void arriving() throws Exception {
		double average = entryRate * elapsed;
		int count = (int) average;

		// Add remainder to the count on a random basis
		double remainder = average - count;
		if (Math.random() < remainder) {
			count++;
		}

		for (int i = 0; i < count; i++) {
			FullAddress node = nodes.get(randomUuid());
			Protocol.send(node, new ArrivalRequest());
		}
	}

	public void update(String sender_uuid, Controller updated) throws Exception {
		System.out.println("Update from " + sender_uuid + ": " + updated);

		// Compute change in entering
		Controller old = controllers.get(sender_uuid);
		if (old == null) {
			old = new Controller();
		}

		int entered = old.get_entering() - updated.get_entering();
		for (int i = 0; i < entered; i++) {
			leavingTimes.add(visitTimeGenerator.get() + now);
		}

		// Update controller
		controllers.put(sender_uuid, updated);

		// Update arring/leaving
		now = timer.now();
		elapsed = now - lastUpdate;
		lastUpdate = now;

		arriving();
		leaving();
	}
}
