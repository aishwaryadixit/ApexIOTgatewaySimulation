package com.example.myapexapp;

import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.contrib.mqtt.AbstractMqttInputOperator;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.fusesource.mqtt.client.Message;


/**
 * Created by aishwarya on 27/3/17
 */
public class Subscribe extends AbstractMqttInputOperator {

    public String topic = "Temperature";
    String clientId = MqttClient.generateClientId();
    public transient MemoryPersistence persistence = new MemoryPersistence();
    public transient MqttClient client;
    String msg;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;


    public final transient DefaultOutputPort out = new DefaultOutputPort();

    @Override
    public void emitTuple(Message message)
    {
        System.out.println("  original message  "  + message);
        System.out.println("class name " + message.getClass().getName());
        System.out.println("  string  " + message.toString() + "\n  Payload " + message.getPayload().toString());
        out.emit(message.getPayload());

    }




    }

