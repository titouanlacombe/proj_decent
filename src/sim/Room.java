package sim;

import java.util.*;
import utils.NormalGenerator;

public class Room {
	private SortedSet<Double> leavingTimes;
	private double entryRate;
	private Timer timer;
	private NormalGenerator visitTimeGenerator;
	
	public Room(double entryRate, NormalGenerator visitTimeGenerator) {
		this.entryRate = entryRate;
		this.visitTimeGenerator = visitTimeGenerator;
		this.leavingTimes = new TreeSet<Double>(); // TODO verify that this is the right type

		this.timer = new Timer();
	}

	// Make a person enter the room
	public void entering() {
		leavingTimes.add(visitTimeGenerator.get() + timer.now());
	}

	// Make persons leave the room if they have to
	public void leaving() {
		Double time = timer.now();
		while (leavingTimes.first() < time) {
			leavingTimes.remove(leavingTimes.first());
			// TODO call random server (output door random)
		}
	}

	// Make persons wait at the doors
	public void arriving() {
		for (int i = 0; i < (int) entryRate; i++) {
			arrive();
		}

		if (Math.random() < entryRate % 1) {
			arrive();
		}
	}

	private void arrive() {
		// TODO call random server (input door random)
	}

	public void run() {
		arriving();
		leaving();
	}
}
