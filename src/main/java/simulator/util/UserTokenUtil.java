package simulator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.beans.RuntimeEnv;

public class UserTokenUtil {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(UserTokenUtil.class);

  private static final String CONST_WEBSERVICE_USER = "287079";
  private static final String CONST_WEBSERVICE_FUNC = "/TokenService/fipstoken/";

  /** Localhost */
  // private static final String BASE_URL = "http://127.0.0.1:8080/";
  // private static final String BASE_URL = "http://10.93.31.64:8080/";

  public static String generateUserToken(RuntimeEnv objRuntimeEnv) {
    HttpURLConnection objConnection = null;
    // String strUrl = "/TokenService/fipstoken/" + CONST_WEBSERVICE_USER;
    int iHttpResult = 0;
    BufferedReader brReader = null;
    StringBuilder sbResponse = null;

    logger.debug("Method getSessionID()");

    try {
      objConnection = getHttpConnection(objRuntimeEnv, "GET");

      iHttpResult = objConnection.getResponseCode();

      if (iHttpResult != HttpURLConnection.HTTP_OK) {
        logger.error("Could not establish Http Connection! Error Code: " + iHttpResult);
        return null;
      }

      logger.debug("Successfully established HTTP Connection");

      sbResponse = new StringBuilder();
      brReader = new BufferedReader(new InputStreamReader(objConnection.getInputStream(), "utf-8"));
      String line = null;

      while ((line = brReader.readLine()) != null) {
        sbResponse.append(line);
      }
      brReader.close();
    }
    catch (IOException ex) {
      logger.error(ex.getLocalizedMessage());
    }
    finally {
      if (Optional.ofNullable(objConnection).isPresent()) {
        objConnection.disconnect();
      }
    }

    logger.debug("Session ID: " + sbResponse.toString());

    return sbResponse.toString();
  }

  private static HttpURLConnection getHttpConnection(RuntimeEnv objRuntimeEnv, String strRequestMethod)
      throws IOException {
    HttpURLConnection objConnection = null;
    URL objURL = null;

    logger.debug("Method getHttpConnection()");

    String strWebServer = "http://" + objRuntimeEnv.getStrServerAddr() + ":8080" + CONST_WEBSERVICE_FUNC
        + CONST_WEBSERVICE_USER;

    try {
      // objURL = new URL(BASE_URL + strUrl);
      objURL = new URL(strWebServer);
      objConnection = (HttpURLConnection) objURL.openConnection();
      objConnection.setDoOutput(true);
      objConnection.setDoInput(true);
      objConnection.setConnectTimeout(5000);
      objConnection.setRequestProperty("Content-Type", "application/json");
      objConnection.setRequestProperty("Accept", "application/json");
      objConnection.setRequestProperty("Connection", "keep-alive");
      objConnection.setRequestMethod(strRequestMethod);
    }
    catch (IOException ex) {
      logger.error("Could not establish HTTP Connection to " + strWebServer);
      throw ex;
    }

    return objConnection;
  }

}
