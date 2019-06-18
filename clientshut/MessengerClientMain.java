/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientshut;

import javax.swing.JFrame;
import java.io.IOException;
public class MessengerClientMain 
{
    
    public static void main(String[] args)
    {
      
        MessengerClient application;
        try{
        GetIPadress hostthing = new GetIPadress();
        String host = hostthing.reciveIP();
        System.out.println(host);
        application = new MessengerClient(host);
        
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        }catch(IOException e){}   
    }
}
    