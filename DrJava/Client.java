import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

  private JTextField userText;
  private JTextArea chatWindow;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private String message = "";
  private String serverIP;
  private Socket connection;
 
 
 //GUI in the constructor
 public Client(String host) {
   super("Jacob's Instant Mesenger Client");
   serverIP = host;
   userText = new JTextField();
   userText.setEditable(false);
   userText.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent event) {
       sendMessage(event.getActionCommand());
       userText.setText("");
     }
   });
   add(userText, BorderLayout.NORTH);
   chatWindow = new JTextArea();
   add(new JScrollPane(chatWindow), BorderLayout.CENTER);
   setSize(400, 400);
   setVisible(true);
 }
 
 //connect to the server
 public void startRunning() {
   try {
     connectToServer();
     setupStreams();
     whileChatting();
   } catch(EOFException eofError) {
     showMessage("\n Client terminated the connection.");
   } catch(IOException ioError) {
     ioError.printStackTrace();
   } finally {
     closeItems();
   }
 }
 
 //connect to a server
 private void connectToServer() throws IOException {
   showMessage("Attempting connection... \n");
   connection = new Socket(InetAddress.getByName(serverIP), 6789);
   showMessage("Connected to: " + connection.getInetAddress().getHostName());
 }
 
 //get stream to send and recieve data
 private void setupStreams() throws IOException {
   output = new ObjectOutputStream(connection.getOutputStream());
   output.flush();
   input = new ObjectInputStream(connection.getInputStream());
   showMessage(" Streams are now setup! \n");
 }
 
 //during the chat conversation
 private void whileChatting() throws IOException {
   String message = " You are now connected! \n";
   sendMessage(message);
   ableToType(true);
   do {
     try {
       //place where only strings are allowed
       message = (String) input.readObject();
       showMessage("\n" + message);
     } catch(ClassNotFoundException cnfError) {
       showMessage("\n USER DID NOT SEND A VALID MESSAGE! \n");
     }
   } while(!message.equals("SERVER - END"));
 }
 
 //closes connections and streams after done chatting
 private void closeItems() {
   showMessage("\n Closing connections... \n");
   ableToType(false);
   try {
     output.close();
     input.close();
     connection.close();
   } catch(IOException ioError) {
     ioError.printStackTrace();
   }
 }
 
 //send a message to the client
 private void sendMessage(String message) {
   try {
     output.writeObject("CLIENT - " + message);
     output.flush();
     showMessage("\nCLIENT - " + message);
   } catch(IOException ioError) {
     chatWindow.append("\n ERROR: USER COULD NOT SEND MESSAGE");
   }
 }
 
 //updates chatWindow
 private void showMessage(final String message) {
   SwingUtilities.invokeLater(new Runnable(){
     public void run() {
       chatWindow.append(message);
     }
   });
 }
 
 //determines if the user is able to type
 private void ableToType(final boolean ability) {
   SwingUtilities.invokeLater(new Runnable(){
     public void run() {
       userText.setEditable(ability);
     }
   });
 }
 
 public static void main(String[] args) {
   Client client = new Client("127.0.0.1"); 
   //client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   client.startRunning();
 }
}
   