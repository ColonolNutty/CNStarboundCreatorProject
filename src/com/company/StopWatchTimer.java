package com.company;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 12:19 PM
 */
public class StopWatchTimer {
    private boolean started;
    private long _startTime;
    private long _endTime;
    private long _duration;
    private String _actionBeingPerformed;
    private CNLog _log;

    public StopWatchTimer(CNLog log) {
        _log = log;
    }

    public void start() {
        if(!started) {
            reset();
            _startTime = System.nanoTime();
            started = true;
        }
    }

    public void start(String action) {
        if(!started) {
            start();
            _actionBeingPerformed = action;
        }
    }

    public void stop() {
        if(started) {
            _endTime = System.nanoTime();
            _duration = (_endTime - _startTime);  //divide by 1000000 to get milliseconds.
            started = false;
        }
    }

    public void reset() {
        started = false;
        _startTime = 0;
        _endTime = 0;
        _duration = 0;
        _actionBeingPerformed = null;
    }

    public long timeInMilliseconds() {
        stop();
        return _duration/1000000;
    }

    public long timeInSeconds() {
        stop();
        return timeInMilliseconds()/1000;
    }

    public long timeInMinutes() {
        stop();
        return timeInSeconds() / 60;
    }

    public void logTime() {
        if(_log == null) {
            return;
        }
        long time = timeInMinutes();
        String unitOfMeasurement = "minutes";
        if(time == 0) {
            time = timeInSeconds();
            unitOfMeasurement = "seconds";
        }
        if(time == 0) {
            time = timeInMilliseconds();
            unitOfMeasurement = "milliseconds";
        }
        if(time == 0) {
            return;
        }
        String action = "running";
        if(_actionBeingPerformed != null) {
            action = _actionBeingPerformed;
        }
        _log.info("Finished " + action + " in " + time + " " + unitOfMeasurement);
    }
}
