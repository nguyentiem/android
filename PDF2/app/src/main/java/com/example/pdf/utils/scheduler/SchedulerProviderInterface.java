package com.example.pdf.utils.scheduler;

import io.reactivex.Scheduler;

public interface SchedulerProviderInterface {

    Scheduler computation();

    Scheduler io();

    Scheduler ui();
}
