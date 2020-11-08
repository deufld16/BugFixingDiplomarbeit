/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.communication;

import remote.communication.*;
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
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import remote.bl.RemoteExecutionManager;
import remote.ftp.FTPUtil;
import settings.io.Loader;
import remote.ftp.FTPDownload;
import simulator.beans.Kasse;
import simulator.bl.ExecutionManager;
import simulator.commands.ACommand;
import simulator.ftp.FTPUtilCommand;

/**
 *
 * @author Lukas Krobath
 */
public class CommandClient implements Callable<Boolean> {

    private Socket socket;
    private final static int PORTNR = 7488;
    private final static int TIMEOUT = 1000;
    private BufferedReader reader;
    private BufferedWriter writer;
    private JSONObject comm = new JSONObject();
    private ACommand command;
    private String pathStrDir="";
    
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    public CommandClient(ACommand command) throws UnknownHostException, IOException, InterruptedException {
        this.command = command;

        InetAddress address = InetAddress.getByName(ExecutionManager.getInstance().getActiveSystem().getBackoffice().getStrIpAdr());
        SocketAddress sockaddr = new InetSocketAddress(address, PORTNR);
        socket = new Socket();
        socket.connect(sockaddr, TIMEOUT);
        socket.setSoTimeout(1000);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        JSONObject test = new JSONObject();
        test.put("type", "location");
        sendToServer(test.toString());
    }

    private void sendToServer(String message) throws IOException, InterruptedException {
        writer.write(message);
        writer.newLine();
        writer.flush();
        Thread.sleep(100);
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Hola");
        while (!Thread.interrupted()) {
            try {
                //System.out.println("wait");
                String line = reader.readLine();
                JSONObject message = new JSONObject(line);
                switch ((String) message.get("type")) {
                    case "dir":
                        for (File target : new File(command.getObjRuntimeEnv().getObjActTG().getObjActTC().getStrTestCasePath()).listFiles()) {
                            if (!target.getName().equals("run.xml")) {
                                FTPUtilCommand.upload(target.getAbsolutePath(), message.getString("path") + "/" + target.getName());
                            }
                        }
                        comm.put("type", "command");
                        comm.put("map", new JSONObject(command.getMapParams()));

                        JSONObject runtime = new JSONObject();
                        runtime.put("ireg", command.getObjRuntimeEnv().getiRegCount());
                        runtime.put("ip", command.getObjRuntimeEnv().getStrServerAddr());

                        JSONObject kassa = new JSONObject();
                        kassa.put("id", command.getObjRuntimeEnv().getObjActKasse().getiRegId());
                        kassa.put("group", command.getObjRuntimeEnv().getObjActKasse().getiRegGrp());
                        kassa.put("kind", command.getObjRuntimeEnv().getObjActKasse().getType());
                        kassa.put("ip", command.getObjRuntimeEnv().getObjActKasse().getStrIpAdr());
                        runtime.put("kasse", kassa);

                        JSONObject testgp = new JSONObject();
                        testgp.put("emp", command.getObjRuntimeEnv().getObjActTG().getStrEmpId());
                        testgp.put("pass", command.getObjRuntimeEnv().getObjActTG().getStrPasswd());
                        testgp.put("tll", command.getObjRuntimeEnv().getObjActTG().getiTllId());
                        runtime.put("testgroup", testgp);

                        pathStrDir = message.getString("path");
                        JSONObject testc = new JSONObject();
                        testc.put("path", message.getString("path"));
                        testc.put("timestamp", dtf.format(command.getObjRuntimeEnv().getObjActTG().getObjActTC().getTimestamp()));
                        
                        testc.put("kassanr", command.getObjRuntimeEnv().getObjActTG().getObjActTC().getKassaNr());
                        runtime.put("testcase", testc);
                        
                        comm.put("classname", command.getClass().getName());
                        comm.put("runtime", runtime);
                        sendToServer(comm.toString());
                        break;
                    case "result":
                        FTPUtilCommand.deleteFolder(pathStrDir);
                        return message.getBoolean("positive");
                }
            } catch (SocketException s) {
                //s.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (IOException ex) {
                //System.out.println("Read failed");
            } catch (NullPointerException n) {
                Thread.currentThread().interrupt();
            } catch (JSONException j) {
                j.printStackTrace();
            }

        }
        return false;
    }

}
