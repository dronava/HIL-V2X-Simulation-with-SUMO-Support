package gpsfake;


import simulation.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by szezso on 2017.03.18..
 */
public class V2XConfigurationThread implements Runnable {
    private Socket socket;
    private Queue<Task> taskQueue;

    boolean m_bRunThread = true;
    boolean ServerOn = true;

    public V2XConfigurationThread()
    {
        super();
    }

    V2XConfigurationThread(Socket s, Queue<Task> taskQueue)
    {
        this.socket = s;
        this.taskQueue = taskQueue;
    }

    public void run()
    {
        // Obtain the input stream and the output stream for the socket
        // A good practice is to encapsulate them with a BufferedReader
        // and a PrintWriter as shown below.
        BufferedReader in = null;
        PrintWriter out = null;

        // Print out details of this connection
        System.out.println("Accepted Client Address - " + socket.getInetAddress().getHostName());


        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            // At this point, we can read for input and reply with appropriate output.

            // Run in a loop until m_bRunThread is set to false
            while(m_bRunThread)
            {
                // read incoming stream
                String clientCommand = in.readLine();
                System.out.println("Client Says :" + clientCommand);
                Task task;

                if(!ServerOn)
                {
                    // Special command. Quit this thread
                    System.out.print("Server has already stopped");
                    out.println("Server has already stopped");
                    out.flush();
                    m_bRunThread = false;

                }

                if(clientCommand.equalsIgnoreCase("quit")) {
                    // Special command. Quit this thread
                    m_bRunThread = false;
                    System.out.print("Stopping client thread for client : ");
                } else if(clientCommand.equalsIgnoreCase("end")) {
                    // Special command. Quit this thread and Stop the Server
                    m_bRunThread = false;
                    System.out.print("Stopping client thread for client : ");
                    ServerOn = false;
                } else {
                    // Process it
                    out.println("Server Says : " + clientCommand);
                    String[] splitCommand = clientCommand.split(";");

                    task = new Task();

                    if(splitCommand.length > 1){
                        task.setId(splitCommand[0]);
                        task.setCommand(splitCommand[1]);


                        for (int i = 2; i < splitCommand.length; i++) {
                            task.addParameter(splitCommand[i]);
                        }
                        taskQueue.offer(task);
                    }
                    out.flush();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Clean up
            try
            {
                in.close();
                out.close();
                socket.close();
                System.out.println("...Stopped");
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }
}
