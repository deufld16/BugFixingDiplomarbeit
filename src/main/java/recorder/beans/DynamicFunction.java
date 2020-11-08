/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

/**
 *
 * @author Maxi
 * 
 * Data class to depict a dynamic function
 */
public class DynamicFunction 
{
    private String btnName;
    private String viewText;
    private Command command;

    public DynamicFunction() {
    }

    public DynamicFunction(String btnName, String viewText, Command command) {
        this.btnName = btnName;
        this.viewText = viewText;
        this.command = command;
    }


    public String getBtnName() {
        return btnName;
    }

    public void setBtnName(String btnName) {
        this.btnName = btnName;
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
    
}
