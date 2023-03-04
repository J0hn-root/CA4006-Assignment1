import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {

    private  ScheduledExecutorService scheduler;
    private static int tickDuration; // tick duration in milliseconds
    private static volatile int ticks;
    private Object lock = new Object();

    public Timer(Integer tickDuration) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.ticks = 0;
        this.tickDuration = tickDuration;
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(() -> {
            this.ticks++;
            //System.out.println("Ticks: "+ this.ticks);
            synchronized (lock) {
                lock.notifyAll();
            }
        }, this.tickDuration, this.tickDuration, TimeUnit.MILLISECONDS); //delay, time, time unit
    }

    public void stop() {
        this.scheduler.shutdown();
    }

    public int getTicks() {
        return this.ticks;
    }

    public void waitTicks(Integer nTicksToWait) throws InterruptedException {
        if (nTicksToWait > 0) {
            Integer stopTicks = getTicks() + nTicksToWait;
            synchronized (lock) {
                while (getTicks() < stopTicks) {
                    lock.wait();
                }
            }
        }
    }
}
