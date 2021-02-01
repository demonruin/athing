package com.github.ompc.athing.aliyun.qatest.puppet;

import com.github.ompc.athing.aliyun.platform.ThingPlatformAccessKey;
import com.github.ompc.athing.aliyun.platform.ThingPlatformBuilder;
import com.github.ompc.athing.aliyun.qatest.QaThingConfigListener;
import com.github.ompc.athing.aliyun.qatest.message.QaThingMessageGroupListener;
import com.github.ompc.athing.aliyun.qatest.message.QaThingPostMessageListener;
import com.github.ompc.athing.aliyun.qatest.message.QaThingReplyMessageListener;
import com.github.ompc.athing.aliyun.qatest.puppet.component.EchoThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.LightThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.impl.QaThingComBoot;
import com.github.ompc.athing.aliyun.qatest.puppet.component.impl.ResourceThingComBoot;
import com.github.ompc.athing.aliyun.thing.ThingAccessKey;
import com.github.ompc.athing.aliyun.thing.ThingConnectOptions;
import com.github.ompc.athing.aliyun.thing.ThingConnector;
import com.github.ompc.athing.aliyun.thing.kernel.ThingBoot;
import com.github.ompc.athing.component.dmgr.api.DmgrThingCom;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.platform.ThingPlatform;
import com.github.ompc.athing.standard.platform.ThingPlatformException;
import com.github.ompc.athing.standard.platform.message.ThingMessageListener;
import com.github.ompc.athing.standard.platform.message.ThingPostMessage;
import com.github.ompc.athing.standard.platform.message.ThingReplyMessage;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.config.ThingConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.github.ompc.athing.aliyun.framework.Constants.DEFAULT_REGION_ID;

/**
 * Puppet设备支撑
 */
public class PuppetSupport {

    // 基础常量
    protected static final Properties properties = getProperties(new Properties());
    protected static final String PRODUCT_ID =
            properties.getProperty("puppet.product.id");
    protected static final String THING_ID =
            properties.getProperty("puppet.thing.id");
    protected static final String THING_SERVER_URL =
            properties.getProperty("puppet.thing.server-url");
    protected static final ThingAccessKey THING_ACCESS_KEY = new ThingAccessKey(
            PRODUCT_ID,
            THING_ID,
            properties.getProperty("puppet.thing.secret")
    );
    protected static final ThingPlatformAccessKey PLATFORM_ACS_KEY = new ThingPlatformAccessKey(
            properties.getProperty("puppet.platform.acs.access-key-id"),
            properties.getProperty("puppet.platform.acs.access-key-secret")
    );
    protected static final ThingPlatformAccessKey PLATFORM_JMS_KEY = new ThingPlatformAccessKey(
            properties.getProperty("puppet.platform.jms.access-key-id"),
            properties.getProperty("puppet.platform.jms.access-key-secret")
    );
    protected static final String PLATFORM_JMS_CONSUMER_GROUP =
            properties.getProperty("puppet.platform.jms.group");
    protected static final String PLATFORM_JMS_CONNECTION_URL =
            properties.getProperty("puppet.platform.jms.connection-url");
    protected static final QaThingReplyMessageListener qaThingReplyMessageListener = new QaThingReplyMessageListener();
    protected static final QaThingPostMessageListener qaThingPostMessageListener = new QaThingPostMessageListener();
    protected static final QaThingConfigListener qaThingConfigListener = new QaThingConfigListener();
    private static final Logger logger = LoggerFactory.getLogger(PuppetSupport.class);
    // 基础变量
    protected static Thing tPuppet;
    protected static ThingPlatform tpPuppet;

    /**
     * 初始化配置文件
     *
     * @param properties 配置信息
     * @return 配置信息
     */
    private static Properties getProperties(Properties properties) {
        // 读取配置文件
        final String propertiesFilePath = System.getProperties().getProperty("athing-qatest.properties.file");
        final File propertiesFile = new File(propertiesFilePath);
        if (!propertiesFile.exists() || !propertiesFile.canRead()) {
            throw new RuntimeException(String.format("loading properties error: file not existed: %s", propertiesFilePath));
        }
        try (final InputStream is = new FileInputStream(propertiesFile)) {
            properties.load(is);
            return properties;
        } catch (Exception cause) {
            throw new RuntimeException("loading properties error!", cause);
        }
    }

    @BeforeClass
    public static void initialization() throws Exception {
        tPuppet = initPuppetThing();
        tpPuppet = initPuppetThingPlatform();
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (null != tPuppet) {
            tPuppet.destroy();
        }
        if (null != tpPuppet) {
            tpPuppet.destroy();
        }
    }


    // ------------------------------------- THING ------------------------------------

    private static Thing initPuppetThing() throws Exception {
        return new ThingConnector()
                .connecting(THING_SERVER_URL, THING_ACCESS_KEY)
                .setThingBoot(new ThingBoot()
                        .booting(new File("./src/test/resources/lib/athing-component-dmgr-core-1.0.0-SNAPSHOT-jar-with-dependencies-for-qatest.jar"))
                        .booting(new QaThingComBoot())
                        .booting(new ResourceThingComBoot())
                        .booting((thing, arguments) -> new ThingCom() {
                        })
                )
                .setThingConfigListener(qaThingConfigListener)
                .setThingOpHook(thing -> logger.info("{} require reboot", thing))
                .connect(new ThingConnectOptions());
    }

    private static ThingPlatform initPuppetThingPlatform() throws ThingPlatformException {
        return new ThingPlatformBuilder()
                .building(DEFAULT_REGION_ID, PLATFORM_ACS_KEY)
                .product(PRODUCT_ID, DmgrThingCom.class, LightThingCom.class, EchoThingCom.class)
                .consumer(
                        DEFAULT_REGION_ID,
                        PLATFORM_JMS_KEY,
                        PLATFORM_JMS_CONNECTION_URL,
                        PLATFORM_JMS_CONSUMER_GROUP,
                        new QaThingMessageGroupListener(new ThingMessageListener[]{
                                qaThingReplyMessageListener,
                                qaThingPostMessageListener
                        })
                )
                .build();
    }

    public <T extends ThingReplyMessage> T waitingForReplyMessageByReqId(String reqId) throws InterruptedException {
        return qaThingReplyMessageListener.waitingForReplyMessageByReqId(reqId);
    }

    public <T extends ThingPostMessage> T waitingForPostMessageByReqId(String reqId) throws InterruptedException {
        return qaThingPostMessageListener.waitingForPostMessageByReqId(reqId);
    }

    public ThingConfig waitingForReceiveThingConfig() throws InterruptedException {
        return qaThingConfigListener.waitingForReceiveThingConfig();
    }

}
