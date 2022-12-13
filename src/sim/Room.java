package sim;

import java.util.*;

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
			Protocol.sendDeparture(controller);
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
			Protocol.sendArrival(controller);
		}
	}

	public void update(int entered, int left) throws Exception {
		System.out.println("Updating room, entered: " + entered + ", left: " + left);
		for (int i = 0; i < entered; i++) {
			leavingTimes.add(visitTimeGenerator.get() + now);
		}

		// Do nothing with left as they are already deleted from leavingTimes

		now = timer.now();
		elapsed = now - lastUpdate;
		lastUpdate = now;
		System.out.println("Updating room, elapsed: " + elapsed + " ms");

		arriving();
		leaving();
	}
}
