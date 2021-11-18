public class Communication {
  
  public static void main(String[] args) {
    Server messenger = new Server();
    //messenger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    messenger.startRunning();
    Client client = new Client("127.0.0.1"); 
    //client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    client.startRunning();
  }
}