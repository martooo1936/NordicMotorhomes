package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import controller.COController;
import controller.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
//most of the methods are done by Nikolay
public class ReservationView implements Initializable{

    private COController coController = new COController();

    private Screen screen = new Screen();

    private Reservation selectedReservation;

    private Object timePeriod;

    @FXML
    private TextField reservationIDField, assistantIDField, startLocationField, startKmField,
            endLocationField, endKmField, reservPriceField, extraFeePeriodField,
            extraFeeKmField, extraFeeExtrasField, totalField, camperID, custIdField;

    @FXML
    private JFXTextField possibleLabel;

    @FXML
    private JFXDatePicker startDatePicker, endDatePicker;

    @FXML
    private JFXComboBox<CamperType> typeComboBox;

    @FXML
    private TableView<ExtraItem> extrasTableView;

    @FXML
    private TableColumn<String, ExtraItem> extrasItemColumn;

    @FXML
    private TableColumn<Double, ExtrasLineItem> extrasPriceColumn;

    @FXML
    private TableView<ExtrasLineItem> chosenExtrasTableView;

    @FXML
    private TableColumn<String, ExtrasLineItem> chosenItemsColumn;

    @FXML
    private TableColumn<Integer, ExtrasLineItem> quantityColumn;

    @FXML
    private TableColumn<Double, ExtrasLineItem> subTotalColumn;

    @FXML
    private ChoiceBox exitOptions;

    @FXML
    private Label redLabel;

    @FXML
    private Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        redLabel.setVisible(false);

        setDisable(false);

        selectedReservation = COController.getSelectedReservation();

        if(!selectedReservation.getState().toLowerCase().equals("reservation"))
        {
            startDatePicker.setDisable(true);

            setDisable(true);

        }

        if(selectedReservation.getState().toLowerCase().equals("cancelled"))
        {
            redLabel.setText("CANCELLED !!!");

            redLabel.setVisible(true);

        }

        if(selectedReservation.getState().toLowerCase().equals("rental"))
        {
            redLabel.setText("Became Rental !!!");

            redLabel.setVisible(true);

        }

        timePeriod = COController.getSelectedTimePeriod();

        exitOptions.setItems(FXCollections.observableArrayList("Log out", "Exit"));


        loadData();

        //region table extraItems
        extrasItemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        extrasPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        ObservableList<ExtraItem> extraItems = FXCollections.observableArrayList();

        extraItems.addAll(coController.getExtras());

        extrasTableView.setItems(extraItems);

        loadExtraLineItems();

        coController.calculateExtraLinesItemsTotal(chosenExtrasTableView, extraFeeExtrasField);

        coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);

        calculateAtTheBeginning();
        //endregion
    }

    public void setDisable(boolean b)
    {
        extrasTableView.setDisable(b);
        chosenExtrasTableView.setDisable(b);
        startKmField.setDisable(b);
        endKmField.setDisable(b);
        endDatePicker.setDisable(b);
        startLocationField.setDisable(b);
        endLocationField.setDisable(b);
        saveButton.setDisable(b);
        typeComboBox.setDisable(b);
    }

    private void calculateAtTheBeginning()
    {
        coController.calculateKmAndSetTotal(
            endKmField, extraFeeKmField,
            reservPriceField, extraFeePeriodField, extraFeeExtrasField, totalField);

        coController.calculateKmAndSetTotal(
                startKmField, extraFeeKmField,
                reservPriceField, extraFeePeriodField, extraFeeExtrasField, totalField);
    }

    private void loadData()
    {
        reservationIDField.setText(String.valueOf(selectedReservation.getId()));
        assistantIDField.setText(String.valueOf(selectedReservation.getAssistantID()));
        custIdField.setText(String.valueOf(selectedReservation.getCustomerID()));
        camperID.setText(String.valueOf(selectedReservation.getRvTypeID()));
        startDatePicker.setValue(selectedReservation.getStartDate().toLocalDate());
        endDatePicker.setValue(selectedReservation.getEndDate().toLocalDate());
        startLocationField.setText(selectedReservation.getStartLocation());
        endLocationField.setText(selectedReservation.getEndLocation());
        reservPriceField.setText(String.valueOf(selectedReservation.getEstimatedPrice()));
        startKmField.setText(selectedReservation.getExtraKmStart() + "");
        endKmField.setText(selectedReservation.getExtraKmEnd() + "");

        typeComboBox.getItems().addAll(coController.getCamperTypes());

        typeComboBox.getSelectionModel().select(coController.getCamperTypeByItsID(selectedReservation.getRvTypeID()));


    }

    public void goBack(ActionEvent event) throws IOException
    {
        Rental rental = coController.getRenalByReservID(selectedReservation.getId());

        if(rental == null)
        {
            screen.change(event, "orders.fxml");
            return;
        }

        COController.setSelectedRental(rental);

        COController.setSelectedReservation(null);

        screen.change(event, "rental.fxml");
    }

    public void exitOrLogOut(MouseEvent mouseEvent)
    {
        screen.exitOrLogOut(mouseEvent, exitOptions);
    }


    public void goToCustommer(ActionEvent event) throws IOException
    {
        COController.setSelectedCustomerID(selectedReservation.getCustomerID());

        screen.change(event, "customerdetails.fxml");

    }


    public void addExtraItem(MouseEvent mouseEvent)
    {
        extrasTableView.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {

                if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2)
                {
                    ExtraItem chosenItem =  extrasTableView.getSelectionModel().getSelectedItem();

                    coController.addExtraLineItem(chosenItem, chosenExtrasTableView,
                            selectedReservation.getId(), "reservation");

                    loadExtraLineItems();

                    coController.calculateExtraLinesItemsTotal(chosenExtrasTableView, extraFeeExtrasField);

                    coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);
                }
            }
        });

    }

    private void loadExtraLineItems()
    {
        chosenItemsColumn.setCellValueFactory(new PropertyValueFactory<>("extraItemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subTotalColumn.setCellValueFactory(new PropertyValueFactory<>("subTotal"));


        ObservableList<ExtrasLineItem> lineItems = FXCollections.observableArrayList();
        lineItems.addAll(coController.getExtrasLineItems(selectedReservation.getId(), "reservation"));
        chosenExtrasTableView.setItems(lineItems);
    }

    public void subtractExtraItem(MouseEvent mouseEvent)
    {
        chosenExtrasTableView.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2)
                {

                    ExtrasLineItem chosenExLineItem = chosenExtrasTableView.getSelectionModel().getSelectedItem();

                    coController.subtractExtraLineItemQuantity(chosenExLineItem);

                    loadExtraLineItems();

                    coController.calculateExtraLinesItemsTotal(chosenExtrasTableView, extraFeeExtrasField);

                    coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);

                }
            }
        });

    }

    public void calculateExtraKmFee(KeyEvent keyEvent)
    {

        coController.calculateKmPriceAndTotal(
                startKmField, extraFeeKmField, totalField, extraFeeKmField,
                reservPriceField, extraFeePeriodField, extraFeeExtrasField);

    }

    public void calculateExtraKmFeeEndLocation(KeyEvent keyEvent)
    {
        coController.calculateKmPriceAndTotal(
                endKmField, extraFeeKmField, totalField, extraFeeKmField,
                reservPriceField, extraFeePeriodField, extraFeeExtrasField);

    }

    public void calculateProlongPeriodPrice(ActionEvent event) throws InterruptedException
    {
        disableFieldsAndButton(false);

        startDatePicker.setDisable(false);

        redLabel.setVisible(false);

        int dateChoice = coController.validateEndDateChoice(endDatePicker, redLabel, extraFeePeriodField, "reservation");

        if(dateChoice == 1)
        {
            disableFieldsAndButton(true);

            startDatePicker.setDisable(true);

            return;
        }

        if(dateChoice == 3)
        {
            return;
        }

        int id = selectedReservation.getId();

        boolean dateValidation = coController.calculateProlongPeriodPrice(id, endDatePicker, redLabel, extraFeePeriodField);

        if (!dateValidation)
        {
            coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);
            return;
        }

        if(!coController.checkAvailability(typeComboBox.getValue().getId(), startDatePicker.getValue(), endDatePicker.getValue()))
        {
            redLabel.setText("You can't prolong the period\n       (date - not available)");

            redLabel.setVisible(true);

            extraFeePeriodField.setText("");

            return;
        }


        coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);

        saveReservChanges();

        extraFeePeriodField.setText("");

        updateReservation();

        coController.updateDateLog(selectedReservation.getId(), startDatePicker.getValue(), endDatePicker.getValue(), selectedReservation.getRvTypeID());
    }

    private void disableFieldsAndButton(boolean truOrFalse)
    {
        extrasTableView.setDisable(truOrFalse);
        chosenExtrasTableView.setDisable(truOrFalse);
        startLocationField.setDisable(truOrFalse);
        endLocationField.setDisable(truOrFalse);
        startKmField.setDisable(truOrFalse);
        endKmField.setDisable(truOrFalse);
        saveButton.setDisable(truOrFalse);
    }

    public void changeCamperType(ActionEvent event)
    {
        saveButton.setDisable(false);

        boolean camperIsAvailable = checkCamperTypeAvailable(typeComboBox.getSelectionModel().getSelectedItem().toString());

        if (!camperIsAvailable)
        {

            reservPriceField.setText("");
            extraFeePeriodField.setText("");
            extraFeeKmField.setText("");
            extraFeeExtrasField.setText("");

            saveButton.setDisable(true);

            coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);

            return;
        }

        changeCamperTypeNewPrice();


    }

    private void changeCamperTypeNewPrice()
    {
        CamperType camperType = typeComboBox.getSelectionModel().getSelectedItem();

        String newPrice = Helper.seasonalPriceChange(startDatePicker.getValue(), endDatePicker.getValue(), coController.getCamperPrice(camperType.getId())) + "";

        reservPriceField.setText(newPrice);

        coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);
    }

    private boolean checkCamperTypeAvailable(String type)
    {
        redLabel.setVisible(false);

        if(!coController.checkAvailability(typeComboBox.getValue().getId(), startDatePicker.getValue(), endDatePicker.getValue()))
        {
            redLabel.setText("There are no available campers of this type\n       (for the selected period)");

            redLabel.setVisible(true);

            extraFeePeriodField.setText("");
            return false;
        }

        return true;
    }

    public void calculateProlongStartDate(ActionEvent event)
    {
        disableFieldsAndButton(false);

        endDatePicker.setDisable(false);

        redLabel.setVisible(false);

        boolean dateValidation = coController.calculateChangeStartDate(selectedReservation, startDatePicker, redLabel, extraFeePeriodField);

        if (!dateValidation)
        {
            reservPriceField.setText("");
            extraFeePeriodField.setText("");
            extraFeeKmField.setText("");
            extraFeeExtrasField.setText("");

            disableFieldsAndButton(true);

            endDatePicker.setDisable(true);

            coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);


            coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);
            return;
        }

        if(!coController.checkAvailability(typeComboBox.getValue().getId(), startDatePicker.getValue(), endDatePicker.getValue()))
        {
            redLabel.setText("You can't prolong the period\n       (date - not available)");

            redLabel.setVisible(true);

            extraFeePeriodField.setText("");
        }

        coController.getRentTotal(reservPriceField, extraFeePeriodField, extraFeeKmField, extraFeeExtrasField, totalField);

        saveReservChanges();

        extraFeePeriodField.setText("");

        updateReservation();

        coController.updateDateLog(selectedReservation.getId(),startDatePicker.getValue(), endDatePicker.getValue(), selectedReservation.getRvTypeID());

            return;
    }

    public void updateReservation()
    {
        Reservation updatedReervation = coController.getReservationByID(selectedReservation.getId());

        reservationIDField.setText(String.valueOf(updatedReervation.getId()));
        assistantIDField.setText(String.valueOf(updatedReervation.getAssistantID()));
        custIdField.setText(String.valueOf(updatedReervation.getCustomerID()));
        camperID.setText(String.valueOf(updatedReervation.getRvTypeID()));
        startDatePicker.setValue(updatedReervation.getStartDate().toLocalDate());
        endDatePicker.setValue(updatedReervation.getEndDate().toLocalDate());
        startLocationField.setText(updatedReervation.getStartLocation());
        endLocationField.setText(updatedReervation.getEndLocation());
        reservPriceField.setText(String.valueOf(updatedReervation.getEstimatedPrice()));
    }

    public void saveChanges(ActionEvent event) throws IOException
    {
        saveReservChanges();
        goBack(event);
    }

    private void saveReservChanges()
    {
        double extraProlPrice = 0;

        try
        {
            extraProlPrice = Double.parseDouble(extraFeePeriodField.getText());

        }catch (Exception e)
        {
            extraProlPrice = 0;
        }
        double newEstPrice = Double.parseDouble(reservPriceField.getText()) + extraProlPrice;
        LocalDate stDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String stLocation = startLocationField.getText();
        String endLocation = endLocationField.getText();
        double stKm = Double.parseDouble(startKmField.getText());
        double endKm = Double.parseDouble(endKmField.getText());

        coController.saveReservChanges(selectedReservation, newEstPrice, stDate, endDate, stLocation, endLocation, stKm, endKm);
    }

    public void seeInvoices(ActionEvent event) throws IOException
    {
        redLabel.setVisible(false);

        System.out.println(selectedReservation.getId());

        if(coController.getInvoices(selectedReservation.getId()).isEmpty())
        {
            redLabel.setVisible(false);

            redLabel.setText("The reservation doesn't have any invoices !!!");

            redLabel.setVisible(true);

            return;
        }

        screen.change(event, "invoice.fxml");

    }
}
