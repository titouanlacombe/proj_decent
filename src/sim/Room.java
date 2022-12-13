package sim;

import java.util.*;

import sim.protocol.*;
import utils.FullAddress;
import utils.NormalGenerator;
import utils.Timer;

public class Room {
	private SortedSet<Double> leavingTimes;
	private FullAddress[] controllers;
	private double entryRate;
	private NormalGenerator visitTimeGenerator;

	private Timer timer;
	private double lastUpdate;
	private double elapsed;
	private double now;

	public Room(double timeScale, double entryRate, NormalGenerator visitTimeGenerator) {
		this.leavingTimes = new TreeSet<Double>(); // TODO verify that this is the right type
		this.controllers = null;
		this.entryRate = entryRate;
		this.visitTimeGenerator = visitTimeGenerator;

		this.timer = new Timer(timeScale);
		this.lastUpdate = timer.now();
	}

	public void setControllers(FullAddress[] controllers) {
		this.controllers = controllers;
	}

	private FullAddress randomController() {
		return controllers[(int) (Math.random() * controllers.length)];
	}

	// Make persons leave the room if they have to
	public void leaving() throws Exception {
		while (!leavingTimes.isEmpty() && leavingTimes.first() < now) {
			leavingTimes.remove(leavingTimes.first());

			FullAddress controller = randomController();
			System.out.println("Person leaving to " + controller);
			Protocol.send(controller, new DepartureRequest());
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

		System.out.println(count + " persons arriving");
		for (int i = 0; i < count; i++) {
			FullAddress controller = randomController();
			System.out.println("Person arriving to " + controller);
			Protocol.send(controller, new ArrivalRequest());
		}
	}

	public void update(String sender_uuid, Controller updated) throws Exception {
		System.out.println("Updating controller " + sender_uuid);

		// Update controller state
		// Controller old = controllers.get(sender_uuid);
		Controller old = null;
		if (old == null) {
			old = new Controller();
		}

		int entered = old.get_entering() - updated.get_entering();
		for (int i = 0; i < entered; i++) {
			leavingTimes.add(visitTimeGenerator.get() + now);
		}

		// controllers.put(sender_uuid, updated);

		// Update arring/leaving
		now = timer.now();
		elapsed = now - lastUpdate;
		lastUpdate = now;
		System.out.println("Updating room, elapsed: " + elapsed + " ms");

		arriving();
		leaving();
	}
}
