package communication;


import communication.Factory.AbstractFactoryCommand;
import communication.Factory.FactoryNavigationScenario1;
import communication.Factory.FactoryTmcCommand;
import communication.command.AbstractCommand;
import communication.command.CommandEnum;
import simulation.RolesCatalog;
import simulation.RolesEnum;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Queue;

/**
 * Created by szezso on 2017.03.18..
 */
public  class  V2XListeningThread<C extends AbstractCommand> implements Runnable {
    private Socket socket;
    private Queue<C> taskQueue;

    boolean m_bRunThread = true;
    boolean ServerOn = true;
    private AbstractFactoryCommand factoryCommand;
    private RolesEnum role;

    V2XListeningThread(Socket s, Queue<C> taskQueue, RolesEnum role) {
        this.socket = s;
        this.taskQueue = taskQueue;
        switch (role){
            case TMC:
                factoryCommand = new FactoryTmcCommand();
                break;
            case NAVIGATION:
                factoryCommand = new FactoryNavigationScenario1();
                break;
        }
        this.role = role;
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
            while (m_bRunThread  && socket.isConnected()) {
                // read incoming stream
                String message = in.readLine();

                synchronized(this) {
                    this.wait(200);
                }
                CommandEnum command;
                if(message != null && message.length() >0) {
                    System.out.println("Client Says :" + message);
                    command = AbstractFactoryCommand.getCommandType(message);
                    System.out.println("FACTORY " + command + " Role: " + role);
                    if (command.equals(CommandEnum.QUIT)) {
                        // Special communication.command. Quit this thread
                        m_bRunThread = false;
                        System.out.print("Stopping client thread for client : ");
                    } else {
                        // Process it
                        //out.println("Server Says : " + message);

                        Optional<AbstractCommand> task =
                                factoryCommand.createCommand(message, command);
                        task.ifPresent(t->{
                            System.out.println("OBU: "+socket.getInetAddress().getHostAddress());
                            RolesCatalog.newOBU(t.getVehicleID(), socket.getInetAddress().getHostAddress());
                            taskQueue.offer((C) t);
                        });


                        //out.flush();
                    }
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
