# aThing

> 做最容易使用的Java物模型框架

```text
      _____ _     _
  __ /__   \ |__ (_)_ __   __ _
 / _` |/ /\/ '_ \| | '_ \ / _` |
| (_| / /  | | | | | | | | (_| |
 \__,_\/   |_| |_|_|_| |_|\__, |
                          |___/
做最容易使用的Java物模型框架                        
```

## 背景简介

物联网出现后，我很喜欢阿里云提出的物模型的概念，物模型将设备控制归类为`属性`、`服务`和`事件`，很好的将杂乱无章的设备控制高度抽象。

因为认同物模型的概念，我开始接触阿里云的IoT平台。但阿里云IoT平台的SDK是面向功能设计的API，没有针对物模型的概念进行抽象和封装，不便于我实际使用。所以这里我使用Java对阿里云的客户端、云端两套SDK进行了封装，希望能帮助到大家。

## 快速上手

### 一个简单的例子

```java
public class Example {

    /**
     * 物模型接口：ECHO
     */
    @ThCom(id = "echo")
    public interface EchoThingCom extends ThingCom {

        /**
         * ECHO服务
         *
         * @param string 字符串
         * @return 字符串
         */
        @ThService
        String echo(@ThParam("string") String string);

    }

    public static void main(String... args) throws Exception {

        // 连接设备
        final Thing thing = new ThingConnector()
                .connecting(
                        "ssl://***********.iot-as-mqtt.cn-shanghai.aliyuncs.com:443",
                        new ThingAccessKey("<PRODUCT>", "<THING_ID>", "***********")
                )
                .setThingBoot(new ThingBoot().booting((_thing, arguments) -> (EchoThingCom) words -> words))
                .connect(new ThingConnectOptions());

        // 销毁设备
        thing.destroy();

    }

}
```

## 当前状态

物模型的概念不止能适用于阿里云IoT平台，其实也适用于微软云、亚马逊云等支持物模型概念的IoT平台。当前我主要是适配了阿里云IoT平台。
