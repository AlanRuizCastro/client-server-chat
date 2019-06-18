/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package working.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;


public class MessengerServer extends JFrame{
    
    private ExecutorService executorService;
    private ServerSocket server;
    private Connection connection;
    private JTextArea textArea;
    private ArrayList<Connection> clientList;
    private broadcast signal;
    
    public MessengerServer(){
        super("Server");
        
        signal = new broadcast();
        executorService = Executors.newCachedThreadPool();
        clientList = new ArrayList();
        
        
        try{
            server = new ServerSocket(12345, 5);
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        textArea = new JTextArea();
        add(new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
                BorderLayout.CENTER);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setText("Server awaiting connections.");
        
        setSize(400, 400);
        setVisible(true);  
    }
    
    
    public void runServer(){
        executorService.execute(signal);
        int control = 1;
        while(control != 0){
           try{
                connection = new Connection(server.accept());            
                executorService.execute(connection);
                clientList.add(connection);
                
            }        
            catch(IOException e){}         
        }
    }
    
    private void displayMessage(final String message)
    {
        SwingUtilities.invokeLater(
            new Runnable()
            {            
                @Override
                public void run(){
                    textArea.append(message);
                }            
            }
        );
    }
    
    
    
    private class Connection implements Runnable
    {
        private Socket connection;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private String name;
        private Thread kill;
        private Slashkill slashkill;
        public Connection(Socket socket)
        {            
           connection = socket;
           slashkill = new Slashkill();
           kill = new Thread(slashkill);
            try{
                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
                displayMessage("\nGot I/O streams.");
                sendData("enter a user name:");
                try{
                name = (String) input.readObject();
                    sendData("username set to : "+name);
                }catch(ClassNotFoundException e){}
            }
            catch(IOException e){
                System.exit(1);
            }
            kill.start();
        }// end constructer
        @Override
        public void run(){    
            try{        
                try{                    
                    displayMessage("\nConnection: " +name+ "\nconnected.");                    
                    proccessConnection();
                }
                catch(EOFException e){
                    displayMessage("\nClient terminated the connection.");
                }
            }
            catch(IOException e){}
            finally{
                closeConnection();
            }
        }       
        
        public void proccessConnection() throws IOException{
            String message="";
            do{   
                try {
                    
                    message = (String) input.readObject();
                    kill.interrupt();
                    if(message.charAt(0) != '@'){
                     messageout(message);
                    }
                    else{
                        
                        privatemessage(message.substring(1));
                    }
                    
                } 
                catch (ClassNotFoundException ex) { 
                    displayMessage("\nError reading message.");
                }
            }while(!message.equals("TERMINATE"));
        }
        
        public void messageout(String message){
           
            for(Connection connect : clientList){
                connect.sendData(name+" : "+message);
                
            }
            displayMessage("\n"+name+" : "+message);
        }
        
        public void sendData(String message){
            try{
                output.writeObject(message);
                output.flush();                
            }
            catch(IOException e){}
        }     
        private void privatemessage(String message) {
           int i=0;
           boolean sent=false;
           int length = message.length();
            for( int ji=1 ; ji < length;ji++){
                if(message.charAt(ji) == ' '&& i==0){
                    i=ji;
                }
            }// end for loop
            String recipient = message.substring(0,i);
           
            for(int j = 0; j< clientList.size();j++){
                if(clientList.get(j).returnName() == recipient)
                   clientList.get(j).sendData("PM from:"+name+" : "+message.substring(i));
                    displayMessage(recipient+" : "+message+" : from :"+name);
                    sent=true;
            }
            if(sent != true){
                displayMessage("\n couldn't find user:"+ recipient);
                sendData("\n couldn't find user:"+ recipient);
            }
        }
        
        public String returnName(){
            return name;
        }

        
        public void closeConnection() {
            try{
                sendData("\nClosing connection with client.");
                input.close();
                output.close();
                connection.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }// end close connection
        
       private class Slashkill implements Runnable{
         
       @Override
       public void run(){
           while(true){
            try {

                Thread.sleep(settime(5));
                messageout("disconected due to inactivity");
                closeConnection();
                for(int j = 0; j< clientList.size();j++){
                    if(clientList.get(j).returnName() == name){
                    clientList.remove(j);
                    }
                }
                break;
            }

            catch (InterruptedException ex) {}
           }
            }// end run
            private int settime(int time){
                int milsec = time *60*1000;
                return milsec;
            }
       }   
    }// end runable
}//end class