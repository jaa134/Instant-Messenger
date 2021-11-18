import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
  
  private JTextField userText;
  private JTextArea chatWindow;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private ServerSocket server;
  private Socket connection;
  
  public Server() {
    super("Jacob's Instant Messenger");
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
  
  //set up server
  public void startRunning() {
    try {
      //port 6789, only 100 people can wait to connect
      server = new ServerSocket(6789, 100);
      while(true) {
        try {
          waitForConnection();
          setupStreams();
          whileChatting();
        } catch(EOFException eofError) {
          showMessage("\n Server ended the connection! ");
        } finally {
          closeItems();
        }
      }
    } catch(IOException ioError) {
      ioError.printStackTrace();
    }
  }
  
  //wait for connection, then display connection information
  private void waitForConnection() throws IOException {
    showMessage(" Waiting for someone to connect... \n");
    connection = server.accept();
    showMessage(" Now connected to " + connection.getInetAddress().getHostName() + "\n");
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
    } while(!message.equals("CLIENT - END"));
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
      output.writeObject("SERVER - " + message);
      output.flush();
      showMessage("\nSERVER - " + message);
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
    Server messenger = new Server();
    //messenger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    messenger.startRunning();
  }  
}