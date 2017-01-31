package maintain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by szzso on 2017. 01. 25..
 */
public class GpsfakeManagement implements Runnable {

    private String host;
    private int managemnetPort;
    private static Socket socket;
    private Queue<String> queue;


    public GpsfakeManagement(String host, int managemnetPort, ConcurrentLinkedQueue<String> queue){
        this.managemnetPort = managemnetPort;
        this.host = host;
        this.queue = queue;

    }

    public List<String> getMessage(){
        List<String> input = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            while(line!= null && line.length() >0){
                input.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public void sendMessage(String message){
        PrintWriter pw;
        try {
            pw = new PrintWriter(socket.getOutputStream(),true);
            pw.println(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        boolean fistcommand = true;
        boolean connected = false;
        while (!connected) {

            try {
                socket = new Socket(host, managemnetPort);
            } catch (IOException e) {
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
            if (socket != null && socket.isConnected()) {
                connected = true;


                while (socket.isConnected()) {
                    //List<String> arrivedMessages = getMessage();

                    String queueElement;
                    int elements = 0;
                    while((queueElement = queue.poll())!= null && elements <10) {
                        if(fistcommand){
                            sendMessage("manual-begin");
                            fistcommand = false;
                        }

                        sendMessage(queueElement);
                        elements++;
                    }
                }
            }
        }
    }
}
