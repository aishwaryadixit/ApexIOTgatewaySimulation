package com.example.myapexapp;
import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.Operator;
import com.datatorrent.common.util.BaseOperator;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by aishwarya on 27/3/17
 */
public class Mqtt extends BaseOperator implements MqttCallback, Operator.ActivationListener<Context.OperatorContext> {

    public String topic = "Temperature";
    String content = "";
    int qos = 0;
    String broker = "tcp://iot.eclipse.org:1883";    //"tcp://localhost:1883"; //    //"   //"tcp://192.168.2.37:1883";       //"tcp://192.168.1.88:1883";
    String clientId = MqttClient.generateClientId();
    public transient MemoryPersistence persistence = new MemoryPersistence();
    public transient MqttClient sampleClient;


    @Override
    public void activate(Context.OperatorContext context) {
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);        //so that the client would start in the same session case of connection loss
            sampleClient.connect(connOpts);
            System.out.println("Connecting");
            connOpts.setKeepAliveInterval(60 * 30);
            connOpts.setConnectionTimeout(60 * 30);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("except " + me);
            me.printStackTrace();
        }
    }

    private String id;
    private String time;
    private int temp;
    private String t;
    private String msg;

    public final transient DefaultOutputPort out = new DefaultOutputPort();
    public final transient DefaultInputPort input = new DefaultInputPort() {
        @Override
        public void process(Object o) {
            processTuple(o);
        }

        private void processTuple(Object o) {

            String SensorId = ((PojoEvent) o).getId();
            time= ((PojoEvent) o).getTime();
            String Temp = ((PojoEvent) o).getTemperature();

            content =  "\""+ SensorId+"\"" + "|" +"\""+ time+ "\""+ "|"+ "\""+ Temp + "\"";
            if(SensorId == null)
                disc();

            MqttMessage message;
            message = new MqttMessage(content.getBytes());
            message.setPayload(content.getBytes());
            message.setQos(qos);

            MqttTopic mytopic = sampleClient.getTopic(topic);

            MqttToken token;
            try {
                // publish message to broker
                token = mytopic.publish(message);
                System.out.println("Publishing" + content);

                // Wait until the message has been delivered to the broker
                token.waitForCompletion();
                Thread.sleep(100);
                System.out.println("Done ");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void setup(Context.PortContext context) {


        }
    };

   

    @Override
    public void deactivate() {

    }


    //@Override
    public void disc() {

        try {
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Disconnected");
        System.exit(0);

    }


    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Lost Connection");
        System.exit(1);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    System.out.println("Published Successfully");
    }
}