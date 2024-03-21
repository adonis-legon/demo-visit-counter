package com.example.demovisitcounter;

import java.util.List;

public interface VisitCounterObserver {
    public void onOperationFinished(List<Long> executionTimes);
}
