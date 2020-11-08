package simulator.communication;

/**
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import simulator.beans.RuntimeEnv;
import simulator.bl.ExecutionManager;
import simulator.recorder.util.RecorderCheckpoint;
import simulator.recorder.util.RecorderResponseWriter;

/**
 * callable for sending and receiving data from and to cash desk
 *
 * @author BKornsteiner
 *
 */
public class DTFConnection
        extends Thread {

    /**
     * Runtime Informationen
     */
    private RuntimeEnv objRuntimeEnv;

    /**
     * TCP Verbindung zur Kasse
     */
    private Socket server = null;

    /**
     * ID des gerade gesendeten Commands (wird laufend um 1 erhöht)
     */
    private long lCommandID = 0;

    /**
     * ID des Checkpoint Commands (letzter Command) eines Recorder Files
     */
    private long lcheckpointID = -1;

    /**
     * ID der letzten Antwort von der Kasse (wird benötigt, da Senden und
     * Empfangen asynchron ist)
     */
    private long lLastResponseID = -1;

    /**
     * Konstante für die PORT-Nummer
     */
    private final int CONNECTION_PORT = 6790;

    /**
     * Name des aktuellen Markers (wird zum Spechern der Ergebn)
     */
    private String strCurrentMarkerName = "";

    /**
     * Queue mit den letzten Recorder Checkpoints
     */
    private Queue<RecorderCheckpoint> queueRecorderCheckpoints = new LinkedList<>();

    /**
     * Timout Zeit für das Warten der Letzten Antwort der Kasse in Sekunden
     */
    private final int CONST_CONNECTION_TIMEOUT = 30;

    /**
     * Konstruktor
     *
     * @param objRuntimeEnv: Runtime Object
     */
    public DTFConnection(RuntimeEnv objRuntimeEnv) {
        this.objRuntimeEnv = objRuntimeEnv;
    }

    /**
     * Verbindung zur Kasse aufbauen
     */
    public void connectToCashDesk() {

        if (server == null) {
            //System.out.println(this.getClass().getSimpleName() + " creating new socket");
            try {
                String strConnectionIP = objRuntimeEnv.getObjActKasse().getStrIpAdr();
                server = new Socket(strConnectionIP, CONNECTION_PORT);
                //System.out.println(this.getClass().getSimpleName() + "Open socket on: " + server.getInetAddress()
                //    + " Port: " + server.getPort() + " local port: " + server.getLocalPort());
            } catch (Exception e) {
                //System.out.println("Konnte keine Verbindung zur Kasse herstellen");
                ExecutionManager.getInstance().log("Fehler bei der Verbindung zur Kassa mit der IP " + objRuntimeEnv.getObjActKasse().getStrIpAdr(), ExecutionManager.LOGLEVEL.ERROR); //e.printStackTrace();
            }
        }
    }

    /**
     * Wartet auf die restlichen Antworten der Kasse
     */
    private void waitForLastResponse() {
        int iSeconds = 1;
        while ((lcheckpointID != lLastResponseID)) {

            //System.out.println("Waiting for Last Response....");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (iSeconds == CONST_CONNECTION_TIMEOUT) {
                System.out.println("Connection Timeout after: " + iSeconds + " Seconds");
                break;
            }
            iSeconds++;
        }
    }

    /**
     * Schließt die Verbindung zur Kasse
     */
    public void closeConnection() {
        if ((server != null)) {
            waitForLastResponse();
            try {
                server.close();
                server = null;
                //System.out.println("Connection closed");
            } catch (IOException ioe) {
                //System.out.println("Fehler beim Schließen der Verbindung");
            }
        }
    }

    /**
     * Setzt den Marker Namen des aktuellen Recorders
     */
    public void updateCurrentMarkerName(String strMarkerName) {
        strCurrentMarkerName = strMarkerName;
        Thread.currentThread().setName(strCurrentMarkerName);
    }

    /**
     * Wandelt den Command in einen DTF-Command und schickt ihn anschließend an
     * die Kasse
     */
    public void sendDTFXmlCommand(String strcmd) {
        try {
            lCommandID++;

            sendMessage("<dtfcmd>\n<id>" + (lCommandID) + "</id>\n" + strcmd + "</dtfcmd> \n");
            /**
             * Falls es sich um einen Checkpoint Command hadelt --> Speichern
             * der ID und des Marker Namen des Recorders
             */
            if (strcmd.contains("<checkpoint>")) {
                lcheckpointID = lCommandID;

                RecorderCheckpoint recCheckPoint = new RecorderCheckpoint(strCurrentMarkerName, lcheckpointID);
                //System.out.println("--------------------------------------------> " + lcheckpointID);
                queueRecorderCheckpoints.add(recCheckPoint);
                strCurrentMarkerName = "";
            }

        } catch (Exception e) {
            //System.out.println("Fehler beim senden des DTF Commands");
            e.printStackTrace();
            closeConnection();
        }
    }

    /**
     * Wandelt den DTF-Command in ein byte Array um und sendet diesen
     * anschließend an die Kasse
     */
    private synchronized void sendMessage(String strDTFCommand)
            throws Exception {

        if (server != null) {
            int length = strDTFCommand.getBytes().length;

            // add the length of the message into a byte array with the length of 4
            byte[] newBytesArray = addValuein2ByteArray(length);

            // add the 4 byte array and the message byte array
            byte[] result = concat(newBytesArray, strDTFCommand.getBytes());

            OutputStream socketOutputStream = server.getOutputStream();
            try {
                socketOutputStream.write(result);
            } catch (SocketException se) {
                //System.out.println(this.getClass().getSimpleName() + " error while sending message to register.");

            }
//      System.out.println("\n" + this.getClass().getSimpleName() + " Sent message: \n " + strDTFCommand
//          + " to " + server.getInetAddress());
        }

    }

    /**
     * Run Methode des Threads
     */
    @Override
    public void run() {
        //System.out.println("Thread started.....");
        try {
            readAnswer();
        } catch (Exception e) {
            ExecutionManager.getInstance().log("Fehler beim Lesen des Ergebnisses", ExecutionManager.LOGLEVEL.ERROR);
        }
    }

    /**
     * Ließt laufend die Antworten von der Kasse sobald der Thread gestartet
     * wurde
     */
    private void readAnswer() {
        //System.out.println("############################################Reading");
        String message = null;
        InputStream input = null;
        LinkedList<String> listResponseLines = new LinkedList<>();

        try {

            input = server.getInputStream();
            //System.out.println(this.getClass().getSimpleName() + " start waiting for incoming messages.");

            while ((server != null) && (server.isConnected()) && (!server.isClosed())) { // read the message until
                // we
                // lose the connection
                // get the amount of data being sent
                byte[] lengthOfReplyAsArray = new byte[4];

                forceRead(input, lengthOfReplyAsArray);

                int lengthOfReply = byteArrayToInt(lengthOfReplyAsArray);

                // read the data into a byte array
                byte[] reply = new byte[lengthOfReply];
                reply = forceRead(input, reply);

                message = new String(reply);

                // -----------------------------------------------
                updateLastResponseID(message);
                // -----------------------------------------------

                //System.out.println(message + " read in Thread " + Thread.currentThread().getName());
                listResponseLines.add(message);

                /**
                 * Prüfen ob die Antwort eines Checkpoint Commands angekommen
                 * ist
                 */
                if (!queueRecorderCheckpoints.isEmpty() && message.contains("<sync/>")) {
                    //System.out.println(message);
                    Long lLastCheckPointID = queueRecorderCheckpoints.peek().getlLastCheckpointID();
                    //System.out.println(lLastCheckPointID);
                    if (lLastResponseID == lLastCheckPointID) {
                        RecorderCheckpoint recCheck = queueRecorderCheckpoints.poll();

                        String strSavePath = objRuntimeEnv.getObjActTG().getObjActTC().getStrErgPath();
                        //System.out.println("Schreiben");
                        RecorderResponseWriter responseWriter
                                = new RecorderResponseWriter(strSavePath, recCheck.getStrMarkerName());
                        responseWriter.writeData(listResponseLines);

                        listResponseLines.clear();
                       // System.out.println("\n ------------------Daten geschrieben---------------------- \n");
                    }

                }
            }

        } catch (IOException e) {
            /* ignored */
        } finally {
            try {
                input.close();
                closeConnection();
            } catch (Exception e) {
                /* ignored */
            }
        }
    }

    /**
     * Aktualisiert die ID der letzen Antwort von der Kasse
     */
    private void updateLastResponseID(String message) {
        // Beispiel: <dtfresp><id>13</id><sync/></dtfresp>
        if (message.contains("<sync/>")) {
            int iStartId = message.indexOf("<id>") + "<id>".length();
            int iEndID = message.indexOf("</id>");

            String strResponseID = message.substring(iStartId, iEndID);
            long lResponseID = Long.parseLong(strResponseID);

            if (lLastResponseID < lResponseID) {
                lLastResponseID = lResponseID;
            }
        }
    }

    private byte[] forceRead(InputStream inputStream, byte[] result)
            throws IOException {

        int bytesRead = 0;
        int total = result.length;
        int remaining = total;

        while (((bytesRead = inputStream.read(result, total - remaining, remaining)) != -1) && (remaining > 0)) {
            remaining -= bytesRead;
        }

        return result;
    }

    private int byteArrayToInt(byte[] byteArray) {
        int result = 0;

        for (int i = 0; (i < byteArray.length) && (i < 8); i++) {
            result |= (byteArray[3 - i] & 0xff) << (i << 3);
        }
        return result;
    }

    /**
     * adds in 4 bytes the length of a string
     *
     * @param length
     * @return byte array
     */
    static private byte[] addValuein2ByteArray(int length) {
        return new byte[]{(byte) ((length >> 24) & 0xff), (byte) ((length >> 16) & 0xff),
            (byte) ((length >> 8) & 0xff), (byte) ((length >> 0) & 0xff),};
    }

    /**
     * joins the byte representation of the message and the leading 4 bytes
     * which contains the length of the stream
     *
     * @param newBytesArray
     * @param oldByteArray
     * @return
     */
    static private byte[] concat(byte[] newBytesArray, byte[] oldByteArray) {
        byte[] result = Arrays.copyOf(newBytesArray, newBytesArray.length + oldByteArray.length);
        System.arraycopy(oldByteArray, 0, result, newBytesArray.length, oldByteArray.length);
        return result;
    }

    public RuntimeEnv getObjRuntimeEnv() {
        return objRuntimeEnv;
    }

}
