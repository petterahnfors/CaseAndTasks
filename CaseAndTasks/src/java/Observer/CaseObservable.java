/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Observer;

import java.util.ArrayList;

/**
 *
 * @author petter
 */
public class CaseObservable {
    private ArrayList<CaseObserver> observers = new ArrayList<CaseObserver>();
    
    public void addListener (CaseObserver o){
        observers.add(o);
    }
    
    public void removeListener (CaseObserver o){
        observers.remove(o);
    }
    
    public void notifyCaseObservers() {
        for(CaseObserver co : observers){
            co.update();
        }
    }
}
