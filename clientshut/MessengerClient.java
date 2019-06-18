/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientshut;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MessengerClient extends JFrame implements Runnable
{   
    private JTextField textField;
    private JTextArea textArea;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private ExecutorService executorService;
    private String messengerHost;
    
    
    public MessengerClient(String host){
       super("Messenger");
       
       messengerHost = host;
       textField = new JTextField();
       add(textField, BorderLayout.SOUTH);
       textField.addActionListener(
       new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent event){
                String message = event.getActionCommand();
                   if(message.length() > 160 ){
                       sendData(message.substring(0, 159)+"...");
                       displayMessage("\n message too large");
                   }else{
                   sendData(event.getActionCommand());
                   }
                   textField.setText("");
           }
           
       });
       
       textArea = new JTextArea();
       add(new JScrollPane(textArea,
       ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
       ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
       ), BorderLayout.CENTER);
       setSize(400,400);
       setVisible(true);
       textArea.setLineWrap(true);
       textArea.setEditable(false);
       startClient();
    }
    
    public void startClient(){
        
        try{
            displayMessage("Starting Client.");
            connection = new Socket(InetAddress.getByName(messengerHost), 12345);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            displayMessage("\nGot I/O streams.");
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(this);
        
    }
    
    @Override
    public void run(){
        
       try{
           processConnection();
       }
       catch(EOFException e){
           displayMessage("\nServer terminated connection.");
       }
       catch(IOException e){
           e.printStackTrace();
       }
       finally{
           closeConnection();           
       }
        
        closeConnection();
        displayMessage("\nConnection terminated.");
        
        
    }
    
    public void processConnection() throws IOException{
        
        String message = "Connection successful.";
        
        do{            
            try{
                message = (String) input.readObject();
                displayMessage("\n"+ message);
                
            }
            catch(ClassNotFoundException e){
                displayMessage("\nUknown object received.");
            }
        }while(!message.equals("TERMINATE"));
    }
    
    public void closeConnection() {
        try{
            displayMessage("\nClosing connection.");
            input.close();
            output.close();
            connection.close();
        }
        catch(IOException e){
            e.printStackTrace();
        } 
    }
    
    public void sendData(String message){        
        
        try{
            output.writeObject(message);
            output.flush();            
        }
        catch(IOException e){}
    }
    
    private void displayMessage(final String message){
        SwingUtilities.invokeLater(
            new Runnable(){

                @Override
                public void run(){
                    textArea.append(message);
                }
            }
        );
    }
     
    public boolean validateMessageLength(String message){        
        return message.matches("\\S{1,160}");        
    }    
}
