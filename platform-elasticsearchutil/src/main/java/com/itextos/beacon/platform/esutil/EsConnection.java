package com.itextos.beacon.platform.esutil;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import com.itextos.beacon.platform.esutil.utility.ElasticsearchProperties;

class EsConnection
{

    private static final Log    log              = LogFactory.getLog(EsConnection.class);

    private final long          mThreadId;
    private boolean             connectionIsOpen = false;
    private long                lastUsed         = 0;
    private RestHighLevelClient lEsClient        = null;

    public EsConnection(
            long aThreadId)
    {
        mThreadId = aThreadId;
    }

    RestHighLevelClient getConnection()
    {
        if ((lEsClient == null) || !connectionIsOpen)
            lEsClient = getEsClient(mThreadId);

        connectionIsOpen = true;
        updateLastUsed();

        return lEsClient;
    }

    void updateLastUsed()
    {
        lastUsed = System.currentTimeMillis();

        if (log.isDebugEnabled())
            log.debug("Thread Id : '" + mThreadId + "' Last used updated " + lastUsed);
    }

    void checkAndCloseClient()
    {
        boolean canClose = false;

        if (connectionIsOpen)
        {
            if ((lastUsed != 0) && ((System.currentTimeMillis() - lastUsed) > ElasticsearchProperties.getInstance().getExpireTime()))
                canClose = true;

            if (log.isDebugEnabled())
                log.debug("Check for close Thread Id : '" + mThreadId + "' connectionIsOpen " + connectionIsOpen + " Cur Time  : " + System.currentTimeMillis() + " Last used : " + lastUsed
                        + " Expire duration " + ElasticsearchProperties.getInstance().getExpireTime() + " >> Canclose ? " + canClose);
        }
        else
            if (log.isDebugEnabled())
                log.debug("Thread Id : '" + mThreadId + "' Connection is already closed");

        if (canClose)
            try
            {
                lEsClient.close();
                lastUsed         = 0;
                connectionIsOpen = false;
                log.error("Thread Id : '" + mThreadId + "' Closed unused Elasticsearch connection");
            }
            catch (final IOException e)
            {
                // ignore the exception.
            }
    }

    private static RestHighLevelClient getEsClient(
            long aThreadId)
    {
        final String[]   hosts     = ElasticsearchProperties.getInstance().getHostIps();
        final HttpHost[] hostArray = new HttpHost[hosts.length];
        int              index     = 0;

        for (final String tempHost : hosts)
        {
            final HttpHost host1 = new HttpHost(tempHost, ElasticsearchProperties.getInstance().getPort(), ElasticsearchProperties.getInstance().getScheme());
            hostArray[index] = host1;
            index++;
        }

        log.error("Thread Id : '" + aThreadId + "' Creating a Elasticsearch connection with the hosts " + Arrays.asList(hosts));

        final RestClientBuilder builder = RestClient.builder(hostArray).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(ElasticsearchProperties.getInstance().getConTimeoutMillis()).setSocketTimeout(ElasticsearchProperties.getInstance().getReadTimmeoutMillis()));

        return new RestHighLevelClient(builder);
    }

}