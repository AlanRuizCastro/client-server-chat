/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package working.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Alan
 */
public class broadcast implements Runnable {
   
    @Override
    public void run(){
    // Get the address that we are going to connect to.
     
        // Open a new DatagramSocket, which will be used to send the data.
        try (DatagramSocket serverSocket = new DatagramSocket()) {
                
            InetAddress addr = InetAddress.getByName("224.0.0.3");
            
                String msg = InetAddress.getLocalHost().getHostAddress();

                while(true){
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, 8888);
                serverSocket.send(msgPacket);
     
                System.out.println("Server sent packet with msg: " + msg);
                Thread.sleep(2000);
                }
            
        } catch (IOException | InterruptedException ex) {}
  }// end run
}// end main
