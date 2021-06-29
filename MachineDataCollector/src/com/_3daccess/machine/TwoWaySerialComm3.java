package com._3daccess.machine;

import de.voidplus.twowayserialcomm.SerialReader;
import de.voidplus.twowayserialcomm.SerialWriter;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class TwoWaySerialComm3 {

    private OutputStream output;
    private InputStream input;

    private SerialReader serial;
    private CommPort commPort;
    private SerialPort serialPort;

    public TwoWaySerialComm3() {
        super();
    }

    public TwoWaySerialComm3(String portName, Integer baudrate) {
        this.connect(portName, baudrate);
    }

    /**
     * Connect to the port with specific baudrate.
     *
     * @param portName
     * @param baudrate
     */
    public void connect(String portName, Integer baudrate) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                System.out.println("Error: Port is currently in use");
            } else {
                this.commPort = portIdentifier.open(this.getClass().getName(), 2000);
                if (commPort instanceof SerialPort) {

                    this.serialPort = (SerialPort) commPort;
                    this.serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    // IN
                    this.input = serialPort.getInputStream();
                    this.serial = new SerialReader(this.input);

                    serialPort.addEventListener(this.serial);
                    serialPort.notifyOnDataAvailable(true);

                    // OUT
                    this.output = serialPort.getOutputStream();
                    // (new Thread(new SerialReader(in))).start();
                    (new Thread(new SerialWriter(this.output))).start();

                    Thread.sleep(5000);
                    this.output.write("Q300".getBytes());
                    System.out.println("===========================================================");

                    System.out.println(this.output.toString());
                    Thread.sleep(1500);

                    //this.input.read("Q300".getBytes());

                    byte[] readBuffer = new byte[20];
                    //this.input = serialPort.getInputStream();

                    //this.output = serialPort.getOutputStream();
                    try {
                        while (input.available() > -1) {
                            int numBytes = input.read(readBuffer);
                            System.out.print("="+new String(readBuffer));
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("NOT WORKING BECAUSE OF LOOP");


                } else {
                    System.out.println("Error: Only serial ports are handled by this example.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect the serial communication.
     */
    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.commPort.close();
            System.out.println("Disconnected from Port " + commPort.getName());
            this.commPort = null;
        } else {
            System.out.println("There is nothing to disconnect");
        }
    }

    /**
     * Check if the device is connected.
     *
     * @return
     */
    public boolean isConnected() {
        return (this.commPort != null);
    }

    public static String[] listAvailablePorts() {
        Vector localVector = new Vector();
        try {
            Enumeration localEnumeration = CommPortIdentifier
                    .getPortIdentifiers();
            while (localEnumeration.hasMoreElements()) {
                CommPortIdentifier localCommPortIdentifier = (CommPortIdentifier) localEnumeration
                        .nextElement();
                if (localCommPortIdentifier.getPortType() == 1) {
                    String str = localCommPortIdentifier.getName();
                    localVector.addElement(str);
                }
            }
        } catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
            // errorMessage("ports", localUnsatisfiedLinkError);
        } catch (Exception localException) {
            // errorMessage("ports", localException);
        }
        String[] arrayOfString = new String[localVector.size()];
        localVector.copyInto(arrayOfString);
        return arrayOfString;
    }

    /**
     * Send JSON message to device.
     *
     * @param str
     */
    public void write(String str) {
        if (this.isConnected()) {
            try {
                System.out.println("write str="+str);
                this.output.write(str.getBytes());
                //여기에 어떻게 RETURN값을 가져오는지 확인 필요.

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all received JSON objects.
     *
     * @return
     */
    public HashMap<Long, String> read() {
        if (this.isConnected()) {
            System.out.println("getMessage="+this.serial.getMessages());
            return this.serial.getMessages();
        }
        return new HashMap<Long, String>();
    }

    public static void main ( String[] args )
    {
        TwoWaySerialComm3 comm3 = new TwoWaySerialComm3();
        System.out.println("TEST3333");
        try
        {
           comm3.connect("COM5",38400);
           Thread.sleep(3000);
           comm3.write("Q300");

            Thread.sleep(3000);
            comm3.write("Q300");

            Thread.sleep(3000);
            comm3.write("Q300");

            Thread.sleep(3000);
            comm3.write("Q300");



//           System.out.println("isconnected="+comm3.isConnected());
//           Thread.sleep(3000);
//           HashMap<Long, String> hm;
//           hm = comm3.read();
//           System.out.println(hm);
           comm3.disconnect();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
