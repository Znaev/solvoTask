package com.solvo;

import com.solvo.entity.RequestA;
import com.solvo.entity.RequestB;
import com.solvo.executor.RequestExecutorThread;

public class Main {

    public static void main(String[] args) {
        RequestExecutorThread executor = new RequestExecutorThread(10);
        executor.start();
        for (int i = 0; i < 30; i++) {
            executor.addRequest(new RequestA(i%7));
            executor.addRequest(new RequestB(i%7));
        }
        executor.finishAndStop();
    }
}
