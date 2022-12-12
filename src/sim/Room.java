package sim;

import java.util.*;

import utils.FullAddress;
import utils.NormalGenerator;
import utils.Timer;

public class Room {
	private SortedSet<Double> leavingTimes;
	private FullAddress[] controllers;
	private double entryRate;
	private Timer timer;
	private double lastUpdate;
	private double now;
	private NormalGenerator visitTimeGenerator;

	public Room(double timeScale, double entryRate, NormalGenerator visitTimeGenerator) {
		this.entryRate = entryRate;
		this.visitTimeGenerator = visitTimeGenerator;
		this.leavingTimes = new TreeSet<Double>(); // TODO verify that this is the right type

		this.timer = new Timer(timeScale);
	}

	private double elapsed() {
		return timer.now() - lastUpdate;
	}

	private FullAddress randomController() {
		return controllers[(int) (Math.random() * controllers.length)];
	}

	// Make a person enter the room
	public void entering() {
		leavingTimes.add(visitTimeGenerator.get() + timer.now());
	}

	private void arrive() {
		FullAddress controller = randomController();
		// TODO call arrive event
	}

	// Make persons leave the room if they have to
	public void leaving() {
		while (leavingTimes.first() < now) {
			leavingTimes.remove(leavingTimes.first());

			FullAddress controller = randomController();
			// TODO call leave event
		}
	}

	// Make persons wait at the doors
	public void arriving() {
		double average = entryRate * elapsed();
		int count = (int) average;

		// Add remainder to the count on a random basis
		double remainder = average - count;
		if (Math.random() < remainder) {
			count++;
		}

		System.out.println(count + " persons arriving");
		for (int i = 0; i < count; i++) {
			arrive();
		}
	}

	public void update(int entered, int left) {
		System.out.println("Updating room, entered: " + entered + ", left: " + left);
		for (int i = 0; i < entered; i++) {
			entering();
		}

		// Do nothing with left as they are already deleted from leavingTimes

		now = timer.now();
		System.out.println("Updating room, elapsed: " + (elapsed() * 1000) + " ms");

		arriving();
		leaving();
	}
}
