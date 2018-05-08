/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Case;
import Model.Tasks;
import java.util.ArrayList;

/**
 *
 * @author lenawikman
 */
public class ModelController {
    
     //deklarering av array caseList
    ArrayList<Case> caseList = new ArrayList();
       
    //deklarering av array taskList 
    ArrayList<Tasks> taskList = new ArrayList();
        
    Case c;
    Tasks a;
    
    //spara case i ArrayList
    public void addCase(Case Case){
        caseList.add(Case);
    
}
    //spara task i ArrayList
    public void addTask(Tasks task){
        taskList.add(task);
    }
    // registrera ärende
    public void regCase (String caseNr, String instructions, String category, String caseStatus){
        c = new Case();
        int nr = Integer.parseInt(caseNr);
        c.setCaseNr(nr);
        c.setCategory(category);
        c.setCaseStatus(caseStatus);
        c.setInstructions(instructions);
        addCase(c);
    }
    // registrera arbetsuppgift 
    public void regTask(int taskNr, int caseNr, String description, String taskStatus, double timeBudget){
        a = new Tasks();
        a.setTaskNr(taskNr);
        a.setCaseNr(caseNr);
        a.setDescription(description);
        a.setTaskStatus(taskStatus);
        a.setTimebudget(timeBudget);
        addTask(a);
    }
     
//    // hitta arbetsuppgift baserat på ÄrendeNr
//    public Tasks findTaskbyCaseNr(int caseNr) {
//       
//        for (Tasks ts: taskList) {
//            if (caseNr== ts.getCaseNr()){
//                a = ts;
//                return a;
//               }
//        }
//        a = null;
//        return a;
//    }
    
    
    
}
