package edu.mit.ll.nics.processor;

//import org.apache.camel.util.StopWatch;
import com.google.common.base.Stopwatch;
import org.geotools.data.DataStore;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class DataStoreManagerTest {
    private String dbType ="postgis";
    private String dbHost ="testhost";
    private String dbPort = "1001";
    private String dbName = "raws";
    private String dbUser ="user";
    private String dbPassword = "pwd";
    private int dataStoreExpiryTimeInSeconds = 3;
    private Stopwatch stopWatch = Stopwatch.createUnstarted();
    private DataStoreManager dataStoreManager;

    @BeforeTest
    public void setup() throws IOException {
        dataStoreManager = new DataStoreManager(dbType, dbHost, dbPort, dbName, dbUser, dbPassword, dataStoreExpiryTimeInSeconds, stopWatch);
    }

    @Test(expectedExceptions = Exception.class)
    public void testDataStoreInitializationThrowsExceptionWhenAnyConnectionPropertiesAreMissing() throws Exception {
        DataStoreManager dataStoreManager = new DataStoreManager(null, dbHost, dbPort, dbName, dbUser, dbPassword, dataStoreExpiryTimeInSeconds, stopWatch);
        dataStoreManager.getInstance();
    }

    @Test(expectedExceptions = Exception.class)
    public void testDataStoreInitializationThrowsExceptionWhenAnyConnectionPropertiesAreBlank() throws Exception {
        DataStoreManager dataStoreManager = new DataStoreManager(dbType, "    ", dbPort, dbName, dbUser, dbPassword, dataStoreExpiryTimeInSeconds, stopWatch);
        dataStoreManager.getInstance();
    }

    @Test
    public void testDataStoreIsInitializedSuccessfullyFirstTime() throws Exception {
        DataStore dataStore = dataStoreManager.getInstance();
        Assert.assertTrue(stopWatch.isRunning());
        Assert.assertNotNull(dataStore);
    }

    @Test
    public void testDataStoreIsdReinitizliedAnd() throws Exception {
        DataStore dataStore1 = dataStoreManager.getInstance();
        //wait for time to pass so that dataStore is ready to be reinitialized
        Thread.sleep(dataStoreExpiryTimeInSeconds * 1000);

        System.out.println("elapsed: " + stopWatch.elapsed(TimeUnit.SECONDS));
        Assert.assertTrue(stopWatch.elapsed(TimeUnit.SECONDS) >= dataStoreExpiryTimeInSeconds);

        DataStore dataStore2 = dataStoreManager.getInstance();
        Assert.assertNotEquals(dataStore1, dataStore2);
        Assert.assertTrue(stopWatch.isRunning());
        Assert.assertTrue(stopWatch.elapsed(TimeUnit.SECONDS) < this.dataStoreExpiryTimeInSeconds);
    }
}
