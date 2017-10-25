package maintain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private ExecutorService cachedPool;
    private static ThreadManager ourInstance = new ThreadManager();

    public static ThreadManager getInstance() {
        return ourInstance;
    }

    private ThreadManager() {
        cachedPool = Executors.newCachedThreadPool();
    }

    public <E extends Runnable> void execute(E runnable){
        cachedPool.execute(runnable);
    }

}
