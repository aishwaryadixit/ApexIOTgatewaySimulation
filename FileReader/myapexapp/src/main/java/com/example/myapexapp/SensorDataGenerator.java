/**
 * Put your copyright and license info here.
 */
package com.example.myapexapp;

import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.InputOperator;
import com.datatorrent.common.util.BaseOperator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;



public class SensorDataGenerator extends BaseOperator implements InputOperator {
  private int numTuples = 40;
  private transient int count = -1;
  private transient int i = 0;
  private transient boolean f = false ;

  public final transient DefaultOutputPort<Object> out = new DefaultOutputPort<Object>();

  @Override
  public void beginWindow(long windowId) {
    //count = 0;
  }

  @Override
  public void emitTuples() {
    //added this while statement to the original code

      PojoEvent pojo =new PojoEvent();


      if (count++ < numTuples)
      {

        if (i < 10 && f == false)
          i++;

        else if (i == 10 ) {
          f=true;
          i--;
        }
        else if (f==true)
        {
          i--;
          if (i == -1)
            f = false;
        }


        int upper = 11;
        int lower = 0;
          int r;
        if(count == 10 || count == 11 || count == 12 )
            r = 9;

        else if (count == 18 || count == 19 || count == 20)
            r = 1;
          else
         //r = (int) (Math.random() * (upper - lower)) + lower;
          r  =  i;

        String s = String.valueOf(r);
        System.out.println("count is  " + count + " s is : " + s +"   r is : " + r );
        pojo.setId("1");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String ts = new SimpleDateFormat("yyyy.MM.dd' 'HH.mm.ss").format(timestamp);
        pojo.setTime(ts);
        pojo.setTemperature(s);
        out.emit(pojo);
      }

    else teardown();

  }


  public int getNumTuples()
  {
    return numTuples;
  }

  /**
   * Sets the number of tuples to be emitted every window.
   * @param numTuples number of tuples
   */
  public void setNumTuples(int numTuples)
  {
    this.numTuples = numTuples;
  }
}
