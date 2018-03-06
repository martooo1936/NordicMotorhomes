package view;

import com.jfoenix.controls.JFXDatePicker;
import controller.COController;
import controller.Helper;
import controller.LoginController;
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
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.ResourceBundle;

import static controller.Helper.doubleClick;
import static controller.Helper.screen;
//most of the methods are made by Jakub
public class CreateResView implements Initializable
{

    @FXML
    private ComboBox<CamperType> chooseRVType;
    @FXML
    private JFXDatePicker startDate;
    @FXML
    private JFXDatePicker endDate;

    @FXML
    private Label availableLabel;
    @FXML
    private Label motorhomePrice;
    @FXML
    private Label extrasPrice;
    @FXML
    private Label deliveryPrice;
    @FXML
    private Label estimatedPrice, redLabel;


    @FXML
    private TableView<ExtraItem> listExtras;
    @FXML
    private TableColumn<String, ExtraItem> item;
    @FXML
    private TableColumn<Double, ExtraItem> price;

    @FXML
    private TableView<ExtrasLineItem> chosenExtras;
    @FXML
    private TableColumn<String, ExtrasLineItem> itemChosen;
    @FXML
    private TableColumn<Integer, ExtrasLineItem> quantityChosen;
    @FXML
    private TableColumn<Double, ExtrasLineItem> priceChosen;

    @FXML
    private TextField startDistance;
    @FXML
    private TextField endDistance;
    @FXML
    private TextField startLocation;
    @FXML
    private TextField endLocation;

    @FXML
    private TextField customerIDField;

    private COController logic = new COController();

    private Reservation reservation = logic.createReservation();

    private ObservableList<ExtraItem> extraItemList = FXCollections.observableArrayList();

    private ObservableList<ExtrasLineItem> lineItemList = FXCollections.observableArrayList();

    private ObservableList<CamperType> camperList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        customerIDField.setText(String.valueOf(COController.getCreatedCustomerID()));

        redLabel.setVisible(false);
        camperList.clear();
        camperList.addAll(logic.getCamperTypes());
        chooseRVType.setItems(camperList);

        item.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));

        itemChosen.setCellValueFactory(new PropertyValueFactory<>("extraItemName"));
        quantityChosen.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceChosen.setCellValueFactory(new PropertyValueFactory<>("subTotal"));

        ObservableList<ExtraItem> extras = FXCollections.observableArrayList();
        extras.addAll(logic.getExtras());
        listExtras.setItems(extras);

        Screen.restrictNumberInput(startDistance);
        Screen.restrictNumberInput(endDistance);

        updateExtrasTables();

        listeners();
    }

    public void updateFields(Reservation reservation, Collection<ExtrasLineItem> lineItems)
    {
        this.reservation = reservation;

        if (lineItems != null)
        {
            this.lineItemList.clear();
            this.lineItemList.addAll(lineItems);
            updateExtrasTables();
        }

        startLocation.setText(reservation.getStartLocation());
        startDistance.setText(reservation.getExtraKmStart() + "");
        endLocation.setText(reservation.getEndLocation());
        endDistance.setText(reservation.getExtraKmEnd() + "");

        calcDeliveryPrice();

        if (reservation.getRvTypeID() > 0)
        {
            CamperType type = null;

            for (CamperType thisType : camperList)
            {
                if (thisType.getId() == reservation.getRvTypeID())
                {
                    type = thisType;
                    break;
                }
            }

            if (type != null)
            {
                chooseRVType.getSelectionModel().select(type);
            }

            if (reservation.getStartDate() != null)
            {
                startDate.setValue(reservation.getStartDate().toLocalDate());
                endDate.setValue(reservation.getEndDate().toLocalDate());
            }

            checkAvailable();
        }
    }

    // Rasmus
    private void updateExtrasTables()
    {
        extraItemList.clear();
        extraItemList.addAll(logic.getExtras());

        listExtras.setItems(extraItemList);
        chosenExtras.setItems(lineItemList);
    }

    // Rasmus
    public void addExtra(MouseEvent mouseEvent)
    {
        ExtraItem item = listExtras.getSelectionModel().getSelectedItem();
        lineItemList = logic.addToExtraLocal(item, lineItemList);
        updateExtrasTables();
        sumOfPrices();
    }

    // Rasmus
    public void subtractExtra(MouseEvent mouseEvent)
    {
        ExtrasLineItem lineItem = chosenExtras.getSelectionModel().getSelectedItem();
        lineItemList = logic.subtractExtraLocal(lineItem, lineItemList, extraItemList);
        updateExtrasTables();

        extrasPrice.setText(logic.calcExtrasPrice(lineItemList) + "");
        sumOfPrices();
    }

    // Rasmus
    private void sumOfPrices()
    {
        extrasPrice.setText(logic.calcExtrasPrice(lineItemList) + "");
        Control[] controls = {motorhomePrice, extrasPrice, deliveryPrice};
        estimatedPrice.setText(Helper.sumOfGUI(controls) + "");
        redLabel.setVisible(false);
    }

    public void checkAvailability(ActionEvent actionEvent)
    {
        checkAvailable();
    }

    private void checkAvailable()
    {
        try
        {
            CamperType camperType = chooseRVType.getSelectionModel().getSelectedItem();

            if (logic.checkAvailability(camperType.getId(), startDate.getValue(), endDate.getValue()))
            {
                availableLabel.setText("Available");

                double price = logic.getCamperPrice(camperType.getId());

                String priceStr = Helper.seasonalPriceChange(startDate.getValue(), endDate.getValue(), price).toString();

                motorhomePrice.setText(priceStr);

                reservation.setRvTypeID(camperType.getId());

                //setReservation();


                CamperType type = chooseRVType.getSelectionModel().getSelectedItem();

                reservation.setRvTypeID(type.getId());

                Date startDateSql = Date.valueOf(startDate.getValue());

                Date endDateSql = Date.valueOf(endDate.getValue());

                reservation.setStartDate(startDateSql);

                reservation.setEndDate(endDateSql);

                calcDeliveryPrice();
            } else
            {
                availableLabel.setText("Unavailable");
            }
        } catch (Exception e)
        {
            //e.printStackTrace();
            screen.warning("Fill in RV type and dates", "You have not filled RV type or dates! Please fill in data again.");
        }
        sumOfPrices();
    }



    public void calcDeliveryPrice()
    {
        Helper helper = new Helper();

        double startKm = helper.doubleFromTxt(startDistance.getText());
        double endKm = helper.doubleFromTxt(endDistance.getText());

        if (reservation.getRvTypeID() < 1)
        {
            return;
        }

        if (startKm < 0 ||endKm < 0 ||
                reservation == null)
        {
            return;
        }

        double totalDelivery = logic.calculateDeliveryPrice(
                startKm, endKm, reservation.getRvTypeID());

        deliveryPrice.setText(totalDelivery + "");

        reservation.setStartLocation(startLocation.getText());
        reservation.setExtraKmStart(startKm);
        reservation.setEndLocation(endLocation.getText());
        reservation.setExtraKmEnd(endKm);

        sumOfPrices();
    }

    public void checkFields(ActionEvent actionEvent)
    {
        calcDeliveryPrice();
    }

    public void cancelBtnAct(ActionEvent actionEvent)
    {
        Screen screen = new Screen();
        try
        {
            screen.change(actionEvent, "orders.fxml");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean setReservation()
    {
        Helper helper = new Helper();

        int customerId = helper.intFromString(customerIDField.getText());

        if(customerId <= 0)
        {
            redLabel.setVisible(true);

            return false;
        }



        redLabel.setVisible(false);

        if (!availableLabel.getText().equals("Available"))
        {
            screen.warning("Incomplete reservation",
                    "Please select an available motorhome");
            return false;
        }

        double price = helper.doubleFromTxt(estimatedPrice.getText());
        if (price <= 0)
        {
            return false;
        }

        Date today = Date.valueOf(LocalDate.now());

        reservation.setState("reservation");
        reservation.setEstimatedPrice(price);
        reservation.setCustomerID(customerId);
        reservation.setCreationDate(today);
        reservation.setAssistantID(LoginController.getPersonId());

        CamperType type = chooseRVType.getSelectionModel().getSelectedItem();
        reservation.setRvTypeID(type.getId());

        return true;
    }

    public void nextBtnAct(ActionEvent actionEvent)
    {
        Screen screen = new Screen();

        screen.changeToCustInfo(actionEvent, reservation, lineItemList);
    }

    public boolean saveNewReservation(ActionEvent event)
    {
        if((startLocation.getText().equals("") || endLocation.getText().equals("")) && ((!startDistance.getText().equals("0.0")) || (!endDistance.getText().equals("0.0"))))
        {
            screen.warning("Fill in the locations", "LOCATIONS");

            return false;
        }
        if (setReservation())
        {
            System.out.println("success");

            reservation.setStartLocation(startLocation.getText());

            reservation.setEndLocation(endLocation.getText());

            logic.saveNewReservation(event, reservation, lineItemList);


            return true;
        }
        else
        {
            System.out.println("wrong");
            return false;
        }
    }

    public void listeners()
    {
        Helper helper = new Helper();



        try
        {
            startDistance.textProperty().addListener((observable, oldValue, newValue) ->
            {
                CamperType type = chooseRVType.getSelectionModel().getSelectedItem();
                reservation.setRvTypeID(type.getId());

                double startKm = helper.doubleFromTxt(startDistance.getText());
                double endKm = helper.doubleFromTxt(endDistance.getText());
                int id = type.getId();

                deliveryPrice.setText(String.valueOf(reservation.listenerControlStart(newValue, endKm, id)));
                setReservation();
                sumOfPrices();

                /*if (reservation.getRvTypeID() < 1)
                {
                    return;
                }

                if (startKm < 0 ||endKm < 0 ||
                        reservation == null)
                {
                    return;
                }*/
            });

            endDistance.textProperty().addListener((observable, oldValue, newValue) ->
            {
                CamperType type = chooseRVType.getSelectionModel().getSelectedItem();
                reservation.setRvTypeID(type.getId());
                double startKm = helper.doubleFromTxt(startDistance.getText());
                double endKm = helper.doubleFromTxt(endDistance.getText());
                int id = type.getId();

                deliveryPrice.setText(String.valueOf(reservation.listenerControlEnd(startKm, newValue, id)));
                setReservation();
                sumOfPrices();

                /*if (reservation.getRvTypeID() < 1)
                {
                    return;
                }

                if (startKm < 0 ||endKm < 0 ||
                        reservation == null)
                {
                    return;
                }*/
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

