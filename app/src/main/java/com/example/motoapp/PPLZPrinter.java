package com.example.motoapp;

import java.util.ArrayList;
import java.util.Map;

import com.argox.sdk.barcodeprinter.BarcodePrinter;
import com.argox.sdk.barcodeprinter.connection.PrinterConnection;
import com.argox.sdk.barcodeprinter.connection.bluetooth.BluetoothConnection;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZ;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZBarCodeType;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZOrient;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZStorage;
import com.argox.sdk.barcodeprinter.util.Encoding;

import android.app.Activity;
import android.graphics.Typeface;

public class PPLZPrinter
{
    protected BarcodePrinter<PrinterConnection, PPLZ> printer;
    private Activity activity = null;
    
    public void initPrinter(Activity activity)
    {
        this.activity = activity;
        
        printer = new BarcodePrinter<PrinterConnection, PPLZ>();

        printer.setEmulation(new PPLZ());

        printer.setConnection(new BluetoothConnection(activity));
        try
        {
            printer.getConnection().open();
        } 
        catch (Exception ex) 
        {
        }

    }
    
    public void setReset()
    {
        try
        {
            printer.getEmulation().getSetUtil().setReset();
        } 
        catch (Exception ex) 
        {
        }
        
    }
    
    public void printF(Activity activity)
    {
        try {
            this.printSubFunction(this.keyValues);
        }
        catch(Exception ex)
        {
            this.initPrinter(activity);
            
            try
            {
                this.printSubFunction(this.keyValues);
            }
            catch (Exception e)
            {
            }
        }
    }
    
    private void printSubFunction(ArrayList< MyEntry<String, String> > keyValues) throws Exception
    {
        try {
            String printText;
            String imageName;
            int y = 10;
            int idx = 1;
            final int line_height = 30;
            final int leading_space = 25;

            for(MyEntry<String, String> keyValue : keyValues)
            {
                String key = keyValue.key;
                String value = keyValue.value;
                
                if(key.length()>0 && key.startsWith("@") )
                {
                    Encoding encode = Encoding.UTF_8;
                    byte[] buf = encode.getBytes(value);
                    printer.getEmulation().getBarcodeUtil().printOneDBarcode(leading_space+20, y, PPLZOrient.Clockwise_0_Degrees, 2, 4, 2*line_height,
                            PPLZBarCodeType.Code_128, 0, buf, 'Y', 'N', 'N', 'N', 'N');// Code 128
                    y += 2.5*line_height;
                }
                else if(key.length()>0)
                {
                    y+=10;
                    imageName = "txt" + String.format("%05d", idx++);
                    printText = key + "："+ value;

                    printer.getEmulation().getTextUtil().storeTextGraphic(Typeface.DEFAULT, 30, false, false, false, false, PPLZStorage.Dram, imageName, printText);
                    printer.getEmulation().getGraphicsUtil().printStoreGraphic(leading_space, y, PPLZStorage.Dram, imageName, 1, 1);
                    y += line_height;
                }
                else
                {
                    y+=10;
                    imageName = "txt" + String.format("%05d", idx++);
                    printText = value;

                    printer.getEmulation().getTextUtil().storeTextGraphic(Typeface.DEFAULT, 30, false, false, false, false, PPLZStorage.Dram, imageName, printText);
                    printer.getEmulation().getGraphicsUtil().printStoreGraphic(leading_space, y, PPLZStorage.Dram, imageName, 1, 1);
                    y += line_height;
                }
            }
            
            printer.getEmulation().getSetUtil().setPrintOut(1, 0, 1, false);
            printer.getEmulation().getIOUtil().printOut();
        }
        catch (Exception ex) 
        {
            throw(ex);
        }
    }

    public void clearData()
    {
        this.keyValues = new ArrayList< MyEntry<String, String> >();

    }
    
    public void addDataPair(String key, String value)
    {
        keyValues.add(new MyEntry<String, String>(key, value));

    }
    ArrayList< MyEntry<String, String> > keyValues = new ArrayList< MyEntry<String, String> >();

    public void print(Activity activity) 
    {
    	this.addDataPair("", "");
        this.addDataPair("@", "123456789");
        this.addDataPair("託運單號", "123456789");
        this.addDataPair("寄件人", "Baga醬");
        this.addDataPair("寄件人電話", "02-8825252");
        this.addDataPair("寄件人地址", "新北市新莊區中平街216號");
        this.addDataPair("收件人", "果子李");
        this.addDataPair("收件人地址", "台北市重慶南路一段1號");
        this.addDataPair("貨品大小", "10x20x30");
        this.addDataPair("貨品數", "3");

        this.printF(activity);
        
        return;
    }

    public class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    public enum ConnectType {

        NETWORK, BLUETOOTH, USB, FILE;
    }
}
