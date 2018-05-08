/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;


import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author petter
 */
//@Named(value = "caseAndTasksBean")
@ManagedBean(name="messageBean")
@Dependent
public class CaseAndTasksBean {

    /**
     * Creates a new instance of CaseAndTasksBean
     */
    public CaseAndTasksBean() {
    }
    
}
