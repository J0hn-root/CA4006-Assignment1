import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    private  ScheduledExecutorService scheduler;
    private int tickDuration; // tick duration in milliseconds
    private volatile int ticks;
    private Object lock = new Object();
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

    public void start() {
        this.scheduler.scheduleAtFixedRate(() -> {
            if (!pause) {
                //System.out.println("Ticks: "+ this.ticks);
                synchronized (lock) {
                    this.ticks++;
                    lock.notifyAll();
                }
            }
        }, this.tickDuration, this.tickDuration, TimeUnit.MILLISECONDS); //delay, time, time unit
    }

    public String getThreadName() {
        return  Thread.currentThread().getName();
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

    public int getTicks() {
        synchronized (lock) {
            return this.ticks;
        }
    }

    public void waitTicks(Integer nTicksToWait) throws InterruptedException {
        Integer stopTicks;
        if (nTicksToWait > 0) {
            synchronized (lock) {
                stopTicks = ticks + nTicksToWait;
            }

            synchronized (lock) {
                while (ticks < stopTicks) {
                    lock.wait();
                }
            }
        }
    }

}
