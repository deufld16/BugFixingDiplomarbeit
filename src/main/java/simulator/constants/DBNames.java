package simulator.constants;

/**
 * Enumeration der vorhandenen Datenbanken.<bR>
 * 
 * @author fbuerger
 *
 * @version 1.0.0
 */
public enum DBNames {
  DB_NAME_NPOS, DB_NAME_HDB, DB_NAME_ETILAG, DB_NAME_ESLLOG, PROMETHEUS_TEST, PROMETHEUS_PROD, KARSTADT_TEST,
  KARSTADT_PROD;

  /**
   * Konvertierungsmethode der Enumeration zu einem String-Wert.<bR>
   * 
   * @author fbuerger
   * 
   * @version 1.0.0
   */
  @Override
  public String toString() {
    switch (this) {
      case DB_NAME_NPOS:
        return "npos";
      case DB_NAME_HDB:
        return "historyd";
      case DB_NAME_ETILAG:
        return "etilag";
      case DB_NAME_ESLLOG:
        return "esllog";
      case PROMETHEUS_TEST:
        return "prometheus_test";
      case KARSTADT_PROD:
        return "karstadt_prod";
      case KARSTADT_TEST:
        return "karstadt_test";
      case PROMETHEUS_PROD:
        return "prometheus_prod";
      default:
        break;
    }
    return "";
  }
}