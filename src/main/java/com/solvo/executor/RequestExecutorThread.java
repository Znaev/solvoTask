package com.solvo.executor;

import com.solvo.entity.RequestB;
import com.solvo.storage.RequestQueueStorage;
import com.solvo.entity.IRequest;
import com.solvo.entity.RequestA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestExecutorThread extends Thread {
    private final Logger logger = LogManager.getLogger(RequestExecutorThread.class);
    private Map<String, LinkedList<Thread>> pool = new HashMap<>();
    private RequestQueueStorage storage;
    private int capacity;
    private AtomicInteger requestACountProcessing;
    private AtomicInteger requestBCountProcessing;
    private Boolean isStop = false;

    public RequestExecutorThread(int capacity) {
        requestACountProcessing = new AtomicInteger(0);
        requestBCountProcessing = new AtomicInteger(0);
        this.capacity = capacity;
        storage = RequestQueueStorage.getInstance();
    }

    public void addRequest(IRequest request) {
        if (request.getClass().equals(RequestA.class)) {
            storage.getRequestAQueue().add((RequestA) request);
        }
        if (request.getClass().equals(RequestB.class)) {
            storage.getRequestBQueue().add((RequestB) request);
        }
        logger.info("Queue of A request: "+storage.getRequestAQueue().size()+". Queue of B request: "+storage.getRequestBQueue().size()+". Type of request incoming: "+request.getClass().getSimpleName());
    }

    @Override
    public void run() {
        do {
            if (requestACountProcessing.get() + requestBCountProcessing.get() < capacity) {
                IRequest request = getNextRequest();
                if (request != null) {
                    new Thread(() -> {
                        try {
                            processRequest(request);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while (!isStop || (storage.getRequestBQueue().size() + storage.getRequestAQueue().size() != 0));
    }

    public void finishAndStop() {
        isStop = true;
    }

    private void processRequest(IRequest request) throws InterruptedException {
        Thread lastInQueue = null;
        Thread current = Thread.currentThread();
        String key = request.getX().toString();
        synchronized (pool) {
            LinkedList<Thread> queue = pool.get(key);
            if (queue != null && queue.size() > 0) {
                lastInQueue = queue.getLast();
            }
            if (queue == null) {
                queue = new LinkedList<>();
                pool.put(key, queue);
            }
            queue.add(Thread.currentThread());
        }
        if (lastInQueue != null) {
            logger.info("Waiting for finish: "+lastInQueue);
            lastInQueue.join();
        }
        logger.info("Type of request: "+ request.getClass().getSimpleName() + ". X is " + request.getX());
        synchronized (pool) {
            pool.get(key).remove(current);
            if (request.getClass().equals(RequestA.class)) {
                requestACountProcessing.decrementAndGet();
            } else {
                requestBCountProcessing.decrementAndGet();
            }
        }

    }

    private IRequest getNextRequest() {
        int aCount = storage.getRequestAQueue().size();
        int bCount = storage.getRequestBQueue().size();
        if (aCount + bCount == 0) {
            return null;
        }
        if (aCount == 0) {
            requestBCountProcessing.incrementAndGet();
            return storage.getRequestBQueue().poll();
        }
        if (bCount == 0) {
            requestACountProcessing.incrementAndGet();
            return storage.getRequestAQueue().poll();
        }
        if (requestACountProcessing.get() >= capacity / 2) {
            requestBCountProcessing.incrementAndGet();
            return storage.getRequestBQueue().poll();
        } else {
            requestACountProcessing.incrementAndGet();
            return storage.getRequestAQueue().poll();
        }
    }
}
