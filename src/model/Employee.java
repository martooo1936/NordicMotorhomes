package model;

import db.PersonWrapper;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
//Martin
public class Employee extends Person
{
    private String accNo;
    private String regNr;

    private String pass;

    private PersonWrapper personWrapper = PersonWrapper.getInstance();

    public Employee()
    {
        super();
    }

    public Employee(String pass, String firstName, String lastName, String address, String driverLicense, String cpr, String eMail, String phoneNum)
    {
        super(pass, firstName, lastName, address, driverLicense, cpr, eMail, phoneNum);
    }

    public String getAccNo()
    {
        return accNo;
    }

    public void setAccNo(String accNo)
    {
        this.accNo = accNo;
    }

    public String getRegNr()
    {
        return regNr;
    }

    public void setRegNr(String regNr)
    {
        this.regNr = regNr;
    }

    public void storeEmployee()
    {
        personWrapper.saveNewEmployee(this);
    }

    public void save(TextField firstName, TextField lastName, TextField cpr, TextField drLicense, ChoiceBox possition, TextField eMail, TextField address, TextField phoneNum, TextField accNo, TextField regNr)
    {
        personWrapper.save(this.getId(), firstName, lastName, cpr, drLicense, possition, eMail, address, phoneNum, accNo, regNr);
    }

    public void updatePassword(PasswordField passwordField)
    {
        if (passwordField.getText().isEmpty())
        {
            return;
        }

        String newPass = personWrapper.addSalt(passwordField.getText());
        personWrapper.updatePassword(this.getId(), newPass);
    }

    public void deleteEmployee()
    {
        personWrapper.deleteEmployee(this.id);
    }


}
