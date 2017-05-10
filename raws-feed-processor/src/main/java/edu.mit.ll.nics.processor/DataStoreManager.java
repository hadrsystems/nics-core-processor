package edu.mit.ll.nics.processor;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataStoreManager {

    private static final Logger logger = Logger.getLogger(DataStoreManager.class.getSimpleName());

    private String dbType;
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private DataStore dataStore;
    private int dataStoreExpiryTimeInSeconds;
    private Stopwatch stopWatch;

    public DataStoreManager(String dbType, String dbHost, String dbPort, String dbName, String dbUser, String dbPassword, int dataStoreExpiryTimeInSeconds, Stopwatch stopWatch) throws IOException {
        this.dbType = dbType;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dataStoreExpiryTimeInSeconds = dataStoreExpiryTimeInSeconds;
        this.stopWatch = stopWatch;
    }

    public DataStoreManager(String dbType, String dbHost, String dbPort, String dbName, String dbUser, String dbPassword, int dataStoreExpiryTimeInSeconds) throws IOException {
        this.dbType = dbType;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dataStoreExpiryTimeInSeconds = dataStoreExpiryTimeInSeconds;
        this.stopWatch = Stopwatch.createUnstarted();
    }


    public DataStore getInstance() throws Exception {
        try {
            if(dataStore == null) {
                this.initializeDataStore();
            } else if(stopWatch.elapsed(TimeUnit.SECONDS) >= dataStoreExpiryTimeInSeconds) {
                dataStore.dispose();
                stopWatch.reset();
                this.initializeDataStore();
            }
        } catch(IOException e) {
            logger.error("Error initializing DataStore", e);
            throw e;
        }
        return dataStore;
    }

    private void initializeDataStore() throws Exception {
        Map<String, String> datastoreParams = new HashMap<String, String>();
        datastoreParams.put("dbtype", dbType);
        datastoreParams.put("host", dbHost);
        datastoreParams.put("port", dbPort);
        datastoreParams.put("database", dbName);
        datastoreParams.put("user", dbUser);
        datastoreParams.put("passwd", dbPassword);

        if(isBlank(dbType) || isBlank(dbHost) || isBlank(dbPort) || isBlank(dbName)
                || isBlank(dbUser) || isBlank(dbPassword)) {
            throw new Exception("Unable to initialize DataStore, please check data store connection properties");
        }

        stopWatch.start();
        dataStore = DataStoreFinder.getDataStore(datastoreParams);
    }

    private void disposeDataStore() {
        if(dataStore != null)
            dataStore.dispose();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}