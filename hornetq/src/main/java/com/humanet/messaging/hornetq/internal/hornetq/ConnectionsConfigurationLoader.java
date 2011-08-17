package com.humanet.messaging.hornetq.internal.hornetq;

import com.humanet.messaging.hornetq.internal.ConnectionFactoryProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.impl.JMSServerConfigParserImpl;

import javax.jms.ConnectionFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConnectionsConfigurationLoader implements ConnectionFactoryProvider {

    public final static Log log = LogFactory.getLog(ConnectionsConfigurationLoader.class);

    private Configuration serverConfiguration;
    public String jsmConfigFile;

    private JMSConfiguration jmsConfiguration;

    private Map<String, HornetQJMSConnectionFactory> hqCfMap = new HashMap<String, HornetQJMSConnectionFactory>();

    public ConnectionsConfigurationLoader(String jmsConfigFile, Configuration serverConfiguration) {
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
            hqCfMap.put(cfConfig.getName(), getHornetQConnectionFactory(cfConfig));
        }
    }

    private HornetQJMSConnectionFactory getHornetQConnectionFactory(ConnectionFactoryConfiguration cfConfig)
            throws HornetQException {

        HornetQJMSConnectionFactory cf;
        if (cfConfig.getDiscoveryGroupName() != null) {
            DiscoveryGroupConfiguration groupConfig = serverConfiguration.getDiscoveryGroupConfigurations()
                    .get(cfConfig.getDiscoveryGroupName());

            if (groupConfig == null) {
                throw new HornetQException(
                        HornetQException.ILLEGAL_STATE,
                        "Discovery Group '" + cfConfig.getDiscoveryGroupName() + "' doesn't exist on maing config");
            }

            cf = new HornetQJMSConnectionFactory(cfConfig.isHA(), groupConfig);

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

            cf = new HornetQJMSConnectionFactory(cfConfig.isHA(), configs);
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


    @Override
    public ConnectionFactory getConnectionFactory(String name) throws HornetQException {
        if (!hqCfMap.containsKey(name)) {
            throw new HornetQException(
                    HornetQException.ILLEGAL_STATE,
                    "Np connection factory found with name: " + name
            );
        }

        return hqCfMap.get(name);
    }
}
