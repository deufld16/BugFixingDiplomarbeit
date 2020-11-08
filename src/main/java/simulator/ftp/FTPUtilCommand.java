/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.ftp;

import remote.ftp.*;
import general.bl.GlobalAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import remote.bl.RemoteExecutionManager;
import simulator.bl.ExecutionManager;

/**
 *
 * @author Lukas Krobath
 */
public class FTPUtilCommand {

    private static final int PORTNR = 7489;
    private static final String USER = "rewe";
    private static final String PASSWORD = "dstore";
    private static int countFiles = 0;

    public static String[] list(String target) throws IOException {
        FTPClient ftp = new FTPClient();
        String[] filenameList;
        try {
            ftp.connect(ExecutionManager.getInstance().getActiveSystem().getBackoffice().getStrIpAdr(), PORTNR);
            ftp.login(USER, PASSWORD);
            filenameList = ftp.listNames(target);
            ftp.logout();
        } finally {
            ftp.disconnect();
        }
        return filenameList;
    }




    public static boolean deleteFolder(String remoteFile) throws IOException {
        FTPClient ftp = new FTPClient();
        FileInputStream fis = null;
        boolean res = true;

        try {
            ftp.connect(ExecutionManager.getInstance().getActiveSystem().getBackoffice().getStrIpAdr(), PORTNR);
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            for (FTPFile listFile : ftp.listFiles(remoteFile)) {
                ftp.deleteFile(remoteFile + "/" + listFile.getName());
            }
            ftp.removeDirectory(remoteFile);
            if (!ftp.logout()) {
                res = false;
            }
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
            ftp.disconnect();
        }

        return res;
    }



    public static boolean upload(String localSourceFile, String remoteResultFile) throws IOException {
        FTPClient ftp = new FTPClient();
        FileInputStream fis = null;
        boolean res = true;

        try {
            ftp.connect(ExecutionManager.getInstance().getActiveSystem().getBackoffice().getStrIpAdr(), PORTNR);
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            fis = new FileInputStream(localSourceFile);
            if (!ftp.storeFile(remoteResultFile, fis)) {
                res = false;
            }
            System.out.println(ftp.getReplyString());
            if (!ftp.logout()) {
                res = false;
            }
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
            ftp.disconnect();
        }

        return res;
    }
}
