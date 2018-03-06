package controller;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Depot;
import model.Employee;

import java.util.ArrayList;

public class AdminController {
    //Martin

    private Depot depot = new Depot();

    public void saveEmployee(String firstNameText, String lastNameText, String cprText, String passText,String driverText,String possitionText, String eMailText, String addressText, String phoneNumText, String accNoText, String regNrText)
    {
        Employee  employee = new Employee( passText,firstNameText,lastNameText,addressText,driverText,cprText,eMailText,phoneNumText);

        employee.setStatus(possitionText);
        employee.setAccNo(accNoText);
        employee.setRegNr(regNrText);
        employee.storeEmployee();
    }

    public ArrayList<Employee> loadEmployee()
    {
       return depot.getEmployees();
    }

    public void updateEmployee(Employee employee, TextField firstName, TextField lastName, TextField cpr,
                               PasswordField pass, TextField drLicense, ChoiceBox possition, TextField eMail,
                               TextField address, TextField phoneNum, TextField accNo, TextField regNr)
    {
        employee.save(firstName,lastName,cpr,drLicense,possition,eMail,address,phoneNum,accNo,regNr);
        employee.updatePassword(pass);
    }

    public void deleteEmployee(Employee e)
    {
        e.deleteEmployee();
    }



}
