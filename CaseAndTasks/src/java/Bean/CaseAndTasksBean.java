/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;

import Controller.DatabasController;
import Model.Tasks;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
//import javax.inject.Named;


/**
 *
 * @author petter
 */
@ManagedBean(name="CaseAndTasksBean")
@SessionScoped


public class CaseAndTasksBean implements Serializable{
    private int personalNr;
    private DatabasController dataController; 
    private String loginMessage;
    private String competens;
    private String name;
    private HtmlDataTable tableTask;
    private Tasks myTask = new Tasks();
    private List <Tasks> myTasks;

    public List<Tasks> getMyTasks() {
        return myTasks;
    }

    public void setMyTasks(List<Tasks> myTasks) {
        this.myTasks = myTasks;
    }

    public Tasks getMyTask() {
        return myTask;
    }

    public void setMyTask(Tasks myTask) {
        this.myTask = myTask;
    }
    
    
    public HtmlDataTable getTableTask() {
        return tableTask;
    }

    public void setTableTask(HtmlDataTable tableTask) {
        this.tableTask = tableTask;
        
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }
    //Hämtar data från aktiv rad i dataTable och tilldelar datan till myTask
    public void dataTableTask(){
        myTask = (Tasks) getTableTask().getRowData();
        int tNr = myTask.getTaskNr();
        double time = myTask.getTimeUsed();
        String status = myTask.getTaskStatus();
        String comm = myTask.getComment();
        
        System.out.println(time + " : " + status + " : " + comm);
        updateTask(tNr, time, status, comm);
    }
    
    public String getName() {
        return name;
    }

    public void setName() {
        init();
        String name = "";
        try {
            name = dataController.getName(personalNr);
        } catch (SQLException ex) {
            System.out.println("Kunde ej namn för angivet personalnummer" + ex.getMessage());
        }
        this.name = name;
    }

    public String getCompetens() {
        return competens;
    }

    public void setCompetens() {
        init();
        String competens = "";
        try {
            competens = dataController.getCompetens(personalNr);
        } catch (SQLException ex) {
            System.out.println("Kunde ej hämta kompetens för angivet personalnummer" + ex.getMessage());
        }
        this.competens = competens;
    }


    public int getPersonalNr() {
        return personalNr;
    }
    
    public void login(int personalNr){
        setPersonalNr(personalNr);
        setCompetens();
        setName();
        loadActivePage();
    }
    
    public void setPersonalNr(int personalNr) {
        this.personalNr = personalNr;
    }

    //Metod för att ladda sidan mina ärenden
    public void loadMyTasksPage() {
       ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
       try {
           initTable();
           externalContext.redirect("MinaArenden.xhtml");
     } catch (Exception e)
       {
           System.out.println(e);
       }    
    }
    
    //metod för att ladda sida med aktiva ärenden som ännu ingen person har blivit tilldelad. Listan tar hänsyn till inloggads kompetens.
    public void loadActivePage() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
       try {
            if (competens.isEmpty()) {
            loginMessage = "Personalnummer eller kompetens kopplat till personalnumret saknas";
        }
        else{
           getTasksForPerson();
           externalContext.redirect("AktivaArenden.xhtml");
           loginMessage = "";
        }
        
     } catch (Exception e)
       {
           System.out.println(e);
       }
    }
    //Metod för att uppdatera uppgift i databasen
    public void updateTask(int taskNr, double timeUsed, String taskStatus, String comment) {
        init();
        try {
            dataController.updateTaskOnWebPage(taskNr, timeUsed, taskStatus, comment);
        } catch (SQLException ex) {
            System.out.println("Gick ej att uppdatera uppgiften" + ex.getMessage());
        }
    }
    //Metod för att hämta inloggad persons uppgifter
    public void getTasksForPerson() throws SQLException {
        init();
        initTable();
        myTasks = dataController.getTasksForPerson(false, personalNr);
    }

    //Metod för att hämta nya uppgifter
    public List<Tasks> getNewTasksForPersonCompetens() throws SQLException {
    init(); 
    initTable();
    return dataController.getNewTasksForPersonCompetens(personalNr);
    }
    
    //Tilldela uppgift till inloggad
    public void asignTask(int taskNr){
        init();
        try {
            dataController.asignTask(taskNr, personalNr);
            getTasksForPerson();
        } catch (SQLException ex) {
            System.out.println("Gick ej att tilldela uppgiften" + ex.getMessage());
        }
    }
    
    // Skapar en ny instans av CaseAndTasksBean
    public CaseAndTasksBean() {
        loginMessage = "";
        init();
        initTable();
}
    //metod för att komma runt problematiken kring binding av dataTable som kräver ny instans
    public void initTable() {
        try {
            tableTask = new HtmlDataTable();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //ny instans av databasControllern
    public void init() {
        try { 
            dataController = new DatabasController();
            
        } catch (SQLException ex) {
           System.out.println("Could not connect to database.");
        }
}
}
