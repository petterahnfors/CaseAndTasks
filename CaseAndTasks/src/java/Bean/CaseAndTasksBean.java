/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;


import Controller.DatabasController;
import Model.Tasks;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author petter
 */
@ManagedBean(name="CaseAndTasksBean")
@SessionScoped

public class CaseAndTasksBean implements Serializable{
 
    private int taskNr; 
    private int caseNr;
    private String description; 
    private String taskStatus; 
    private double timeBudget; 
    private int personalNr;
    private double timeUsed;
    private String comment;
    private String attestedBy;
    private DatabasController dataController; 
    
    public int getTaskNr() {
        return taskNr;
    }

    public void setTaskNr(int taskNr) {
        this.taskNr = taskNr;
    }

    public int getCaseNr() {
        return caseNr;
    }

    public void setCaseNr(int caseNr) {
        this.caseNr = caseNr;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public double getTimeBudget() {
        return timeBudget;
    }

    public void setTimeBudget(double timeBudget) {
        this.timeBudget = timeBudget;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAttestedBy() {
        return attestedBy;
    }

    public void setAttestedBy(String attestedBy) {
        this.attestedBy = attestedBy;
    }
   
    /**
     * @throws java.sql.SQLException 
     * @return 
     */
    
    public List<Tasks> getActiveTasks() throws SQLException {
    init(); 
    return dataController.getActiveTasks();
    }
    
    
    // Skapar en ny instans av CaseAndTasksBean
    public CaseAndTasksBean() {
        init(); 
}
    public void init() {
        try { 
            dataController = new DatabasController();
        } catch (SQLException ex) {
           System.out.println("Could not connect to database.");
        }
}
}
