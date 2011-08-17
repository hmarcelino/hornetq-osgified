package com.humanet.messaging.hornetq.internal.hornetq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.deployers.impl.FileConfigurationParser;

import java.io.File;
import java.io.FileInputStream;

public class ServerConfigurationLoader {

    public final static Log log = LogFactory.getLog(ConnectionsConfigurationLoader.class);

    public String configFile;
    private Configuration serverConfiguration;

    public ServerConfigurationLoader(String configFile) {
        this.configFile = configFile;
    }

    public void init() {
        try {
            FileInputStream configIs = new FileInputStream(new File(configFile));
            serverConfiguration = new FileConfigurationParser().parseMainConfig(configIs);
        } catch (Exception e) {
            log.error("Error loading hornetq server configuration file : " + configFile, e);
        }
    }

    public Configuration getConfiguration() {
        return serverConfiguration;
    }
}
