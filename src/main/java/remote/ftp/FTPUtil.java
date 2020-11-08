/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.ftp;

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
public class FTPUtil {

    private static final int PORTNR = 7487;
    private static final String USER = "rewe";
    private static final String PASSWORD = "dstore";
    private static int countFiles = 0;

    public static String[] list(String target) throws IOException {
        FTPClient ftp = new FTPClient();
        String[] filenameList=new String[1];
        try {
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
            System.out.println(ftp.getReplyString());
            ftp.enterLocalPassiveMode();
            System.out.println(ftp.getReplyString());
            ftp.login(USER, PASSWORD);
            
            System.out.println(ftp.getStatus());
            
            filenameList = ftp.listNames(target);
            System.out.println(ftp.getReplyString() + ftp.listHelp());
            for (String string : filenameList) {
                System.out.println(string);
            }
            ftp.logout();
            System.out.println("Hilf mir Jesus" + ftp.getReplyString());
        }catch(IOException i){
            i.printStackTrace();
        } finally {
            ftp.disconnect();
        }
        return filenameList;
    }

//    public static boolean downloadErgs(Path localRoot, String remote) throws IOException {
//        FTPClient ftp = new FTPClient();
//        boolean res = true;
//        try {
//            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
//            if (!ftp.login(USER, PASSWORD)) {
//                res = false;
//            }
//            createErgs(ftp, remote, localRoot);
//            if (!ftp.logout()) {
//                res = false;
//            }
//        } finally {
//            ftp.disconnect();
//        }
//
//        return res;
//    }
    public static boolean downloadErg(Path localRoot, String remote, int amount) throws IOException {
        FTPClient ftp = new FTPClient();
        boolean res = true;

        try {
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
            ftp.enterLocalPassiveMode();
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            createErg(ftp, remote, localRoot, amount);
            if (!ftp.logout()) {
                res = false;
            }
        } finally {
            ftp.disconnect();
        }

        return res;
    }

    private static void updateProgressbar(int amount) {
        RemoteExecutionManager.getInstance().getPanel().getPbStatus().setString("Download (" + countFiles + "/" + amount + ")");
        RemoteExecutionManager.getInstance().getPanel().getPbStatus().setValue(countFiles);
    }

    public static int ergAmount(String remote) throws IOException {
        int count = 0;
        FTPClient ftp = new FTPClient();
        boolean res = true;
        try {
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
            ftp.enterLocalPassiveMode();
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            count = countErg(ftp, remote);
            if (!ftp.logout()) {
                res = false;
            }
        } finally {
            ftp.disconnect();
        }
        return count;
    }

    public static boolean deleteFolder(String remoteFile) throws IOException {
        FTPClient ftp = new FTPClient();
        FileInputStream fis = null;
        boolean res = true;

        try {
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
            ftp.enterLocalPassiveMode();
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            killChildren(ftp, remoteFile);
            killDirectories(ftp, remoteFile);
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

    private static void createErgs(FTPClient ftp, String remoteFile, Path localRoot, int amount) throws IOException {
        FileOutputStream fos = null;
        for (FTPFile listDirectory : ftp.listFiles("/projekte" + remoteFile)) {
            if (listDirectory.isDirectory()) {
                File hilfe = new File(localRoot + File.separator + remoteFile.replaceAll("/", "\\" + File.separator) + File.separator + listDirectory.getName());
                hilfe.mkdirs();
                countFiles++;
                updateProgressbar(amount);
                createErgs(ftp, remoteFile + "/" + listDirectory.getName(), localRoot, amount);
            } else {
                fos = new FileOutputStream(new File(localRoot + File.separator + remoteFile.replaceAll("/", "\\" + File.separator) + File.separator + listDirectory.getName()));
                ftp.retrieveFile("/projekte" + remoteFile + "/" + listDirectory.getName(), fos);
                countFiles++;
                updateProgressbar(amount);
            }
        }
    }

    private static int countErgs(FTPClient ftp, String remoteFile) throws IOException {
        FileOutputStream fos = null;
        int count = 0;
        for (FTPFile listDirectory : ftp.listFiles("/projekte" + remoteFile)) {
            if (listDirectory.isDirectory()) {
                count++;
                count += countErgs(ftp, remoteFile + "/" + listDirectory.getName());
            } else {
                count++;
            }
        }
        return count;
    }

    private static int countErg(FTPClient ftp, String remoteFile) throws IOException {
        FTPFile[] list = ftp.listFiles("/projekte" + remoteFile);
        if (list.length > 0) {

            return countErgs(ftp, remoteFile + "/" + list[list.length - 1].getName())+1;
        }
        return 0;
    }

    private static void createErg(FTPClient ftp, String remoteFile, Path localRoot, int amount) throws IOException {
        FTPFile[] list = ftp.listFiles("/projekte" + remoteFile);
        if (list.length > 0) {
            File hilfe = new File(localRoot + File.separator + remoteFile.replaceAll("/", "\\" + File.separator) + File.separator + list[list.length - 1].getName());
            hilfe.mkdirs();
            createErgs(ftp, remoteFile + "/" + list[list.length - 1].getName(), localRoot, amount);
            countFiles++;
            updateProgressbar(amount);
            Path pathToErg = hilfe.toPath();
            Path ref = hilfe.toPath().getParent().getParent().resolve("ref");
            File[] files = ref.toFile().listFiles();
            ref = ref.resolve(files[files.length - 1].getName());
            if (pathToErg != null && ref != null) {
                GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("analyzer");
                GlobalAccess.getInstance().getPaAnalyzer().activate(ref, pathToErg);
            } else {

                ExecutionManager.getInstance().log("Fehler beim Starten des Analysators", ExecutionManager.LOGLEVEL.ERROR);
            }
        }

    }

    private static void killChildren(FTPClient ftp, String remoteFile) throws IOException {
        for (FTPFile listFile : ftp.listFiles(remoteFile)) {
            if (listFile.isFile()) {
                ftp.deleteFile(remoteFile + "/" + listFile.getName());
                System.out.println(ftp.getReplyString());
            } else if (listFile.isDirectory()) {
                if (!listFile.getName().equals("erg")) {
                    killChildren(ftp, remoteFile + "/" + listFile.getName());
                }
            }
        }
    }

    private static void killDirectories(FTPClient ftp, String remoteFile) throws IOException {
        for (FTPFile listFile : ftp.listDirectories(remoteFile)) {
            killDirectories(ftp, remoteFile + "/" + listFile.getName());
        }
        System.out.println(ftp.getReplyString());
        ftp.removeDirectory(remoteFile);
    }

    public static boolean createDirs(String remoteResultFile) throws IOException {
        FTPClient ftp = new FTPClient();
        FileInputStream fis = null;
        boolean res = true;

        try {
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
ftp.enterLocalPassiveMode();
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            ftp.makeDirectory(remoteResultFile);
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
            ftp.connect(RemoteExecutionManager.getInstance().getHost().getBackoffice().getStrIpAdr(), PORTNR);
            ftp.enterLocalPassiveMode();
            if (!ftp.login(USER, PASSWORD)) {
                res = false;
            }
            fis = new FileInputStream(localSourceFile);
            System.out.println(ftp.storeFile(remoteResultFile, fis));
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
