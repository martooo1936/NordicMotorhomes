package view;

import com.jfoenix.controls.JFXButton;
import controller.COController;
import controller.Helper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.MouseEvent;
import model.Customer;
import model.ExtrasLineItem;
import model.Reservation;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
//most of the methods are made by Martin
public class CustomerDetailsView implements Initializable
{
    //Martin
    // region FXML elements
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Integer,Customer> customerIdClm;
    @FXML
    private TableColumn<String,Customer> customerCprClm;
    @FXML
    private TableColumn<String,Customer> firstNameClm;
    @FXML
    private TableColumn<String,Customer> lastNameClm;
    @FXML
    private TableColumn<String,Customer> emailClm;
    @FXML
    private TableColumn<String,Customer> phoneNumClm;
    @FXML
    private   Button saveNewCustomer, saveButton;
    @FXML
    private TextField firstNameTxt,lastNameTxt,cprTxt,drLicenseTxt,phoneNumTxt,emailTxt,addressTxt,passTxt, searchField;
    @FXML
    private Label passLabel;
    @FXML
    private ChoiceBox exitOptions;
    @FXML
    private Button createNewCustButton, assignButton;
    @FXML
    private TextField logField;

    // endregion
    private Customer selectedCustomer;
    private String screenToGoBack = "";
    private Screen screen = new Screen();
    private COController coController = new COController();
    private int custIDforNewReservation;
    private Reservation reservation;
    private ArrayList<ExtrasLineItem> lineItems = new ArrayList<>();
    private static final int LIMIT = 10;


    public boolean checkforEmpty() //Martin
    {
        if (firstNameTxt.getText().isEmpty()||lastNameTxt.getText().isEmpty()||cprTxt.getText().isEmpty()|| drLicenseTxt.getText().isEmpty()||phoneNumTxt.getText().isEmpty()||emailTxt.getText().isEmpty()||addressTxt.getText().isEmpty()){
            return false;
        }
        return true;
    }
   // region initialized
    @Override
    public void initialize(URL location, ResourceBundle resources) // Martin
    {
        createNewCustButton.setVisible(false);

        saveNewCustomer.setVisible(false);

        passTxt.setVisible(false);

        passLabel.setVisible(false);

        selectedCustomer = coController.getSelectedCustomer();

        if(COController.getSelectedRental() == null && COController.getSelectedReservation() != null)
        {
            screenToGoBack = "reservation.fxml";

            saveNewCustomer.setVisible(false);

            createNewCustButton.setVisible(false);

            assignButton.setVisible(false);

            createNewCustButton.setVisible(false);

        }else
        {
            screenToGoBack = "rental.fxml";

            assignButton.setVisible(false);

            createNewCustButton.setVisible(false);
        }

        if(COController.getSelectedRental() == null && COController.getSelectedReservation() == null)
        {
            assignButton.setVisible(true);

            createNewCustButton.setVisible(true);

            saveButton.setVisible(false);

            screenToGoBack = "createRes.fxml";

            setDisableCustomerFileds(true);

        }else
        {
            loadSelectedCustomer();

        }

        loadCustomers();


        exitOptions.setItems(FXCollections.observableArrayList("Log out", "Exit"));

    }
    // endregion

    public void exitOrLogOut(MouseEvent mouseEvent)
    {
        screen.exitOrLogOut(mouseEvent, exitOptions);

    }
    // region customer's data

    private void loadSelectedCustomer() //Martin
    {
         firstNameTxt.setText(selectedCustomer.getFirstName());
         lastNameTxt.setText(selectedCustomer.getLastName());
         cprTxt.setText(selectedCustomer.getCpr());
         drLicenseTxt.setText(selectedCustomer.getDriverLicense());
         phoneNumTxt.setText(selectedCustomer.getPhoneNum());
         emailTxt.setText(selectedCustomer.getEMail());
         addressTxt.setText(selectedCustomer.getAddress());
         logField.setText(selectedCustomer.getLog());

    }

    public void setDisableCustomerFileds(boolean b) //Martin
    {
        saveNewCustomer.setDisable(b);
        firstNameTxt.setDisable(b);
        lastNameTxt.setDisable(b);
        cprTxt.setDisable(b);
        drLicenseTxt.setDisable(b);
        phoneNumTxt.setDisable(b);
        emailTxt.setDisable(b);
        addressTxt.setDisable(b);
        logField.setDisable(b);
    }

    public void clearCustomerFileds() //Martin
    {

        if(screenToGoBack.equals("createRes.fxml"))
        {
            setDisableCustomerFileds(false);

            saveNewCustomer.setVisible(true);
        }

        firstNameTxt.clear();
        lastNameTxt.clear();
        cprTxt.clear();
        drLicenseTxt.clear();
        phoneNumTxt.clear();
        emailTxt.clear();
        addressTxt.clear();
        logField.clear();
    }

    private void loadCustomers() //Martin
    {

      customerIdClm.setCellValueFactory(new PropertyValueFactory<>("id"));
      firstNameClm.setCellValueFactory(new PropertyValueFactory<>("firstName"));
      lastNameClm.setCellValueFactory(new PropertyValueFactory<>("lastName"));
      emailClm.setCellValueFactory(new PropertyValueFactory<>("eMail"));
      customerCprClm.setCellValueFactory(new PropertyValueFactory<>("cpr"));
      phoneNumClm.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

      ObservableList<Customer> cstms = FXCollections.observableArrayList();
      cstms.addAll(coController.getCustomers());
      customerTableView.setItems(cstms);

    }
     // endregion

    // region search customer and change screens  -Rasmus
    public void setResAndItems (Reservation reservation,
                                Collection<ExtrasLineItem> lineItems) //Martin
    {
        this.reservation = reservation;
        this.lineItems.addAll(lineItems);
    }

    public void goBack(ActionEvent event) throws IOException //Martin
    {
        if(screenToGoBack.equals("createRes.fxml"))
        {
            screen.changeToNewRes(event, reservation, lineItems);

            return;
        }

        screen.change(event, screenToGoBack);
    }

    public void saveCustomer(ActionEvent event) throws IOException //Martin
    {
        coController.updateCustomerInfo(selectedCustomer,firstNameTxt,lastNameTxt,cprTxt,drLicenseTxt,phoneNumTxt,emailTxt,addressTxt, logField);

        loadCustomers();

        clearCustomerFileds();

        if(coController.getSelectedCustomer().getId() != selectedCustomer.getId())
        {
            changeOrderCustomer();

            screen.change(event, "orders.fxml");
        }
    }

    private void changeOrderCustomer() //Martin
    {
        String table = "";

        if(screenToGoBack.equals("reservation.fxml"))
        {
            table = "reservations";

        }else
        {
            table = "rentals";
        }

        coController.changeOrderCustomerID(table, selectedCustomer.getId());

        loadCustomers();
    }

    public void searchCustomers(KeyEvent keyEvent) //Martin
    {
        customerIdClm.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameClm.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameClm.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailClm.setCellValueFactory(new PropertyValueFactory<>("eMail"));
        customerCprClm.setCellValueFactory(new PropertyValueFactory<>("cpr"));
        phoneNumClm.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        ObservableList<Customer> cstms = FXCollections.observableArrayList();
        cstms.addAll(coController.searchCustomers(searchField.getText()));
        customerTableView.setItems(cstms);

    }
     // end region



    // region restrict intput
    public void cprRestrict(KeyEvent keyEvent) //Martin
    {

        Screen.restrictIntInput(cprTxt);
        cprTxt.lengthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if (newValue.intValue() > oldValue.intValue())
                {
                     if (cprTxt.getText().length()> LIMIT)
                     {
                        cprTxt.setText(cprTxt.getText().substring(0,LIMIT));
                     }
                }
            }

        });

    }

    public void drLicenseRestrict(KeyEvent keyEvent) //Martin
    {
    Screen.restrictIntInput(drLicenseTxt);
    drLicenseTxt.lengthProperty().addListener(new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (newValue.intValue() > oldValue.intValue())
            {
                if(drLicenseTxt.getText().length()>LIMIT-1)
                {
                    drLicenseTxt.setText(drLicenseTxt.getText().substring(0,LIMIT-1));
                }
            }
        }
    });
    }

    public void phoneRestrict(KeyEvent keyEvent)//Martin
    {
        Screen.restrictIntInput(phoneNumTxt);
        phoneNumTxt.lengthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if (newValue.intValue()> oldValue.intValue())
                {
                    if (phoneNumTxt.getText().length()>LIMIT-2)
                    {
                        phoneNumTxt.setText(phoneNumTxt.getText().substring(0,LIMIT-2));
                    }
                }
            }
        });


    }
    // endregion


    // region create/select customer
    public void createCustomer(ActionEvent event) throws IOException //Martin
    {

        System.out.println("something");

        if (!checkforEmpty())
        {
            Helper.displayError("ERROR",null,"Please fill the required information");
            return ;

        }
         custIDforNewReservation =  coController.createCustomer(passTxt.getText(),firstNameTxt.getText(),lastNameTxt.getText(),addressTxt.getText(),phoneNumTxt.getText(),drLicenseTxt.getText(),emailTxt.getText(),cprTxt.getText(), logField.getText());


        if (screenToGoBack.equals("createRes.fxml"))
        {
            COController.setCreatedCustomerID(custIDforNewReservation);

            screen.changeToNewRes(event, reservation, lineItems);

            return;
        }

        Helper.dispplayConfirmation("Success",null,"Operation has been successful");
        loadCustomers();
        clearCustomerFileds();


    }

    public void selectCustomer(MouseEvent mouseEvent) // Martin
    {

        if(customerTableView.getSelectionModel().getSelectedItem() == null)
        {
            return;
        }

        if(screenToGoBack.equals("createRes.fxml"))
        {
            setDisableCustomerFileds(true);
        }

        selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        loadSelectedCustomer();
    }

    public void assignToNewReservation(ActionEvent event) throws IOException
    {
        custIDforNewReservation = customerTableView.getSelectionModel().getSelectedItem().getId();

        COController.setCreatedCustomerID(custIDforNewReservation);

        if (screenToGoBack.equals("createRes.fxml"))
        {
            System.out.println("order edit");
            System.out.println(reservation);
            screen.changeToNewRes(event, reservation, lineItems);
            return;
        }
        screen.change(event, screenToGoBack);
    }
    // endregion

}
