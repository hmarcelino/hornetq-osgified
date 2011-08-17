package com.humanet.messaging.hornetq;

import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class SilentTestCase {

    @BeforeClass
    public void turn_Logging_Off() throws IOException {
        //we need this to initialize the logger
        LogFactory.getLog(this.getClass());

        for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers()) {
            handler.setLevel(Level.SEVERE);
        }
    }

    public void turn_Logging_to(Level level) throws IOException {
        //we need this to initialize the logger
        LogFactory.getLog(this.getClass());

        for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers()) {
            handler.setLevel(level);
        }
    }

    @AfterClass
    public void turn_Logging_Back_On() {
        for (Handler handler : LogManager.getLogManager().getLogger("").getHandlers()) {
            handler.setLevel(Level.INFO);
        }
    }

}