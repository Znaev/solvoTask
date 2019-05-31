package com.solvo.storage;

import com.solvo.entity.RequestA;
import com.solvo.entity.RequestB;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueueStorage {
    private static RequestQueueStorage instance;

    static {
        instance = new RequestQueueStorage();
    }

    public static RequestQueueStorage getInstance() {
        return instance;
    }

    public Queue<RequestA> getRequestAQueue() {
        return requestAQueue;
    }

    public Queue<RequestB> getRequestBQueue() {
        return requestBQueue;
    }

    private RequestQueueStorage() {
        requestAQueue = new LinkedBlockingQueue<>();
        requestBQueue = new LinkedBlockingQueue<>();
    }

    private Queue<RequestA> requestAQueue;
    private Queue<RequestB> requestBQueue;
}
