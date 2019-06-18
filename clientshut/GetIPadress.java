/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientshut;

/**
 *
 * @author Admin
 */
import java.io.IOException;
import java.net.DatagramPacket; 
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class GetIPadress {
    private String group;
    private InetAddress adress;
    private byte[] buf;
    private MulticastSocket clientSocket;
    private DatagramPacket packet;
    
    public GetIPadress()throws UnknownHostException{
        
    adress = InetAddress.getByName("224.0.0.3");
    buf = new byte[256];
    packet = new DatagramPacket(buf, buf.length);
    try{
    clientSocket = new MulticastSocket(8888);
    clientSocket.joinGroup(adress);
    }catch(IOException e){}
    
    }
    
    public String reciveIP()throws IOException{
    
        clientSocket.receive(packet);

    String msg = new String(buf, 0, buf.length);
    System.out.println(msg);
    return msg;
    }
}
