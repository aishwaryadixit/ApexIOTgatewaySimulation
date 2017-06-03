
package com.example.myapexapp;

import com.datatorrent.api.DAG;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.api.annotation.ApplicationAnnotation;
import org.apache.hadoop.conf.Configuration;

@ApplicationAnnotation(name="Publisher")
public class Application implements StreamingApplication
{

  @Override
  public void populateDAG(DAG dag, Configuration conf)
  {

    SensorDataGenerator rm = dag.addOperator("sensorData",new SensorDataGenerator());
    rm.setNumTuples(40);
    Mqtt mqtt = dag.addOperator("MqttConverter", new Mqtt());

    dag.addStream("sensordata", rm.out,mqtt.input);

  }
}