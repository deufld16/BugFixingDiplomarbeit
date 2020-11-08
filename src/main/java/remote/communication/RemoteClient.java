/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.communication;

import general.beans.io_objects.ProjectRun;
import general.bl.GlobalParamter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import remote.bl.RemoteExecutionManager;
import remote.ftp.FTPUtil;
import settings.io.Loader;
import remote.ftp.FTPDownload;

/**
 *
 * @author Lukas Krobath
 */
public class RemoteClient {

    private static RemoteClient instance;
    private Socket socket;
    private final static int PORTNR = 7486;
    private final static int TIMEOUT = 1000;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread reading;

    private RemoteClient() {
    }

    public static RemoteClient getInstance() {
        if (instance == null) {
            instance = new RemoteClient();
        }
        return instance;
    }

    public void connect() throws IOException {
//        socket = new Socket(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
//        System.out.println("Connected");

        InetAddress address = InetAddress.getByName(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr());
        SocketAddress sockaddr = new InetSocketAddress(address, PORTNR);
        socket = new Socket();
        socket.connect(sockaddr, TIMEOUT);
        socket.setSoTimeout(1000);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reading = new Thread(new Reader());
        reading.start();

    }

    public void disconnect() {
        reading.interrupt();
        try {
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        reading = null;
    }

    public void sendToServer(String message) throws IOException, InterruptedException {
        writer.write(message);
        writer.newLine();
        writer.flush();
        Thread.sleep(100);
    }

    class Reader implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    //System.out.println("wait");
                    String line = reader.readLine();
                    JSONObject message = new JSONObject(line);
                    switch ((String) message.get("type")) {
                        case "possibletargets":
                            List<String> possible = new ArrayList<>();
                            for (String object : message.getJSONObject("values").keySet()) {
                                possible.add(object);
                            }
                            RemoteExecutionManager.getInstance().setPossibleTargets(possible);
                            break;
                        case "status":
                            if (message.getBoolean("running")) {
                                RemoteExecutionManager.getInstance().getPanel().getBtTVStop().setEnabled(true);
                                RemoteExecutionManager.getInstance().getPanel().getCbTestvorgang().setEnabled(false);
                                RemoteExecutionManager.getInstance().getPanel().getBtTVStart().setEnabled(false);
                            } else {
                                RemoteExecutionManager.getInstance().getPanel().getBtTVStart().setEnabled(true);
                                RemoteExecutionManager.getInstance().getPanel().getCbTestvorgang().setEnabled(true);
                                RemoteExecutionManager.getInstance().getPanel().getBtTVStop().setEnabled(false);
                            }
                            break;
                        case "log":
                            RemoteExecutionManager.getInstance().getPanel().getEpLog().setText(message.getString("message"));
                            RemoteExecutionManager.getInstance().getPanel().getEpLog().setCaretPosition(RemoteExecutionManager.getInstance().getPanel().getEpLog().getDocument().getLength());
                            break;
                        case "finished":
                            String remote = "/" + ((ProjectRun) RemoteExecutionManager.getInstance().getPanel().getCbTestvorgang().getSelectedItem()).getPath().getFileName().toString() + "/" + "erg";

                            new Thread(new FTPDownload(Paths.get(Loader.getSpecificParameter(false).get("projectPath")), remote)).start();
                            break;
                        case "runningtarget":
                            for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
                                if (workingProject.getDescription().equals(message.getString("target"))) {
                                    RemoteExecutionManager.getInstance().getPanel().getCbTestvorgang().setSelectedItem(workingProject);
                                }
                            }
                            break;
                        case "updatepiegesamt":

                            //System.out.println(message.toString(4));
                            for (String key : message.getJSONObject("value").keySet()) {
                                RemoteExecutionManager.getInstance().getPanel().getGes().updatePieSeries(key, message.getJSONObject("value").getInt(key));
                            }
                            RemoteExecutionManager.getInstance().getPanel().updateUI();
                            break;
                        case "updatepie":

//                            System.out.println(message.toString(4));
                            for (String key : message.getJSONObject("value").keySet()) {
                                RemoteExecutionManager.getInstance().getChartCash().get(message.getString("kassa")).updatePieSeries(key, message.getJSONObject("value").getInt(key));
                            }
                            RemoteExecutionManager.getInstance().getPanel().updateUI();
                            break;
                    }
                } catch (SocketException s) {
                    RemoteExecutionManager.getInstance().getPanel().getBtTVStart().setEnabled(false);
                    RemoteExecutionManager.getInstance().getPanel().getCbTestvorgang().setEnabled(false);
                    RemoteExecutionManager.getInstance().getPanel().getCbTestsystem().setEnabled(true);
                    RemoteExecutionManager.getInstance().getPanel().getBtTVStop().setEnabled(false);
                    RemoteExecutionManager.getInstance().getPanel().getBtConnect().setText("Verbinden");
                    Thread.currentThread().interrupt();
                } catch (IOException ex) {
                    //System.out.println("Read failed");
                } catch (NullPointerException n) {
                    Thread.currentThread().interrupt();
                } catch (JSONException j) {
                    //j.printStackTrace();
                }
            }
        }

    }

}
