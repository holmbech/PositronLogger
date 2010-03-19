package com.holmbech;

import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.helpers.CyclicBuffer;

public class PositronAppenderTest extends TestCase {
    private final String logFileName = "test.log";
    private Logger root = Logger.getLogger("Positron");

    public void testSimple() {
        final PositronAppender positronAppender = getPositronAppender();
        root.addAppender(positronAppender);
        final CyclicBuffer buffer = positronAppender.getBuffer();
        root.warn("x");
        assertEquals(1, buffer.length());
        root.warn("y");
        assertEquals(2, buffer.length());
        root.error("ERROR OCCURED");
        assertEquals(0, positronAppender.getBuffer().length());
    }

    public void testMinimumLevelToAdd() {
        final PositronAppender positronAppender = getPositronAppender();
        positronAppender.setLevelToLogBuffer(Level.ERROR);
        positronAppender.setMinimumLevelToAdd(Level.WARN);
        positronAppender.activateOptions();
        root.addAppender(positronAppender);
        root.debug("x 1");
        assertEquals(0, positronAppender.getBuffer().length());
        root.warn("x 1");
        assertEquals(1, positronAppender.getBuffer().length());
        root.error("x 1");
        assertEquals(0, positronAppender.getBuffer().length());
    }

    public void testLevelToLogBuffer() {
        final PositronAppender positronAppender = getPositronAppender();
        positronAppender.setLevelToLogBuffer(Level.DEBUG);
        positronAppender.activateOptions();
        root.addAppender(positronAppender);
        root.warn("x 1");
        assertEquals(0, positronAppender.getBuffer().length());
    }

    private PositronAppender getPositronAppender() {
        final PositronAppender positronAppender = new PositronAppender();
        positronAppender.setFile(logFileName);
        positronAppender.setLayout(new SimpleLayout());
        positronAppender.activateOptions();
        return positronAppender;
    }

}
