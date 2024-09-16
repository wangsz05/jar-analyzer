/*
 * MIT License
 *
 * Copyright (c) 2023-2024 4ra1n (Jar Analyzer Team)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.n1ar4.dbg.core;

import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.util.concurrent.LinkedBlockingQueue;

public class DBGRunner {
    private static final Logger logger = LogManager.getLogger();
    private ThreadReference curThread;
    private final String mainClassName;
    private final DBGEngine engine;
    private final LinkedBlockingQueue<EventRequest> queue;

    public DBGEngine getEngine() {
        return engine;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public DBGRunner(String port, String mainClassName) {
        this(DBGEngine.HOST, port, mainClassName);
    }

    public DBGRunner(String ip, String port, String main) {
        this.mainClassName = main;
        this.engine = new DBGEngine(ip, port, DBGEngine.TRANSPORT);
        this.queue = new LinkedBlockingQueue<>();
        boolean success = this.engine.init();
        if (!success) {
            throw new RuntimeException("dbg runner start error");
        }
        logger.info("init java-dbg runner success");
    }

    public void sendIntoQueue(EventRequest request) {
        try {
            this.queue.put(request);
        } catch (Exception ex) {
            logger.error("send to queue error");
        }
    }

    public EventRequest getFromQueue() {
        try {
            return this.queue.take();
        } catch (Exception ex) {
            logger.error("take from queue error");
        }
        return null;
    }

    public void start() {
        new DBGThread(this).start();
    }

    public void doClassPrepare() {
        ClassPrepareRequest cpr = this.engine.createClassPrepareRequest(
                this.mainClassName);
        cpr.enable();
    }

    public void doBreakpoint(String className,
                             String methodName) {
        this.engine.createBreakpoint(
                className, methodName, null);
    }

    public void doStepOver() {
        if (this.curThread == null) {
            logger.error("cannot create step over");
            return;
        }
        StepRequest request = this.engine.createStepOverCodeRequest(this.curThread);
        request.enable();
        this.getEngine().getVm().resume();
    }

    public void doStepInto() {
        if (this.curThread == null) {
            logger.error("cannot create step into");
            return;
        }
        StepRequest request = this.engine.createStepIntoCodeRequest(this.curThread);
        request.enable();
        this.getEngine().getVm().resume();
    }

    public void doStepOut() {
        if (this.curThread == null) {
            logger.error("cannot create step out");
            return;
        }
        StepRequest request = this.engine.createStepOutCodeRequest(this.curThread);
        request.enable();
        this.getEngine().getVm().resume();
    }

    public void doBreakpointLoc(Location location, ThreadReference thread) {
        BreakpointRequest request = this.engine.createBreakpointLoc(location);
        request.enable();
        this.curThread = thread;
    }

    public void run() {
        this.getEngine().getVm().resume();
    }
}
