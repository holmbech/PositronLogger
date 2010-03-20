/*
 * Copyright (c) 2010 Anders Holmbech Brandt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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