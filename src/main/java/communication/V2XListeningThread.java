package communication;


import communication.command.navigation.AbstractNavigationCommand;
import communication.command.navigation.CommandEnum;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Queue;

/**
 * Created by szezso on 2017.03.18..
 */
public class V2XListeningThread implements Runnable {
    private Socket socket;
    private Queue<AbstractNavigationCommand> taskQueue;

    boolean m_bRunThread = true;
    boolean ServerOn = true;

    V2XListeningThread(Socket s, Queue<AbstractNavigationCommand> taskQueue) {
        this.socket = s;
        this.taskQueue = taskQueue;
    }

    public void run() {
        // Obtain the input stream and the output stream for the socket
        // A good practice is to encapsulate them with a BufferedReader
        // and a PrintWriter as shown below.

        // Print out details of this connection
        System.out.println("Accepted Client Address - " + socket.getInetAddress().getHostName());


        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream())) ) {
            // At this point, we can read for input and reply with appropriate output.

            // Run in a loop until m_bRunThread is set to false
            while (m_bRunThread) {
                // read incoming stream
                String message = in.readLine();
                System.out.println("Client Says :" + message);

                CommandEnum command = FactoryCommand.getCommandType(message);

                if (command.equals(CommandEnum.QUIT)) {
                    // Special communication.command. Quit this thread
                    m_bRunThread = false;
                    System.out.print("Stopping client thread for client : ");
                } else {
                    // Process it
                    out.println("Server Says : " + message);

                    Optional<AbstractNavigationCommand> task =
                            FactoryCommand.getFactory(socket.getRemoteSocketAddress().toString(),message, command);
                    task.ifPresent(t->taskQueue.offer(t));

                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                socket.close();
                System.out.println("...Stopped");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
