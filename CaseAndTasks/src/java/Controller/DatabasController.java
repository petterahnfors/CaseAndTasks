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
        con = DriverManager.getConnection( host, username, password );
        
    }
    
    public void closeDbConnection() throws SQLException {
        con.close();
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
    
    //Hämta alla ärenden beroende på status
    public List<Case> getCases(boolean status) throws SQLException {
        List<Case> lstCase = new ArrayList<>();
        ResultSet rs = null;
        connectToDb();
        Statement stmt = con.createStatement();
        String sql1 = "SELECT * FROM arende WHERE NOT status = 'Avslutat'";
        String sql2 = "SELECT * FROM arende";
        if (status) {
            rs = stmt.executeQuery(sql2);    
        }
        else {
            rs = stmt.executeQuery(sql1);
        }
        while (rs.next()) {
            lstCase.add(new Case(rs.getInt("arendeNr"), rs.getString("kategori"), rs.getString("status"), rs.getString("instruktioner")));
        }
        closeDbConnection();
        return lstCase;
    }
    
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
        System.out.println("3");
        connectToDb();
        System.out.println("4");
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
     public void getCategoryForCase(int caseNr) throws SQLException {
        connectToDb(); 
        Statement stmt =(Statement)con.createStatement();
        String select = "SELECT kategori FROM arende WHERE arendeNr = " + caseNr +";" ;
        System.out.println(select);
        stmt.executeUpdate(select);        
        closeDbConnection();
     }
     
     public void attestTask(int taskNr, String name) throws SQLException{
        connectToDb();
        Statement stmt =(Statement)con.createStatement();
        String update = "UPDATE arbetsuppgift SET attesteradAv ='" + name + "' WHERE arbetsuppgNr =" + taskNr +";";
        System.out.println(update);
        stmt.executeUpdate(update);
        closeDbConnection();
     }
     
         
     }
     
     
