package kg.apc.jmeter.perfmon;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import kg.apc.emulators.SocketEmulator;
import kg.apc.emulators.SocketEmulatorInputStream;
import kg.apc.emulators.TestJMeterUtils;
import kg.apc.emulators.TestSocketFactory;
import kg.apc.jmeter.vizualizers.JSettingsPanel;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Hoblingre
 */
public class MetricsProviderTest {

    private static class MetricsProviderEmul extends MetricsProvider {

        SocketEmulator sock;

        public MetricsProviderEmul(int aType, AbstractPerformanceMonitoringGuiImpl abstractPerformanceMonitoringGuiImpl, AgentConnector[] connectors) {
            super(aType, abstractPerformanceMonitoringGuiImpl, connectors);
            sock = new SocketEmulator();
        }

        @Override
        protected Socket createSocket(String host, int port) throws IOException {
            return sock;
        }
    }
    private AgentConnector[] connectors = {
        new AgentConnector("localhost", 4444), new AgentConnector("server1", 4455)
    };

    public MetricsProviderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestJMeterUtils.createJmeterEnv();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public void runWithType(int aType, String data) throws InterruptedException, IOException {
        System.out.println("Run With type " + aType + " and data " + data);
        MetricsProvider instance = new MetricsProviderEmul(aType, new AbstractPerformanceMonitoringGuiImpl(), connectors);


        TestSocketFactory sf = new TestSocketFactory();
        SocketEmulatorInputStream is = (SocketEmulatorInputStream) sf.createSocket().getInputStream();
        is.setBytesToRead(data.getBytes());

        instance.testStarted();
        Thread.sleep(500);
        instance.testEnded();
    }

    @Test
    public void testRun_CPU() throws InterruptedException, IOException {
        runWithType(ServerPerfMonitoringGUI.PERFMON_CPU, "test\n1\n2\n25\n3:4\n5:6\n7:8\n");
    }

    @Test
    public void testRun_MEM() throws InterruptedException, IOException {
        runWithType(ServerPerfMonitoringGUI.PERFMON_MEM, "test\n1\n2\n25\n3:4\n5:6\n7:8\n");
    }

    @Test
    public void testRun_DISKS() throws InterruptedException, IOException {
        runWithType(ServerPerfMonitoringGUI.PERFMON_DISKS_IO, "test\n3:4\n5:6\n7:8\n");
    }

    @Test
    public void testRun_SWAP() throws InterruptedException, IOException {
        runWithType(ServerPerfMonitoringGUI.PERFMON_SWAP, "test\n3:4\n5:6\n7:8\n");
    }

    @Test
    public void testRun_NET() throws InterruptedException, IOException {
        runWithType(ServerPerfMonitoringGUI.PERFMON_NETWORKS_IO, "test\n3:4\n5:6\n7:8\n");
    }

    @Test
    public void testRun_BadPerfmon() throws InterruptedException, IOException {
        runWithType(-1, "");
    }

    @Test
    public void testRun_BrokenData() throws InterruptedException, IOException {
        MetricsProvider instance = new MetricsProviderEmul(1, new AbstractPerformanceMonitoringGuiImpl(), connectors);
        SocketEmulator sock = ((MetricsProviderEmul) instance).sock;
        SocketEmulatorInputStream is = (SocketEmulatorInputStream) sock.getInputStream();
        String data = "test\n1\n2\n25\n3:4\n5:6\n7:8\n";
        is.setBytesToRead(data.getBytes());

        instance.testStarted();
        Thread.sleep(2500);
        instance.testEnded();
    }

    /**
     * Test of run method, of class MetricsProvider.
     */
    @Test
    public void testRun() {
        //covered above
    }

    /**
     * Test of testStarted method, of class MetricsProvider.
     */
    @Test
    public void testTestStarted() {
        System.out.println("testStarted");
        MetricsProvider instance = new MetricsProviderEmul(1, new AbstractPerformanceMonitoringGuiImpl(), connectors);
        instance.testStarted();
        instance.testEnded();
    }

    /**
     * Test of testEnded method, of class MetricsProvider.
     */
    @Test
    public void testTestEnded() {
        System.out.println("testEnded");
        MetricsProvider instance = new MetricsProviderEmul(1, new AbstractPerformanceMonitoringGuiImpl(), connectors);
        instance.testEnded();
    }

    public class AbstractPerformanceMonitoringGuiImpl
            extends AbstractPerformanceMonitoringGui {

        @Override
        public String getLabelResource() {
            return "test";
        }

        public void add(SampleResult sample) {
            return;
        }

        @Override
        protected JSettingsPanel getSettingsPanel() {
            return new JSettingsPanel(this, JSettingsPanel.GRADIENT_OPTION);
        }

        @Override
        public String getStaticLabel() {
            return "perfmontest";
        }

        @Override
        public void addPerfRecord(String serverName, double value) {
        }

        @Override
        public void addPerfRecord(String serverName, double value, long time) {
        }

        @Override
        public void setErrorMessage(String msg) {
        }

        @Override
        public void clearErrorMessage() {
        }

        @Override
        public void setChartType(int monitorType) {
        }

        @Override
        public void setLoadMenuEnabled(boolean enabled) {
        }

        @Override
        public String getWikiPage() {
            return "";
        }
    }

    /**
     * Test of loadFile method, of class MetricsProvider.
     */
    @Test
    public void testLoadFile() throws Exception {
        System.out.println("loadFile");
        File file = new File(MetricsProviderTest.class.getResource("test.jppm").toURI());
        MetricsProvider instance = new MetricsProvider(AbstractPerformanceMonitoringGui.PERFMON_CPU, new AbstractPerformanceMonitoringGuiImpl(), null);
        instance.loadFile(file);
    }

    /**
     * Test of getConnectTimeout method, of class MetricsProvider.
     */
    @Test
    public void testGetConnectTimeout() {
        System.out.println("getConnectTimeout");
        MetricsProvider instance = new MetricsProvider(AbstractPerformanceMonitoringGui.PERFMON_CPU, connectors);
        int expResult = 10000;
        int result = instance.getConnectTimeout();
        assertEquals(expResult, result);
    }

    /**
     * Test of setConnectTimeout method, of class MetricsProvider.
     */
    @Test
    public void testSetConnectTimeout() {
        System.out.println("setConnectTimeout");
        int connectTimeout = 0;
        MetricsProvider instance = new MetricsProvider(AbstractPerformanceMonitoringGui.PERFMON_CPU, connectors);
        instance.setConnectTimeout(connectTimeout);
    }

    /**
     * Test of createSocket method, of class MetricsProvider.
     */
    @Test
    public void testCreateSocket() throws Exception {
        System.out.println("createSocket");
        String host = "";
        int port = 0;
        MetricsProvider instance = new MetricsProvider(AbstractPerformanceMonitoringGui.PERFMON_CPU, connectors);
        try {
        Socket result = instance.createSocket(host, port);
        fail("must fail");
        }catch(ConnectException ex)
        {}
    }
}