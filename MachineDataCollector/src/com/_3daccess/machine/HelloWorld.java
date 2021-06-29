

package com._3daccess.machine;

import com.fazecast.jSerialComm.SerialPort;

public class HelloWorld {
    public static void main(String[] args) {
        //SerialPort.getCommPorts();
        SerialPort.getCommPort("COM4");
        System.out.println("HELLO");
    }
}
