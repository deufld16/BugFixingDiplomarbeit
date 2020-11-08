/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author A180953
 */
public class Change {
    private int userID;
    private int gegenstandID;
    private LocalDateTime changeDate;
    private int type;

    public Change(int userID, int gegenstandID, LocalDateTime changeDate, int type) {
        this.userID = userID;
        this.gegenstandID = gegenstandID;
        this.changeDate = changeDate;
        this.type = type;
    }
    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGegenstandID() {
        return gegenstandID;
    }

    public void setGegenstandID(int gegenstandID) {
        this.gegenstandID = gegenstandID;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    
    
    @Override
    public String toString() {
        return userID + " - " + gegenstandID + " - "  + changeDate;
    }
    
    
}
