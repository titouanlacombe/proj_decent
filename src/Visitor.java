import java.time.LocalDate;

public class Visitor {

    private int id;
    private int lifetime;

    // logs
    private LocalDate timein;
    private LocalDate timeout;
    private LocalDate timeinqueue;
    private LocalDate timeoutqueue;

    public Visitor(int id, int lifetime) {
        this.id = id;
        this.lifetime = lifetime;
    }

    public int getId() {
        return id;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void enter() {
        this.timein = LocalDate.now();
    }

    public void enterQueue() {
        this.timeinqueue = LocalDate.now();
    }

    public void leave() {
        this.timeout = LocalDate.now();
    }

    public void leaveQueue() {
        this.timeoutqueue = LocalDate.now();
    }

    public boolean hasToLeave() {
        return lifetime == 0;
    }

}
