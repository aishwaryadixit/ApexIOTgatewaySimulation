package com.example.myapexapp;

import com.datatorrent.api.DAG;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.api.annotation.ApplicationAnnotation;
import com.datatorrent.contrib.formatter.CsvFormatter;
import com.datatorrent.contrib.mqtt.MqttClientConfig;
import com.datatorrent.contrib.parser.CsvParser;
import com.datatorrent.lib.filter.FilterOperator;
import com.datatorrent.lib.io.SmtpOutputOperator;
import org.apache.apex.malhar.lib.fs.GenericFileOutputOperator;
import org.apache.hadoop.conf.Configuration;
import org.fusesource.mqtt.client.QoS;


@ApplicationAnnotation(name="Fetch")
public class Application implements StreamingApplication
{

  @Override
  public void populateDAG(DAG dag, Configuration conf) {
    CsvParser csvParser = dag.addOperator("csvParser",CsvParser.class);
    FilterOperator filterOperator = dag.addOperator("filterOperator", new FilterOperator());

    MqttClientConfig config = new MqttClientConfig();
    config.setHost("iot.eclipse.org");
    config.setPort(1883);
    config.setCleanSession(true);

    Subscribe subscribe = dag.addOperator("Subscribe", new Subscribe());
    subscribe.setMqttClientConfig(config);
    subscribe.addSubscribeTopic("Temperature", QoS.AT_LEAST_ONCE);

    CheckAlert checkAlert =dag.addOperator("checksAlert", new CheckAlert());

    GenericFileOutputOperator.StringFileOutputOperator inrange = dag.addOperator("FileInRange", new GenericFileOutputOperator.StringFileOutputOperator());

    GenericFileOutputOperator.StringFileOutputOperator fileAlerts = dag.addOperator("fileAlerts", new GenericFileOutputOperator.StringFileOutputOperator());

    CsvFormatter csvFormatter = dag.addOperator("csvFormatter", new CsvFormatter());
//    JdbcPOJOInsertOutputOperator jdbc = dag.addOperator("jdbc",new JdbcPOJOInsertOutputOperator());
//    JdbcTransactionalStore transactionalStore = new JdbcTransactionalStore();
//    jdbc.setStore(transactionalStore);
//
//    JdbcPOJOInsertOutputOperator jdbc2 = dag.addOperator("jdbc2",new JdbcPOJOInsertOutputOperator());
//    JdbcTransactionalStore transactionalStore2 = new JdbcTransactionalStore();
//    jdbc.setStore(transactionalStore2);

    SmtpOutputOperator mailAlert2 = dag.addOperator("mailAlert2", new SmtpOutputOperator());

    SmtpOutputOperator mailAlert = dag.addOperator("mailAlert", new SmtpOutputOperator());
//    KafkaSinglePortOutputOperator kafka = dag.addOperator("Kafka", new KafkaSinglePortOutputOperator());

    dag.addStream("ToParser",subscribe.out, csvParser.in);
    dag.addStream("ToFilter",csvParser.out,filterOperator.input);
    dag.addStream("Require Action)", filterOperator.truePort,checkAlert.inputPort);
//    dag.addStream("TemperaturesTOJdbc",checkAlert.outputPort, jdbc.input);
    dag.addStream("Store",checkAlert.outputPort, inrange.input);
//    dag.addStream("Store",checkAlert.outputPort, kafka.inputPort);
    dag.addStream("MODERATE_ALERT", checkAlert.mediumAlertPort,mailAlert2.input);
    dag.addStream("HIGH_ALERT",checkAlert.alertPort,mailAlert.input);
//    dag.addStream("SafeTemperatures_(Within_Range)", filterOperator.falsePort, jdbc2.input);
    dag.addStream("SafeTemperature", filterOperator.falsePort, csvFormatter.in);
    dag.addStream("StoreALerts", csvFormatter.out, fileAlerts.input);

  }




}