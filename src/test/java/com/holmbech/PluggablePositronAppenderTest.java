package com.holmbech;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.helpers.CyclicBuffer;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by IntelliJ IDEA. User: andreaja Date: Apr 8, 2010 Time: 8:54:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PluggablePositronAppenderTest extends TestCase {

    private final String logFileName = "test.log";
    private Logger root = Logger.getLogger("Positron");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root.removeAllAppenders();
    }

    public void testSimple() {
        final PluggablePositronAppender positronAppender = getPositronAppender();
        root.addAppender(positronAppender);
        final CyclicBuffer buffer = getBuffer(positronAppender);
        root.debug("x");
        assertEquals(1, buffer.length());
        root.debug("y");
        assertEquals(2, buffer.length());
        root.error("ERROR OCCURED");
        assertEquals(0, getBuffer(positronAppender).length());
    }

    public void testMaximumLevelToStore() {
        final PluggablePositronAppender positronAppender = getPositronAppender();
        positronAppender.setLevelToLogBuffer(Level.ERROR);
        positronAppender.setMaximumLevelToStore(Level.DEBUG);
        positronAppender.activateOptions();
        root.addAppender(positronAppender);
        root.warn("x 1");
        assertEquals(0, getBuffer(positronAppender).length());
        root.debug("x 1");
        assertEquals(1, getBuffer(positronAppender).length());
        root.error("x 1");
        assertEquals(0, getBuffer(positronAppender).length());
    }

    public void testLevelToLogBuffer() {
        final PluggablePositronAppender positronAppender = getPositronAppender();
        positronAppender.setLevelToLogBuffer(Level.DEBUG);
        positronAppender.activateOptions();
        root.addAppender(positronAppender);
        root.warn("x 1");
        assertEquals(0, getBuffer(positronAppender).length());
    }

    public void testCycling() throws Exception {
        final PluggablePositronAppender positronAppender = getPositronAppender();
        root.addAppender(positronAppender);
        positronAppender.setBufferSize(3);
        positronAppender.activateOptions();

        root.debug("1");
        root.debug("2");
        root.debug("3");
        root.debug("4");
        root.debug("5");

        final CyclicBuffer buffer = getBuffer(positronAppender);
        assertEquals(3, buffer.length());
        assertEquals("3", buffer.get().getMessage());

        root.error("6");
    }

    private CyclicBuffer getBuffer(final PluggablePositronAppender positronAppender) {
        return (CyclicBuffer) ReflectionTestUtils.getField(positronAppender, "buffer");
    }

    private PluggablePositronAppender getPositronAppender() {
        final RollingFileAppender rollingFileAppender = new RollingFileAppender();
        rollingFileAppender.setFile(logFileName);
        rollingFileAppender.setLayout(new SimpleLayout());
        rollingFileAppender.activateOptions();

        final PluggablePositronAppender pluggablePositronAppender = new PluggablePositronAppender();
        pluggablePositronAppender.addAppender(rollingFileAppender);

        return pluggablePositronAppender;
    }
}
