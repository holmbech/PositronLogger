package com.holmbech;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by IntelliJ IDEA.
 * User: andreaja
 * Date: Apr 7, 2010
 * Time: 9:19:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PluggablePositronAppender extends AppenderSkeleton implements AppenderAttachable {
    private CyclicBuffer buffer;
    private int bufferSize = 100;
    private Level levelToLogBuffer = Level.ERROR;
    private Level maximumLevelToStore = Level.INFO;

    private final AppenderAttachableImpl appenders;

    PluggablePositronAppender(){
        appenders = new AppenderAttachableImpl();
        buffer = new CyclicBuffer(bufferSize);
    }

    public void addAppender(final Appender appender) {
        synchronized (appenders) {
            appenders.addAppender(appender);
        }
    }

    @SuppressWarnings("unchecked")
    public Enumeration<Appender> getAllAppenders() {
        synchronized (appenders) {
            return appenders.getAllAppenders();
        }
    }

    public Appender getAppender(final String s) {
        synchronized (appenders) {
            return appenders.getAppender(s);
        }
    }

    public boolean isAttached(final Appender appender) {
        synchronized (appenders) {
            return isAttached(appender);
        }
    }

    public void removeAllAppenders() {
        synchronized (appenders) {
            appenders.removeAllAppenders();
        }
    }

    public void removeAppender(final Appender appender) {
        synchronized (appenders) {
            appenders.removeAppender(appender);
        }
    }

    public void removeAppender(final String s) {
        synchronized (appenders) {
            appenders.removeAppender(name);
        }
    }

    @Override
    protected void append(final LoggingEvent loggingEvent) {
        if (loggingEvent.getLevel().isGreaterOrEqual(levelToLogBuffer)) {
            addToBuffer(loggingEvent);
            writeAndClearBufferedEvents();
        } else if (!loggingEvent.getLevel().equals(maximumLevelToStore)
                && loggingEvent.getLevel().isGreaterOrEqual(maximumLevelToStore)) {
            synchronized (appenders) {
                appenders.appendLoopOnAppenders(loggingEvent);
            }
        } else {
            addToBuffer(loggingEvent);
        }
    }

    private void writeAndClearBufferedEvents() {
        List<LoggingEvent> events = null;
        synchronized (buffer) {
            events = clearBuffer();
        }
        synchronized (appenders) {
            for (LoggingEvent loggingEvent : events) {
                appenders.appendLoopOnAppenders(loggingEvent);
            }
        }
    }

    private List<LoggingEvent> clearBuffer() {
        List<LoggingEvent> events = new ArrayList<LoggingEvent>();
        LoggingEvent e = null;
        while((e=buffer.get()) != null){
            events.add(e);
        }
        return events;
    }

    private void addToBuffer(final LoggingEvent loggingEvent) {
        synchronized (buffer) {
            buffer.add(loggingEvent);
        }
    }

    public boolean requiresLayout() {
        return false;
    }

    public void close() {
        closed = true;
        appenders.removeAllAppenders();
    }

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
        synchronized (buffer) {
            buffer.resize(bufferSize);
        }
    }

    public void setLevelToLogBuffer(final Level levelToLogBuffer) {
        this.levelToLogBuffer = levelToLogBuffer;
    }

    public void setMaximumLevelToStore(final Level minimumLevelToAdd) {
        this.maximumLevelToStore = minimumLevelToAdd;
    }

}
