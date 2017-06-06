/**
 * Put your copyright and license info here.
 */
package com.example.myapexapp;

import com.datatorrent.api.Context;
import com.datatorrent.api.DAG;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.LocalMode;
import com.datatorrent.common.util.BaseOperator;
import com.datatorrent.contrib.mqtt.MqttClientConfig;
import com.datatorrent.lib.stream.DevNull;
import com.datatorrent.lib.util.KeyValPair;
import org.eclipse.paho.client.mqttv3.*;
import org.fusesource.mqtt.client.QoS;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Test the DAG declaration in local mode.
 */
public class ApplicationTest {

    private String msg;

  private static HashMap<String, Object> resultMap = new HashMap<String, Object>();
  private static int resultCount = 0;
  private static boolean activated = false;
    MqttClient client = null;

  public static class TestSubscribe extends Subscribe
  {

    @SuppressWarnings("UseOfSystemOutOrSystemErr")

    @Override
    public void activate(Context.OperatorContext context)

    {
        String clientId = MqttClient.generateClientId();

        try {
            client = new MqttClient("tcp://iot.eclipse.org:1883", clientId);

        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
//      super.acticom.google.inject.servlet.InternalServletModule$BackwardsCompatibleServletContevate(context);
      activated = true;

    }

  }

  public static class CollectorModule<T> extends BaseOperator implements MqttCallback
  {
    public final transient DefaultInputPort<T> inputPort = new DefaultInputPort<T>()
    {
      @Override
      public void process(T t)
      {
        @SuppressWarnings("unchecked")
        KeyValPair<String, String> kvp = (KeyValPair<String, String>)t;
        resultMap.put(kvp.getKey(), kvp.getValue());
        resultCount++;
      }

    };

      @Override
      public void connectionLost(Throwable throwable) {

      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
          String time = new Timestamp(System.currentTimeMillis()).toString();
          String s = "Time:\t" + time +
                  "  Topic:\t" + topic +
                  "  Message:\t" + new String(message.getPayload()) +
                  "  QoS:\t" + message.getQos();
          System.out.println("+++++++++++++MESSAGE IS " + s +" +++++++++++++++++++");

      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

      }

  }





  @Test

  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public void testInputOperator() throws InterruptedException, Exception
  {
      String host = "iot.eclipse.org";
      int port = 1883;
      MqttClientConfig config = new MqttClientConfig();
      config.setHost(host);
      config.setPort(port);
      config.setCleanSession(true);

      String content = "1|11:04|27";
      MqttMessage message;
      message = new MqttMessage(content.getBytes());
//      client.publish("Temperature",message);

      LocalMode lma = LocalMode.newInstance();
      DAG dag = lma.getDAG();
      final TestSubscribe input = dag.addOperator("input", TestSubscribe.class);

      System.out.println("subscribing");
      input.addSubscribeTopic("Temperature", QoS.AT_MOST_ONCE);
      DevNull d =dag.addOperator("devnull", DevNull.class);
      input.setMqttClientConfig(config);
      input.setup(null);

      dag.addStream("stream", input.out,d.data);

      LocalMode.Controller lc = lma.getController();
    lc.runAsync();
      long timeout = System.currentTimeMillis() + 3000;
      while (true) {
          if (activated) {
              break;
          }
          Assert.assertTrue("Activation timeout", timeout > System.currentTimeMillis());
          Thread.sleep(1000);
      }

      input.activate(null);
      //Thread.sleep(3000);
      //input.generateData();

//      long timeout1 = System.currentTimeMillis() + 5000;
//      try {
//          while (true) {
//              if (resultCount == 0) {
//                  Thread.sleep(10);
//                  Assert.assertTrue("timeout without getting any data", System.currentTimeMillis() < timeout1);
//              }
//              else {
//                  break;
//              }
//          }
//      }
//      catch (InterruptedException ex) {
//      }
      lc.shutdown();

//      Assert.assertEquals("Number of emitted tuples", 1, resultMap.size());
//      Assert.assertEquals("value of a is ", "10", resultMap.get("Temperature"));
////      Assert.assertEquals("value of b is ", "200", resultMap.get("b"));
//      Assert.assertEquals("value of c is ", "3000", resultMap.get("c"));
      client.subscribe("Temperature");




      System.out.println("resultCount:" + resultCount);
  }


//  public void testApplication() throws IOException, Exception {
//    try {
//      LocalMode lma = LocalMode.newInstance();
//      Configuration conf = new Configuration(false);
//      conf.addResource(this.getClass().getResourceAsStream("/META-INF/properties.xml"));
//      lma.prepareDAG(new Application(), conf);
//      LocalMode.Controller lc = lma.getController();
//      lc.run(10000); // runs for 10 seconds and quits
//    } catch (ConstraintViolationException e) {
//      Assert.fail("constraint violations: " + e.getConstraintViolations());
//    }
  //}

}
