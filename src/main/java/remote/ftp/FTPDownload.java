/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.ftp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import remote.bl.RemoteExecutionManager;
import settings.io.Loader;

/**
 *
 * @author Lukas Krobath
 */
public class FTPDownload implements Runnable {

    private Path projektPath;
    private String remote;
    private int amount = 0;
    private int cnt = 0;

    public FTPDownload(Path projektPath, String remote) {
        this.projektPath = projektPath;
        this.remote = remote;
    }
    

    @Override
    public void run() {
        try {
            amount = FTPUtil.ergAmount(remote);

            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setEnabled(true);
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setStringPainted(true);
            RemoteExecutionManager.getInstance().getPanel().getPbStatus().setMaximum(amount);
            FTPUtil.downloadErg(projektPath, remote,amount);
        } catch (IOException ex) {
            Logger.getLogger(FTPDownload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
