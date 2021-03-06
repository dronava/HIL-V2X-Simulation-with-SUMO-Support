package communication;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class V2XCommunicationClient extends Thread {

    private String ipAddress;
    private int port;
    private String message;
    private Socket socket;

    public V2XCommunicationClient(String ipAddress, int port, String message) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.message = message;
    }

    @Override
    public void run() {
        int tryNumber = 3;
        while (tryNumber-- > 0) {
            try {
                System.out.println("Connect to: "+ipAddress +":"+  port);
                socket = new Socket(ipAddress, port);

                if (socket != null && socket.isConnected()) {
                    sendMessage();
                    socket.close();
                    break;
                }
            } catch (IOException e) {
                System.out.println("Connection problem: " + e.getMessage());
            }
        }
    }

    private void sendMessage() {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true)) {
            out.println(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
