import com.solvo.entity.RequestA;
import com.solvo.entity.RequestB;
import com.solvo.executor.RequestExecutorThread;
import com.solvo.storage.RequestQueueStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RequestExecutorTest extends Assert {
    private RequestExecutorThread executor;
    private int countA,countB;

    @Before
    public void setUp(){
        executor=new RequestExecutorThread(10);
        countA=20;
        countB=20;
    }


    @Test
    public void testExecutor() throws InterruptedException {
        RequestQueueStorage storage=RequestQueueStorage.getInstance();
        executor.start();
        for(int i=0;i<countA;i++){
            executor.addRequest(new RequestA(i%7));
        }
        for(int i=0;i<countB;i++){
            executor.addRequest(new RequestB(i%7));
        }
        executor.finishAndStop();
        executor.join();

        assertEquals(storage.getRequestAQueue().size()+storage.getRequestBQueue().size(),0);

    }
    @Test
    public void testExecutorWithStartAfter() throws InterruptedException {
        RequestQueueStorage storage=RequestQueueStorage.getInstance();
        for(int i=0;i<countA;i++){
            executor.addRequest(new RequestA(i%7));
        }
        for(int i=0;i<countB;i++){
            executor.addRequest(new RequestB(i%7));
        }
        executor.start();
        executor.finishAndStop();
        executor.join();

        assertEquals(storage.getRequestAQueue().size()+storage.getRequestBQueue().size(),0);

    }
}
