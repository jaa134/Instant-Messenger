import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

  private JTextField userText;
  private final JTextArea chatWindow;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  final private String serverIP;
  private Socket connection;
 
 
 //GUI in the constructor
 public Client(String host) {
   super("Jacob's Instant Mesenger Client");
   serverIP = host;
   userText = new JTextField();
   userText.setEditable(false);
   userText.addActionListener((ActionEvent event) -> {
       sendMessage(event.getActionCommand());
       userText.setText("");
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
   } finally {
     closeItems();
   }
 }
 
 //connect to a server
 private void connectToServer() throws IOException {
   showMessage("Attempting connection... \n");
   connection = new Socket(InetAddress.getByName(serverIP), 35697);
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
   //receive();
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
   SwingUtilities.invokeLater(() -> {
       chatWindow.append(message);
   });
 }
 
 //determines if the user is able to type
 private void ableToType(final boolean ability) {
   SwingUtilities.invokeLater(() -> {
       userText.setEditable(ability);
   });
 }
 
 /*private void receive() throws IOException {
	byte[] sizeAr = new byte[4];
    input.read(sizeAr);
    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
    byte[] imageAr = new byte[size];
    input.read(imageAr);
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
    ImageIO.write(image, "png", new File("C:/Users/Jacob/Desktop/s1.png"));
 }*/
 
 public static void main(String[] args) {
   Client client = new Client("127.0.0.1"); 
   client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   client.startRunning();
 }
}