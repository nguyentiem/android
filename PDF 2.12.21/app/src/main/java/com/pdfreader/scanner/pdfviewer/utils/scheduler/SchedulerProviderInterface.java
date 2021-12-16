package com.pdfreader.scanner.pdfviewer.utils.scheduler;

import io.reactivex.Scheduler;

public interface SchedulerProviderInterface {

    Scheduler computation();

    Scheduler io();

    Scheduler ui();
}
