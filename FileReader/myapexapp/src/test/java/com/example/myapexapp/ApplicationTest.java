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
import com.datatorrent.lib.util.KeyValPair;
import org.apache.hadoop.conf.Configuration;
import org.fusesource.mqtt.client.Message;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;





/**
 * Test the DAG declaration in local mode.
 */
public class ApplicationTest {
  
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Mqtt.class);
    private static HashMap<String, Object> resultMap = new HashMap<String, Object>();
    private static int resultCount = 0;
    private static boolean activated = false;

    public static class TestMqtt extends Mqtt
    {
      //@Override
      public KeyValPair<String, String> getTuple(Message msg)
      {
        return new KeyValPair<String, String>(msg.getTopic(), new String(msg.getPayload()));
      }

      @SuppressWarnings("UseOfSystemOutOrSystemErr")
      public void generateData() throws Exception
      {
        int qos = 0;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 10);
        map.put("b", 200);
        map.put("c", 3000);
        System.out.println("Data generator map:" + map.toString());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
          sampleClient.publish(entry.getKey(), entry.getValue().toString().getBytes(), qos, false);
        }
      }

      //@Override
      public void activate(Context.OperatorContext context)
      {
        super.activate(context);
        activated = true;
      }

    }

    public static class CollectorModule<T> extends BaseOperator
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
    }

    @Test
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void testInputOperator() throws IOException{
      String host = "localhost";
      int port = 1883;
      MqttClientConfig config = new MqttClientConfig();
      config.setHost(host);
      config.setPort(port);
      config.setCleanSession(true);
    }


  public void testApplication() throws Exception {
    try {
      LocalMode lma = LocalMode.newInstance();
      Configuration conf = new Configuration(false);
      conf.addResource(this.getClass().getResourceAsStream("/META-INF/properties.xml"));
      lma.prepareDAG(new Application(), conf);
      LocalMode.Controller lc = lma.getController();
      lc.run(10000); // runs for 10 seconds and quits


      DAG dag = lma.getDAG();
      final TestMqtt input = dag.addOperator("input", TestMqtt.class);
      CollectorModule<KeyValPair<String, String>> collector = dag.addOperator("collector", new CollectorModule<KeyValPair<String, String>>());


//      input.addSubscribeTopic("a", QoS.AT_MOST_ONCE);
//      input.addSubscribeTopic("b", QoS.AT_MOST_ONCE);
//      input.addSubscribeTopic("c", QoS.AT_MOST_ONCE);
//      input.setMqttClientConfig(config);
//      input.setup(null);
      dag.addStream("stream", input.out, collector.inputPort);

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
      input.generateData();

      long timeout1 = System.currentTimeMillis() + 5000;
      try {
        while (true) {
          if (resultCount == 0) {
            Thread.sleep(10);
            Assert.assertTrue("timeout without getting any data", System.currentTimeMillis() < timeout1);
          } else {
            break;
          }
        }
      } catch (InterruptedException ex) {
      }
      lc.shutdown();

      Assert.assertEquals("Number of emitted tuples", 3, resultMap.size());
      Assert.assertEquals("value of a is ", "10", resultMap.get("a"));
      Assert.assertEquals("value of b is ", "200", resultMap.get("b"));
      Assert.assertEquals("value of c is ", "3000", resultMap.get("c"));
      System.out.println("resultCount:" + resultCount);
    }
   catch (ConstraintViolationException e) {
    Assert.fail("constraint violations: " + e.getConstraintViolations());
  }


  }



  }


