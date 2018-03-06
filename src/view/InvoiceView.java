package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import controller.COController;
import controller.Helper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Invoice;
import model.Rental;
import model.Reservation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//most of the methods are made by Nikolay
public class InvoiceView implements Initializable
{
    private Screen screen = new Screen();

    private COController coController = new COController();

    private Invoice selectedInvoice = null;

    private String screenToGoBack = "";

    @FXML
    private TextArea textArea;

    @FXML
    private JFXComboBox comboBox;

    @FXML
    private JFXButton payButton;

    @FXML
    private Label redLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
       setDisable(false);

        Reservation res = COController.getSelectedReservation();

        Rental ren = COController.getSelectedRental();

        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        if(res == null)
        {
            invoices.addAll(coController.getInvoices(ren.getReservID()));

            screenToGoBack = "rental.fxml";

        }else
        {
            invoices.addAll(coController.getInvoices(res.getId()));

            screenToGoBack = "reservation.fxml";
        }

        selectedInvoice = invoices.get(0);

        if(selectedInvoice.getPaid().equals("paid"))
        {
            setDisable(true);
        }

        comboBox.setItems(invoices);

        textArea.setText(selectedInvoice.getText());

        comboBox.valueProperty().addListener(new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                selectedInvoice = (Invoice) comboBox.getSelectionModel().getSelectedItem();

                textArea.setText(selectedInvoice.getText());

                if(selectedInvoice.getPaid().equals("paid"))
                {
                    setDisable(true);
                }
            }
        });
    }

    public void setDisable(boolean b)
    {
        payButton.setDisable(b);

        redLabel.setVisible(b);
    }

    public void goBack(ActionEvent event) throws IOException
    {
       screen.change(event, screenToGoBack);
    }

    public void print(ActionEvent event) throws IOException
    {
        Helper.dispplayConfirmation("Payment validation", null, "The invoice is printed");

        screen.change(event, screenToGoBack);
    }

    public void goToPayment(ActionEvent event) throws IOException
    {
        boolean paymentValidation = coController.validatePayment();

        if(paymentValidation)
        {
            Helper.dispplayConfirmation("Payment validation", null, "The payment has been verified");
        }

        coController.updateInvoice(selectedInvoice);

        screen.change(event, screenToGoBack);
    }

}
