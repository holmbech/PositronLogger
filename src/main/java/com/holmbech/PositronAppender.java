package com.holmbech;

import org.apache.log4j.Level;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.spi.LoggingEvent;

public class PositronAppender extends RollingFileAppender {
    private CyclicBuffer buffer = new CyclicBuffer(100);
    private int bufferSize = 100;
    private Level levelToLogBuffer = Level.ERROR;
    private Level minimumLevelToAdd = Level.DEBUG;

    @Override
    public void append(LoggingEvent event) {
        if (event.getLevel().isGreaterOrEqual(levelToLogBuffer)) {
            buffer.add(event);
            writeBufferedEvents();
            buffer = new CyclicBuffer(bufferSize);
        } else if (event.getLevel().isGreaterOrEqual(minimumLevelToAdd)) {
            buffer.add(event);
        }
    }

    private void writeBufferedEvents() {
        for (int i = 0; i < buffer.length(); i++) {
            super.append(buffer.get(i));
        }
    }

    /**
     * Default is {@link Level#DEBUG}
     *
     * @param minimumLevelToAdd the minimum level to include in the buffer. The given level is included
     */
    public void setMinimumLevelToAdd(Level minimumLevelToAdd) {
        this.minimumLevelToAdd = minimumLevelToAdd;
    }

    /**
     * The levelToLogBuffer to use for logging all the events in the log buffer
     * Default is {@link Level#ERROR}
     *
     * @param levelToLogBuffer the levelToLogBuffer at which is logged
     */
    public void setLevelToLogBuffer(Level levelToLogBuffer) {
        this.levelToLogBuffer = levelToLogBuffer;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer.resize(bufferSize);
    }

    CyclicBuffer getBuffer() {
        return buffer;
    }
}