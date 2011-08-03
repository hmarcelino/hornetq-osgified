package com.humanet.messaging.hornetq.internal.hornetq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.core.config.Configuration;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.impl.JMSServerConfigParserImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConnectionsConfigurationParser {

    public final static Log log = LogFactory.getLog(ConnectionsConfigurationParser.class);

    private Configuration serverConfiguration;
    public String jsmConfigFile;

    private JMSConfiguration jmsConfiguration;

    public ConnectionsConfigurationParser(String jmsConfigFile, Configuration serverConfiguration) {
        this.jsmConfigFile = jmsConfigFile;
        this.serverConfiguration = serverConfiguration;
    }

    public void init() throws FileNotFoundException, HornetQException {
        final InputStream jmsXmlStream = new FileInputStream(new File(jsmConfigFile));

        try {
            jmsConfiguration = new JMSServerConfigParserImpl().parseConfiguration(jmsXmlStream);
        } catch (Exception e) {
            log.error("Error loading jms configuration file : " + jsmConfigFile);
        }

        for (ConnectionFactoryConfiguration cfConfig : jmsConfiguration.getConnectionFactoryConfigurations()) {
            getHornetQConnectionFactory(cfConfig);
        }
    }

    private HornetQConnectionFactory getHornetQConnectionFactory(ConnectionFactoryConfiguration cfConfig)
            throws HornetQException {


        HornetQConnectionFactory cf;
        if (cfConfig.getDiscoveryGroupName() != null) {
            DiscoveryGroupConfiguration groupConfig = serverConfiguration.getDiscoveryGroupConfigurations()
                    .get(cfConfig.getDiscoveryGroupName());

            if (groupConfig == null) {
                throw new HornetQException(
                        HornetQException.ILLEGAL_STATE,
                        "Discovery Group '" + cfConfig.getDiscoveryGroupName() + "' doesn't exist on maing config");
            }

            if (cfConfig.isHA()) {
                cf = HornetQJMSClient.createConnectionFactoryWithHA(groupConfig, cfConfig.getFactoryType());
            } else {
                cf = HornetQJMSClient.createConnectionFactoryWithoutHA(groupConfig, cfConfig.getFactoryType());
            }
        } else {
            if (cfConfig.getConnectorNames() == null || cfConfig.getConnectorNames().size() == 0) {
                throw new HornetQException(HornetQException.ILLEGAL_STATE,
                        "Null Connector name passed to create ConnectionFactory");
            }

            TransportConfiguration[] configs = new TransportConfiguration[cfConfig.getConnectorNames().size()];

            int count = 0;
            for (String name : cfConfig.getConnectorNames()) {
                TransportConfiguration connector = serverConfiguration.getConnectorConfigurations().get(name);
                if (connector == null) {
                    throw new HornetQException(HornetQException.ILLEGAL_STATE, "Connector '" + name +
                            "' not found on the main configuration file");
                }
                configs[count++] = connector;
            }

            if (cfConfig.isHA()) {
                cf = HornetQJMSClient.createConnectionFactoryWithHA(cfConfig.getFactoryType(), configs);
            } else {
                cf = HornetQJMSClient.createConnectionFactoryWithoutHA(cfConfig.getFactoryType(), configs);
            }
        }

        cf.setName(cfConfig.getName());
        cf.setClientID(cfConfig.getClientID());
        cf.setClientFailureCheckPeriod(cfConfig.getClientFailureCheckPeriod());
        cf.setConnectionTTL(cfConfig.getConnectionTTL());
        cf.setCallTimeout(cfConfig.getCallTimeout());
        cf.setCacheLargeMessagesClient(cfConfig.isCacheLargeMessagesClient());
        cf.setMinLargeMessageSize(cfConfig.getMinLargeMessageSize());
        cf.setConsumerWindowSize(cfConfig.getConsumerWindowSize());
        cf.setConsumerMaxRate(cfConfig.getConsumerMaxRate());
        cf.setConfirmationWindowSize(cfConfig.getConfirmationWindowSize());
        cf.setProducerWindowSize(cfConfig.getProducerWindowSize());
        cf.setProducerMaxRate(cfConfig.getProducerMaxRate());
        cf.setBlockOnAcknowledge(cfConfig.isBlockOnAcknowledge());
        cf.setBlockOnDurableSend(cfConfig.isBlockOnDurableSend());
        cf.setBlockOnNonDurableSend(cfConfig.isBlockOnNonDurableSend());
        cf.setAutoGroup(cfConfig.isAutoGroup());
        cf.setPreAcknowledge(cfConfig.isPreAcknowledge());
        cf.setConnectionLoadBalancingPolicyClassName(cfConfig.getLoadBalancingPolicyClassName());
        cf.setTransactionBatchSize(cfConfig.getTransactionBatchSize());
        cf.setDupsOKBatchSize(cfConfig.getDupsOKBatchSize());
        cf.setUseGlobalPools(cfConfig.isUseGlobalPools());
        cf.setScheduledThreadPoolMaxSize(cfConfig.getScheduledThreadPoolMaxSize());
        cf.setThreadPoolMaxSize(cfConfig.getThreadPoolMaxSize());
        cf.setRetryInterval(cfConfig.getRetryInterval());
        cf.setRetryIntervalMultiplier(cfConfig.getRetryIntervalMultiplier());
        cf.setMaxRetryInterval(cfConfig.getMaxRetryInterval());
        cf.setReconnectAttempts(cfConfig.getReconnectAttempts());
        cf.setFailoverOnInitialConnection(cfConfig.isFailoverOnInitialConnection());
        cf.setCompressLargeMessage(cfConfig.isCompressLargeMessages());
        cf.setGroupID(cfConfig.getGroupID());
        return cf;
    }

}
