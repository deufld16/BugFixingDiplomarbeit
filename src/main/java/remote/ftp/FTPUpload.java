/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.ftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import remote.bl.RemoteExecutionManager;
import remote.communication.RemoteClient;
import settings.io.Loader;

/**
 *
 * @author Lukas Krobath
 */
public class FTPUpload implements Runnable {

    private Path globalPath;
    private Path projektPath;
    private int amount = 0;
    private int cnt = 0;

    public FTPUpload(Path globalPath, Path projektPath) {
        this.globalPath = globalPath;
        this.projektPath = projektPath;
    }

    private int countDirectories(Path parent, Path path) {
        int count = 0;
        for (File listFile : path.toFile().listFiles()) {
            if (listFile.isDirectory()) {
                count++;
                if (!listFile.getName().equals("erg")) {
                    count += countDirectories(parent, listFile.toPath());
                }
            } else if (listFile.isFile()) {
                count++;
            }
        }
        return count;
    }

    private void copyDirectories(Path parent, Path path, String prefix) throws IOException {
        for (File listFile : path.toFile().listFiles()) {
            System.out.println(path);
            if (listFile.isDirectory()) {
                FTPUtil.createDirs(prefix + "/" + parent.relativize(listFile.toPath()).toString());
                cnt++;
                updateProgressbar();
                if (!listFile.getName().equals("erg")) {
                    copyDirectories(parent, listFile.toPath(), prefix);
                }
            } else if (listFile.isFile()) {
                System.out.println(FTPUtil.upload(listFile.getAbsolutePath(), prefix + "/" + parent.relativize(listFile.toPath()).toString()));
                cnt++;
                updateProgressbar();
            }
        }

    }

    private void updateProgressbar() {
        RemoteExecutionManager.getInstance().getPanel().getPbStatus().setString("Upload (" + cnt + "/" + amount + ")");
        RemoteExecutionManager.getInstance().getPanel().getPbStatus().setValue(cnt);
    }

    @Override
    public void run() {
        try {
            amount = countDirectories(globalPath, globalPath) + countDirectories(projektPath, projektPath);
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setEnabled(true);
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setStringPainted(true);
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setMaximum(amount);
            FTPUtil.list("");
            FTPUtil.deleteFolder("");
            FTPUtil.createDirs("global");
            FTPUtil.createDirs("projekte");
            copyDirectories(globalPath, globalPath, "global");
            copyDirectories(projektPath, projektPath, "projekte");
            JSONObject message = new JSONObject();
            message.put("type", "updatetargets");
            RemoteClient.getInstance().sendToServer(message.toString());
            RemoteExecutionManager.getInstance().getPanel().getBtTVStart().setEnabled(true);
            
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setEnabled(false);
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

}
