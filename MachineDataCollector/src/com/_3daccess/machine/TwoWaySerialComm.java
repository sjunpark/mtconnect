package com._3daccess.machine;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TwoWaySerialComm
{
    public TwoWaySerialComm()
    {
        super();
    }

    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                //serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                serialPort.setSerialPortParams(38400,SerialPort.DATABITS_7,SerialPort.STOPBITS_1,SerialPort.PARITY_EVEN);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialReader(in))).start();
                System.out.println("Thread reader started");
                (new Thread(new SerialWriter(out))).start();
                System.out.println("Thread writer started");

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    /** */
    public static class SerialReader implements Runnable
    {
        InputStream in;

        public SerialReader ( InputStream in )
        {
            this.in = in;
        }

        public void run ()
        {
            System.out.println("Executed Reader");
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
                System.out.println("finish reader");
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    /** */
    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        public void run ()
        {
            System.out.println("Executed Writter");
            try
            {

                int c = 0;


                while ( ( c = System.in.read()) > -1 )
                {
                    System.out.println("system.in==="+System.in.read());
                    this.out.write(c);

                    this.out.write("echo Q300".getBytes());
                    this.out.close();

                    out.write("echo Q300".getBytes());
                    out.close();

                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void main ( String[] args )
    {
        try
        {
            (new TwoWaySerialComm()).connect("COM5");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void sendMessage(String msg) {
            //serialPort.getInstance();
//        SerialWriter writer = new SerialWriter(serialPort.getOutputStream()); // of course you'll have to keep reference to serialPort when connection is established
//        writer.setMessage(msg);
//        (new Thread(writer)).start();
    }

}