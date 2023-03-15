import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer implements Runnable{

    private  ScheduledExecutorService scheduler;
    private int tickDuration; // tick duration in milliseconds
    private volatile int ticks;
    private Boolean pause;
    private Integer numberOfJobs;

    public Timer(Integer tickDuration) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.ticks = 0;
        this.tickDuration = tickDuration;
        this.pause = false;
        // initialize number of jobs executed to 1 (timer thread)
        this.numberOfJobs = 1;
    }

    public void run() {
        // runs every fix amount of time, increases the current tick and notifies all waiting threads
        this.scheduler.scheduleAtFixedRate(() -> {
            if (!pause) {
                synchronized (this) {
                    this.ticks++;
                    notifyAll();
                }
            }
        }, this.tickDuration, this.tickDuration, TimeUnit.MILLISECONDS); //delay, time, time unit
    }

    // pauses the requesting thread for n amounts of ticks
    public void waitTicks(Integer nTicksToWait) throws InterruptedException {
        Integer stopTicks;
        if (nTicksToWait > 0) {
            synchronized (this) {
                stopTicks = ticks + nTicksToWait;
            }

            synchronized (this) {
                while (ticks < stopTicks) {
                    wait();
                }
            }
        }
    }

    public synchronized void increaseNumberOfJobs () {
        this.numberOfJobs++;
    }

    public synchronized void decreaseNumberOfJobs () {
        this.numberOfJobs--;
    }

    public synchronized Integer getNumberOfJobs () {
        return this.numberOfJobs;
    }

    public void stop() {
        this.scheduler.shutdown();
    }

    public void pauseResumeTimer() {
        this.pause = !this.pause;
    }

    public synchronized int getTicks() {
        return this.ticks;
    }

}
