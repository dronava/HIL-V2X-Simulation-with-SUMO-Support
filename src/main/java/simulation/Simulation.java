package simulation;


import communication.command.navigation.AbstractNavigationCommand;
import it.polito.appeal.traci.*;
import maintain.ThreadManager;
import simulation.TMC.Tmc;
import simulation.managedobject.SumoVehicle;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by szzso on 2017. 01. 21..
 */
public class Simulation implements Runnable {
    private int delay;
    private String configfile;
    private Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues = new HashMap<String, ConcurrentLinkedQueue<String>>();
    private Queue<AbstractNavigationCommand> taskQueue;

    private List<SumoVehicle> managedVehicles = new ArrayList<>();

    private boolean closeSumo;
    private boolean runGpsFake;

    public Simulation(String configfile, int delay,
                      Map<String, ConcurrentLinkedQueue<String>> gpsfakeManagmentQueues,
                      Queue<AbstractNavigationCommand> taskQueue, boolean runGpsFake) {
        this.configfile = configfile;
        this.delay = delay;
        this.gpsfakeManagmentQueues = gpsfakeManagmentQueues;
        this.taskQueue = taskQueue;
        closeSumo = true;
        this.runGpsFake = runGpsFake;

        ThreadManager.getInstance().execute(new Tmc());
    }

    @Override
    public void run() {
        try {
            SumoTraciConnection conn = new SumoTraciConnection(
                    configfile,  // config file
                    12345                                  // random seed
            );

            conn.addOption("quit-on-end", "1");
            conn.runServer(true);

            // the first two steps of this simulation have no vehicles.
            conn.nextSimStep();
            conn.nextSimStep();
            System.out.println("ok");
            gpsfakeManagmentQueues.forEach((key,value)-> System.out.println("Key:" + key));

            do {

                if (!managedVehicles.stream().map(u -> u.getVehicleID()).collect(Collectors.toList()).containsAll(gpsfakeManagmentQueues.keySet())) {
                    Map<String, Vehicle> vehicles = conn.getVehicleRepository().getAll();
                    storeVehicleSrcDst(vehicles);
                }

                Map<String, List<AbstractNavigationCommand>> task = processCommands();

                for (SumoVehicle managedVehicle : managedVehicles) {
                    managedVehicle.nextStep(conn, task.get(managedVehicle.getVehicleID()));
                }
                conn.nextSimStep();
            } while (closeSumo && !conn.isClosed());

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        System.out.println("SUMO shutdown");
    }

    private void storeVehicleSrcDst(Map<String, Vehicle> vehicles) throws IOException {

        for (Map.Entry<String, Vehicle> entry : vehicles.entrySet()) {
            if (!managedVehicles.stream().map(u -> u.getVehicleID()).collect(Collectors.toList()).contains(entry.getKey()) &&
                    gpsfakeManagmentQueues.containsKey(entry.getKey())) {
                List<Edge> actroute = entry.getValue().getCurrentRoute();

                System.out.println();
                System.out.println("Src: " + entry.getKey() + " - " + actroute.get(0));
                System.out.println("Dst: " + actroute.get(actroute.size() - 1));
                if(runGpsFake)
                    managedVehicles.add(new SumoVehicle(gpsfakeManagmentQueues.get(entry.getKey()),entry.getKey(), actroute.get(0), actroute.get(actroute.size() - 1)));
                else
                    managedVehicles.add(new SumoVehicle(entry.getKey(), actroute.get(0), actroute.get(actroute.size() - 1)));
            }
        }
    }

    private Map<String, List<AbstractNavigationCommand>> processCommands() {
        Map<String, List<AbstractNavigationCommand>> tasks = new HashMap<>();
        int actualCommandQueueSize = taskQueue.size();
        while (actualCommandQueueSize != 0) {
            AbstractNavigationCommand command = taskQueue.poll();

            actualCommandQueueSize--;
            if (!tasks.containsKey(command.getVehicleID())) {
                tasks.put(command.getVehicleID(), new ArrayList<AbstractNavigationCommand>());
            }

            List<AbstractNavigationCommand> commands = tasks.get(command.getVehicleID());
            commands.add(command);
            tasks.put(command.getVehicleID(), commands);
        }
        return tasks;
    }



    private void reRoutePROBA(SumoTraciConnection conn) throws IOException {
        Edge edge1 = conn.getEdgeRepository().getByID("296896085#3");
        if(edge1 != null) {
            System.out.println("Effort 296896085#3: " + edge1.getEffort(conn.getCurrentSimTime())); //ge1.getEffort(conn.getCurrentSimTime()));
            //edge1.queryChangeEffort().setEffort(1000);
            ChangeGlobalEffortQuery effort = edge1.queryChangeEffort();
            effort.setEffort(10000);
            effort.run();
            System.out.println("Effort 296896085#3: " + edge1.getEffort(conn.getCurrentSimTime()));
        }

        Edge edge2 = conn.getEdgeRepository().getByID("148233399#0");
        if(edge2 != null) {
            System.out.println("Effort 148233399#0: " + edge2.getEffort(conn.getCurrentSimTime()));
            ChangeGlobalEffortQuery effort= edge2.queryChangeEffort();
            effort.setEffort(10000);
            effort.run();
            System.out.println("Effort 148233399#0: " + edge2.getEffort(conn.getCurrentSimTime()));
        }

        conn.getVehicleRepository().getByID("555").reRouteByEffort();
    }
}
