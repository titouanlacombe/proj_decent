import java.util.SortedSet;

class Room {
	private SortedSet<double> leavingTimes;
	private double entryRate;
	private lambda visitTimeGenerator;
	
	public Room(double entryRate, lambda visitTimeGenerator) {
		this.entryRate = entryRate;
		this.visitTimeGenerator = visitTimeGenerator;
		this.leavingTimes = new SortedSet<double>();
	}

	// Make a person enter the room
	public void entering() {
		leavingTimes.add(System.currentTimeMillis() + visitTimeGenerator());
	}

	// Make persons leave the room if they have to
	public void leaving() {
		time = System.currentTimeMillis();
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
