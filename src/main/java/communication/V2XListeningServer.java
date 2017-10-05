package communication;

import communication.command.navigation.AbstractNavigationCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Created by szezso on 2017.03.18..
 */
public class V2XListeningServer implements Runnable {

    private Queue<AbstractNavigationCommand> taskQueue;
    private ServerSocket myServerSocket;
    boolean ServerOn;
    private ExecutorService executorService;
    int port;


    public V2XListeningServer(int port, ExecutorService executorService, Queue<AbstractNavigationCommand> taskQueue) {
        ServerOn = true;
        this.port = port;
        this.executorService = executorService;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        try {
            myServerSocket = new ServerSocket(port);
        } catch (IOException ioe) {
            System.out.println("Could not create server socket on port 11111. Quitting.");
            ServerOn = false;
            //System.exit(-1);
        }


        // Successfully created Server Socket. Now wait for connections.
        while (ServerOn) {
            try {
                // Accept incoming connections.
                Socket clientSocket = myServerSocket.accept();


                // accept() will block until a client connects to the server.
                // If execution reaches this point, then it means that a client
                // socket has been accepted.

                // For each client, we will start a service thread to
                // service the client requests. This is to demonstrate a
                // Multi-Threaded server. Starting a thread also lets our
                // MultiThreadedSocketServer accept multiple connections simultaneously.

                // Start a Service thread

                V2XListeningThread cliThread = new V2XListeningThread(clientSocket, taskQueue);
                executorService.execute(cliThread);

            } catch (IOException ioe) {
                System.out.println("Exception encountered on accept. Ignoring. Stack Trace :");
                ioe.printStackTrace();
            }

        }

        try {
            myServerSocket.close();
            System.out.println("Server Stopped");
        } catch (Exception ioe) {
            System.out.println("Problem stopping server socket");
            ServerOn = false;
            //System.exit(-1);
        }
    }
}
