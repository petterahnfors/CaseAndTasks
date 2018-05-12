/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import Model.Tasks;
import Model.Case;
import Observer.CaseObservable;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;
/**
 *
 * @author lenawikman
 */
public class DatabasController extends CaseObservable{
    static final String host="jdbc:mysql://127.0.0.1:3306/arendehantering?zeroDateTimeBehavior=convertToNull";
    static final String username="root";
    static final String password="1qaz2wsX!@"; //ange eventuellt lösenord
    
    private Connection con = null;
    private final PreparedStatement insertArende;
    private final PreparedStatement deleteArende;
    private final PreparedStatement selectArende;
    private final PreparedStatement insertArbetsuppgifter; 
    private final PreparedStatement deleteArbetsuppgifter;
    private final PreparedStatement selectArbetsuppgifter; 
    
    private final String arende_INSERT = "INSERT INTO arende (message) VALUES(?)";
    private final String arende_DELETE = "DELETE FROM arende WHERE arendeNr = ?";
    private final String arende_SELECT = "SELECT ? FROM arende";
    private final String arbetsuppgifter_INSERT = "INSERT INTO arbetsuppgifter VALUES(?)"; 
    private final String arbetsuppgifter_DELETE = "DELETE ? FROM arbetsuppgifter WHERE arbetsuppgNr = ?";
    private final String arbetsuppgifter_SELECT = "SELECT ? FROM arbetsuppgifter";
    
   
    public DatabasController() throws SQLException {
        connectToDb();
        selectArende = con.prepareStatement(arende_SELECT);
        insertArende = con.prepareStatement(arende_INSERT);
        deleteArende = con.prepareStatement(arende_DELETE);
        insertArbetsuppgifter = con.prepareStatement(arbetsuppgifter_INSERT);
        deleteArbetsuppgifter = con.prepareStatement(arbetsuppgifter_DELETE);
        selectArbetsuppgifter = con.prepareStatement(arbetsuppgifter_SELECT); 
    }
    
    public void connectToDb() throws SQLException {
        if (con == null) {
            try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection( host, username, password );
            } catch(ClassNotFoundException e) {
                System.out.println("Could not find SQL class");
            }
        }
    }
    
    public void closeDbConnection() throws SQLException {
        con.close();
        con = null;
    }

    //Hämtar högsta registrerade ärendenumret och returnerar det + 1
    public int getNewCaseNr ()throws SQLException{
        int nextNr = 0;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT max(arendeNr) As arendeNR FROM arende";
        ResultSet rs = stmt.executeQuery(sql);
           while(rs.next()){
               nextNr = rs.getInt("arendeNr");
           }
        nextNr = nextNr + 1;
        closeDbConnection();
        return nextNr;
    }
    //Hämtar högsta registrerade arbetsuppgiftnsummer och returnerar det + 1 
    public int getNewTaskNr () throws SQLException{
        int nextNr = 0;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT max(arbetsuppgNr) As arbetsuppgNr FROM arbetsuppgift";
        ResultSet rs = stmt.executeQuery(sql);
           while(rs.next()){
               nextNr = rs.getInt("arbetsuppgNr");
           }
        nextNr = nextNr + 1;
        closeDbConnection();
        
        return nextNr;
    }
    //hämta arbetsuppgifter för ärende
    public List<Tasks> getTasksforCase(int caseNr) throws SQLException {
        List<Tasks> lstTasks = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM arbetsuppgift WHERE arendeNr ="+caseNr+";";
        rs = stmt.executeQuery(sql);
        while(rs.next()) {
            lstTasks.add(new Tasks(rs.getInt("arbetsuppgNr"), rs.getInt("arendeNr"), rs.getString("beskrivning"), rs.getString("status"), rs.getDouble("budgeteradTid")));
        }
        closeDbConnection();
        return lstTasks;
    }
    //Hämta detaljerade arbetsuppgifter
    public List<Tasks> getDetailedTasks(int caseNr) throws SQLException {
        List<Tasks> lstTasks = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM arbetsuppgift WHERE arendeNr ='" + caseNr +"'";
        rs = stmt.executeQuery(sql); 
        while (rs.next()) {
            lstTasks.add(new Tasks(rs.getInt("arbetsuppgNr"), rs.getInt("arendeNr"), rs.getString("beskrivning"), rs.getString("status"), rs.getDouble("budgeteradTid"), rs.getInt("personalNr"), rs.getDouble("tidforbrukad"), rs.getString("kommentar"), rs.getString("attesteradAv")));
        }
        
        closeDbConnection();
        return lstTasks;
    }
    //Hämtar kompetens tillhörande ett personalnummer
    public String getCompetens(int personalNr) throws SQLException {
        String competens = "";
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT kompetens FROM personal WHERE personalNr =" + personalNr +";";
        rs = stmt.executeQuery(sql);
         while (rs.next()) {
             competens = rs.getString("kompetens");
         }
        closeDbConnection();
        return competens;
    }
    
    //Hämtar namn tillhörande ett personalnummer
        public String getName(int personalNr) throws SQLException {
        String name = "";
        String fName = "";
        String lName = "";
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql = "SELECT fornamn, efternamn FROM personal WHERE personalNr =" + personalNr +";";
        rs = stmt.executeQuery(sql);
         while (rs.next()) {
             fName = rs.getString("fornamn");
             lName = rs.getString("efternamn");
         }
        closeDbConnection();
        name = fName + lName;
        return name;
    }
        
    //Hämta alla ärenden beroende på status
    public List<Case> getCases(boolean status) throws SQLException {
        List<Case> lstCase = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql1 = "SELECT * FROM arende WHERE NOT status = 'Avslutat'";
        String sql2 = "SELECT * FROM arende";
        if (status) {
            rs = stmt.executeQuery(sql1);    
        }
        else {
            rs = stmt.executeQuery(sql2);
        }
        while (rs.next()) {
            lstCase.add(new Case(rs.getInt("arendeNr"), rs.getString("kategori"), rs.getString("status"), rs.getString("instruktioner")));
        }
        closeDbConnection();
        return lstCase;
    }
    //Hämtar ett ärende 
    public List<Case> getCase(String caseNr) throws SQLException{
       List<Case> lstCase = new ArrayList<>();
       ResultSet rs = null;
       connectToDb();
       Statement stmt = con.createStatement(); 
       String sql = "SELECT * FROM arende WHERE arendeNr ='" + caseNr +"'";
       rs = stmt.executeQuery(sql);
       while (rs.next()) {
            lstCase.add(new Case(rs.getInt("arendeNr"), rs.getString("kategori"), rs.getString("status"), rs.getString("instruktioner")));
        }
       closeDbConnection();
       return lstCase;
    }
    
    //lägg till ärende i databas
    public void saveCaseToDatabase(String arendeNr, String instructions, String category, String status) throws SQLException{
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String insert = "INSERT INTO arende VALUES" +"("+ arendeNr +", "+ "\""+ instructions +"\", "+ "\""+status+"\", " +"\""+ category+"\");";
        System.out.println(insert);
        stmt.executeUpdate(insert);
        closeDbConnection();
        super.notifyCaseObservers();
    }
    //Lägg till arbetsuppgift i databas
    public void addTaskToDatabase(int taskNr, int caseNr, String taskDesc, double timeBudget, String uppgStatus) throws SQLException{
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String insert = "INSERT INTO arbetsuppgift (arbetsuppgNr, arendeNr, beskrivning, budgeteradTid, tidforbrukad, status) VALUES " + "("+taskNr+", "+caseNr+", '"+taskDesc+"', "+timeBudget+", 0, '"+uppgStatus+"');";
        System.out.println(insert);
        stmt.executeUpdate(insert);        
        closeDbConnection();
    }
    
//    public void updateCaseStatus (int caseNr, String status) throws SQLException {
//        connectToDb();
//        Statement stmt =(Statement)con.createStatement();
//        String insert = "UPDATE arende SET status = " + status + "WHERE arendeNr =" + caseNr;
//        System.out.println(insert);
//        stmt.executeUpdate(insert);        
//        closeDbConnection();
//    }
    
    //uppdaterar ärende i databas
     public void updateCase (int caseNr,  String category,  String status,  String instructions) throws SQLException {
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arende SET kategori ='" + category + "', status ='"+ status + "', instruktioner ='"+ instructions + "' WHERE arendeNr =" + caseNr;
        System.out.println(update);
        stmt.executeUpdate(update);        
        closeDbConnection();
    }
     //uppdaterar arbetsuppgift i databas
     public void updateTask (int taskNr, int staffNr,  String description, double timeBudget, double timeUsed, String status) throws SQLException {
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arbetsuppgift SET personalNr ="+ staffNr +", beskrivning ='" + description + "', budgeteradTid =" + timeBudget + ", tidforbrukad ="+ timeUsed + ", status='"+ status + "' WHERE arbetsuppgNr =" + taskNr + ";";
        System.out.println(update);
        stmt.executeUpdate(update);        
        closeDbConnection();
    }
    //Metod för att uppdatera arbetsuppgift anpassat för webgränssnitt
    public void updateTaskOnWebPage(int taskNr, double timeUsed, String taskStatus, String comment) throws SQLException {
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arbetsuppgift SET tidforbrukad ="+ timeUsed +", status ='" + taskStatus + "', kommentar ='" + comment + "' WHERE arbetsuppgNr =" + taskNr + ";";
        System.out.println(update);
        stmt.executeUpdate(update);        
        closeDbConnection();
    }
     
     //Hämtar ett ärendes kategori
     public void getCategoryForCase(int caseNr) throws SQLException {
        connectToDb(); 
        Statement stmt =(Statement)con.createStatement();
        String select = "SELECT kategori FROM arende WHERE arendeNr = " + caseNr +";" ;
        System.out.println(select);
        stmt.executeUpdate(select);        
        closeDbConnection();
     }
     
     public void asignTask(int taskNr, int personalNr) throws SQLException {
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arbetsuppgift SET personalNr ='" + personalNr + "' WHERE arbetsuppgNr =" + taskNr +";";
        System.out.println(update);
        stmt.executeUpdate(update);
        closeDbConnection();  
     }
     //Attestera arbetsuppgift
     public void attestTask(int taskNr, String name) throws SQLException{
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arbetsuppgift SET attesteradAv ='" + name + "' WHERE arbetsuppgNr =" + taskNr +";";
        System.out.println(update);
        stmt.executeUpdate(update);
        closeDbConnection();
     }
     

     
     //Hämta alla arbetsuppgifter beroende på status
    public List<Tasks> getActiveTasks(boolean status) throws SQLException {
        List<Tasks> lstTasks = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql1 = "SELECT * FROM arbetsuppgift WHERE status NOT IN ('Påbörjad', 'Avslutat');";
        String sql2 = "SELECT * FROM arbetsuppgift;";
        if (status) {
            rs = stmt.executeQuery(sql2);    
        }
        else {
            rs = stmt.executeQuery(sql1);
        }
        while (rs.next()) {
            lstTasks.add(new Tasks(rs.getInt("arbetsuppgNr"), rs.getInt("arendeNr"), rs.getString("beskrivning"), rs.getString("status"), rs.getDouble("budgeteradTid"), rs.getInt("personalNr"), rs.getDouble("tidforbrukad"), rs.getString("kommentar"), rs.getString("attesteradAv") ));
        }
        closeDbConnection();
        return lstTasks;
    }
    
         //Hämta alla arbetsuppgifter beroende på status
    public List<Tasks> getTasksForPerson(boolean status, int personalNr) throws SQLException {
        List<Tasks> lstTasks = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql1 = "SELECT * FROM arbetsuppgift WHERE status NOT IN ('Avslutat') AND personalNr =" + personalNr +";";
        String sql2 = "SELECT * FROM arbetsuppgift;";
        if (status) {
            rs = stmt.executeQuery(sql2);    
        }
        else {
            rs = stmt.executeQuery(sql1);
        }
        while (rs.next()) {
            lstTasks.add(new Tasks(rs.getInt("arbetsuppgNr"), rs.getInt("arendeNr"), rs.getString("beskrivning"), rs.getString("status"), rs.getDouble("budgeteradTid"), rs.getInt("personalNr"), rs.getDouble("tidforbrukad"), rs.getString("kommentar"), rs.getString("attesteradAv") ));
        }
        closeDbConnection();
        return lstTasks;
    }
    
             //Hämta alla ej påbörjade arbetsuppgifter beroende på status
    public List<Tasks> getNewTasksForPersonCompetens(int personalNr) throws SQLException {
        List<Tasks> lstTasks = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql1 = "Select ar.arbetsuppgNr, ar.arendeNr, ar.beskrivning, ar.status, ar.budgeteradTid, ar.personalNr, ar.tidforbrukad, ar.kommentar, ar.attesteradAv, a.kategori FROM arbetsuppgift ar join arende a on a.arendeNr = ar.arendeNr where ar.status NOT IN ('Avslutat', 'Påbörjad') AND a.kategori = (SELECT kompetens from personal where personalNr =" + personalNr +") AND (ar.personalNr IS NULL);";
        rs = stmt.executeQuery(sql1);
        while (rs.next()) {
            lstTasks.add(new Tasks(rs.getInt("arbetsuppgNr"), rs.getInt("arendeNr"), rs.getString("beskrivning"), rs.getString("status"), rs.getDouble("budgeteradTid"), rs.getInt("personalNr"), rs.getDouble("tidforbrukad"), rs.getString("kommentar"), rs.getString("attesteradAv") ));
        }
        closeDbConnection();
        return lstTasks;
    }


         
     }
     
     
