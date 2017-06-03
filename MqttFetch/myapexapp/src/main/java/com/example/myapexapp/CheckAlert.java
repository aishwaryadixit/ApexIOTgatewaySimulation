package com.example.myapexapp;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;

/**
 * Created by aishwarya on 28/4/17.
 */
public class CheckAlert extends BaseOperator {

    private int count = 0;
    private int high  =0;

    public final transient DefaultOutputPort<Object> outputPort = new DefaultOutputPort();
    public final transient DefaultOutputPort<Object> alertPort = new DefaultOutputPort<>();
    public final transient DefaultOutputPort<Object> mediumAlertPort = new DefaultOutputPort();

    public final transient DefaultInputPort<Object> inputPort =  new DefaultInputPort<Object>() {
        @Override
        public void process(Object o) {
            processTuple(o);
        }
    };

    public void processTuple(Object o)
    {
        String temperature = ((PojoEvent) o).getTemperature();
        System.out.println("temperature received is " + temperature );
        int t = Integer.parseInt(temperature);

        if(t<2)
            count ++;
        if (t <2 && count >= 3)
        {
            System.out.println(" count is   " + count );
            alertPort.emit(o);
            count = 0;
        }
        if(2 < t  && t < 8)
        {
            count = high = 0;
        }
        else if(t>8)
            high ++;
        if(t >8  &&  high >= 3 )
        {
            System.out.println("High range temperature " + t + "HIGH is " + high);
            high = 0;
            mediumAlertPort.emit(o);
        }

         outputPort.emit(o);

    }


}
