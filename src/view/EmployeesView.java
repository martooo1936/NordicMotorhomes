package view;

import controller.AdminController;
import controller.Helper;


import db.PersonWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Employee;
import javafx.scene.control.*; import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.input.KeyEvent;


//most of the methods are made by Martin
public class EmployeesView implements Initializable
{


    //region FXML elements

     @FXML
     TextField firstName,lastName,cpr,eMail,address,phoneNum,accNo,regNr,drLicense;
     @FXML
    PasswordField pass;
    @FXML
    ChoiceBox exitOptions;

    @FXML
    Button deleteEmpl,saveEmpl,updateButton;

     @FXML
     private TableView<Employee> employeeTabableView;
     @FXML
     private TableColumn<String,Employee> fNameClm;
     @FXML
     private TableColumn<String,Employee> lNameClm;
     @FXML
     private TableColumn<String,Employee> cprClm;
     @FXML
     private TableColumn<String,Employee> pssClm;
     @FXML
     private TableColumn<String,Employee> postClm;
     @FXML
     private TableColumn<String,Employee> drLicenseClm;
     @FXML
     private TableColumn<String,Employee> emailClm;
     @FXML
     private TableColumn<String,Employee> addressClm;
     @FXML
     private TableColumn<String,Employee> phoneNumClm;
     @FXML
     private TableColumn<String,Employee> accNumClm;
     @FXML
     private TableColumn<String,Employee> regNumClm;
     @FXML
     private ChoiceBox<String> possition;




    // endregion

    private AdminController adm = new AdminController ();
    private Helper converter = new Helper();
    private Screen screen = new Screen();
    private Employee selectedEmployee;
    private static final int LIMIT = 10;


    // region save employee
    public void setPossition()
     {

     }


    public boolean checkforEmpty() // Martin
    {
        if (firstName.getText().isEmpty()||lastName.getText().isEmpty()||cpr.getText().isEmpty()|| drLicense.getText().isEmpty()||/*possition.getValue().toString().isEmpty()||*/eMail.getText().isEmpty()||address.getText().isEmpty()||phoneNum.getText().isEmpty()||accNo.getText().isEmpty()||regNr.getText().isEmpty())
        {
            return false;
        }
            return true;
    }


    public void saveEmployee(ActionEvent event)//Martin
    {


        saveEmpl.setVisible(true);
        updateButton.setVisible(false);

           if (possition.getValue()==null)
           {
               Helper.displayError("ERROR",null,"Please fill the required information");
               return ;
           }
        if (!checkforEmpty())
        {
            Helper.displayError("ERROR",null,"Please fill the required information");
            return ;

        }
           if (pass.getText().isEmpty()){
               Helper.displayError("ERROR",null,"Please fill the required information");

           }
          else
           {
            adm.saveEmployee(firstName.getText(),lastName.getText(),cpr.getText(),pass.getText(), drLicense.getText(),possition.getValue().toString() ,eMail.getText(),address.getText(),phoneNum.getText(),accNo.getText(),regNr.getText());
            Helper.dispplayConfirmation("Confirmation Dialog",null,"Operation has been successful");
               clearEmployeeFields();
                loadData();


        }

    }
    public void clearEmployeeFields(){ //Martin

        firstName.clear();
        lastName.clear();
        cpr.clear();
        pass.clear();
        drLicense.clear();
        possition.setValue(null);
        eMail.clear();
        address.clear();
        phoneNum.clear();
        accNo.clear();
        regNr.clear();

    }
// endregion

    public void deleteEmployee(ActionEvent actionEvent) // Martin
    {
        if (selectedEmployee==null)
        {
            Helper.displayError("Attention",null,"Please select employee from the table above");
           clearEmployeeFields();
           return;
        }
       adm.deleteEmployee(selectedEmployee);
        loadData();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) // Martin
    {
         loadData();

        saveEmpl.setVisible(false);
        updateButton.setVisible(false);
        exitOptions.setItems(FXCollections.observableArrayList("Log out", "Exit"));
        possition.getItems().addAll("admin","assistant","mechanic","cleaner","accountant");


    }
    public void loadData() //Martin
    {
        fNameClm.setCellValueFactory(
                new PropertyValueFactory("firstName"));
        lNameClm.setCellValueFactory(new PropertyValueFactory("lastName"));
        addressClm.setCellValueFactory(new PropertyValueFactory("address"));
        cprClm.setCellValueFactory(new PropertyValueFactory("cpr"));
        drLicenseClm.setCellValueFactory(new PropertyValueFactory("driverLicense"));
        emailClm.setCellValueFactory(new PropertyValueFactory("eMail"));
        phoneNumClm.setCellValueFactory(new PropertyValueFactory("phoneNum"));
        accNumClm.setCellValueFactory(new PropertyValueFactory("accNo"));
        regNumClm.setCellValueFactory(new PropertyValueFactory("regNr"));
        pssClm.setCellValueFactory(new PropertyValueFactory("pass"));
        postClm.setCellValueFactory(new PropertyValueFactory("status"));

        ObservableList<Employee> empls = FXCollections.observableArrayList();
        empls.addAll(adm.loadEmployee());
        employeeTabableView.setItems(empls);
    }

    public void exitOrLogOut(javafx.scene.input.MouseEvent mouseEvent)
    {
        screen.exitOrLogOut(mouseEvent, exitOptions);
    }



    public void emplTableAct(MouseEvent mouseEvent)
    {

    }


   //region restrict input
    public void cprRestrict(KeyEvent keyEvent)//Martin
    {

        Screen.restrictIntInput(cpr);
        cpr.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()){
                    if (cpr.getText().length()> LIMIT){
                        cpr.setText(cpr.getText().substring(0,LIMIT));
                    }
                }
            }
        });

    }




    public void drLicenseRestrcit(KeyEvent keyEvent)//Martin
    {
        Screen.restrictIntInput(drLicense);
        drLicense.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()> oldValue.intValue())
                {
                    if (drLicense.getText().length()> LIMIT-1)
                    {
                        drLicense.setText(drLicense.getText().substring(0,LIMIT-1));
                    }
                }
            }
        });
    }

    public void phoneRestrict(KeyEvent keyEvent) // Martin
    {
        Screen.restrictIntInput(phoneNum);
        phoneNum.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()> oldValue.intValue())
                {
                if (phoneNum.getText().length()> LIMIT-2)
                {
                    phoneNum.setText(phoneNum.getText().substring(0,LIMIT-2));
                }
                }
            }
        });
        {

        }
    }

    public void restrictAccNo(KeyEvent keyEvent)//Martin
    {
        Screen.restrictIntInput(accNo);
        accNo.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()> oldValue.intValue())
                {
                if (accNo.getText().length()>LIMIT)
                {
                    accNo.setText(accNo.getText().substring(0,LIMIT));
                }
                }
            }
        });
    }

    public void restrictRegNr(KeyEvent keyEvent) // Martin
    {
        Screen.restrictIntInput(regNr);
        regNr.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue()> oldValue.intValue())
                {
                    if (regNr.getText().length()>LIMIT-6)
                    {
                        regNr.setText(regNr.getText().substring(0,LIMIT-6));
                    }
                }
            }
        });
    }
      // endregion
    public void createNewEmpl(ActionEvent event) // Martin
    {

        clearEmployeeFields();
        saveEmpl.setVisible(true);
        updateButton.setVisible(false);


    }


    public void selectEmployee(MouseEvent mouseEvent) //Martin
    {
        clearEmployeeFields();
        saveEmpl.setVisible(false);
        updateButton.setVisible(true);
        loadEmployeeData();


    }


      // region update employee

    private void loadEmployeeData() //Martin
    {
        selectedEmployee = employeeTabableView.getSelectionModel().getSelectedItem();
        firstName.setText(selectedEmployee.getFirstName());
        lastName.setText(selectedEmployee.getLastName());
        cpr.setText(selectedEmployee.getCpr());
       // pass.setText(selectedEmployee.getPass());
        drLicense.setText(selectedEmployee.getDriverLicense());
        possition.setValue(selectedEmployee.getStatus());
        eMail.setText(selectedEmployee.getEMail());
        address.setText(selectedEmployee.getAddress());
        phoneNum.setText(selectedEmployee.getPhoneNum());
        accNo.setText(selectedEmployee.getAccNo());
        regNr.setText(selectedEmployee.getRegNr());

    }

    public void updateEmployee(ActionEvent event) //Martin
    {



        if (!checkforEmpty()){
            Helper.displayError("ERROR",null,"Please fill the required information");
            return ;
        }
        else {
            adm.updateEmployee(selectedEmployee, firstName, lastName, cpr, pass, drLicense, possition, eMail, address, phoneNum, accNo, regNr);
            loadData();
        }
    }
      // endregion

    public void goBack(ActionEvent actionEvent)
    {

    }


}
