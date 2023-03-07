import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    private  ScheduledExecutorService scheduler;
    private int tickDuration; // tick duration in milliseconds
    private volatile int ticks;
    private Object lock = new Object();
    private Boolean pause;

    public Timer(Integer tickDuration) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.ticks = 0;
        this.tickDuration = tickDuration;
        this.pause = false;
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

    public void stop() {
        this.scheduler.shutdown();
    }

    public void pauseTimer() {
        this.pause = true;
    }

    public void resumeTimer() {
        this.pause = false;
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
