/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package working.server;

import javax.swing.JFrame;

public class MessengerServerMain 
{
    
    
   public static void main(String[] args) 
   {
       MessengerServer server;
       server = new MessengerServer();
       server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       server.runServer();
       
   }
    
}
