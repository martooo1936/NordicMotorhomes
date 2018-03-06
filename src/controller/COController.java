package controller;

import com.jfoenix.controls.JFXDatePicker;
import com.sun.org.apache.regexp.internal.RE;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import model.*;
import view.Screen;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

import static controller.Helper.screen;

public class COController
{
    private Depot depot = new Depot();
    private Helper helper = new Helper();

    private static Rental selectedRental;
    private static Reservation selectedReservation;
    private static int selectedRentalCustID;
    private static ExtraItem selectedExtra;
    private static Object selectedTimePeriod;
    private static ArrayList<Customer> customers;
    private static int createdCustomerID;
    private Invoice invoicesForComboBox;

    public static void setCreatedCustomerID(int createdCustomerID)
    {
        COController.createdCustomerID = createdCustomerID;
    }

    public static int getCreatedCustomerID()
    {
        return createdCustomerID;
    }

    public ArrayList<Customer> getCustomers()
    {
        return depot.getCustomers();

    }

    public Double getCamperPrice(/*String camperName*/ int typeId)
    {
        CamperType type = new CamperType();
        type.load(typeId);
        return type.getPrice();
        /*double price = 0;
        ObservableList<CamperType> types = FXCollections.observableArrayList();
        types.addAll(depot.getMotorhomeTypes());

        for (CamperType type : getMotorhomeTypes())
        {
            if (camperName.equals(type.getBrand()))
            {
                price = type.getPrice();
            }
        }
        return price;*/
    }

    public ObservableList<CamperType> getMotorhomeTypes()
    {
        ObservableList<CamperType> types = FXCollections.observableArrayList();
        types.addAll(depot.getMotorhomeTypes());
        return types;
    }

    public ArrayList<ExtraItem> getExtras()
    {
        ArrayList<ExtraItem> extras = new ArrayList<>();
        extras = depot.getExtras();
        return extras;
    }

    public ArrayList<Reservation> getReservations(String str)
    {
        ArrayList<Reservation> allReservations = new ArrayList<>();

        allReservations.addAll(depot.getReservations());

        ArrayList<Reservation> allWithoutRental = new ArrayList<>();

        for (Reservation r : allReservations)
        {
            if (!r.getState().equals("rental"))
            {
                allWithoutRental.add(r);
            }
        }

        allReservations = allWithoutRental;

        ArrayList<Reservation> reservationsForPeriod = new ArrayList<>();

        Date today = Date.valueOf(LocalDate.now());

        switch (str)
        {
            case "today":
                for (Reservation r : allReservations)
                {
                    if (r.getStartDate().equals(today))
                    {
                        reservationsForPeriod.add(r);
                    }
                }
                break;

            case "past":
                for (Reservation r : allReservations)
                {
                    if (r.getStartDate().before(today))
                    {
                        reservationsForPeriod.add(r);
                    }
                }
                break;

            case "future":
                for (Reservation r : allReservations)
                {
                    if (r.getStartDate().after(today))
                    {
                        reservationsForPeriod.add(r);
                    }
                }
                break;

            case "all":
                reservationsForPeriod = allReservations;
                break;
        }

        return reservationsForPeriod;

    }

    public ArrayList<Camper> getAvailableCampers(Reservation selectedReservation)
    {
        ArrayList<Camper> allAvailable = new ArrayList<>();
        ArrayList<Camper> availableOfSelectedType = new ArrayList<>();

        allAvailable = depot.getAvailableCampers(/*selectedReservation*/);

        Date yesterday = Date.valueOf(LocalDate.now().minusDays(1));

        for (Camper camper : allAvailable)
        {
            if (camper.getRvTypeID() == selectedReservation.getRvTypeID() && selectedReservation.getStartDate().after(yesterday))
            {
                availableOfSelectedType.add(camper);
            }
        }

        return availableOfSelectedType;
    }

    public ArrayList<Rental> getRentals(String str)
    {

        ArrayList<Rental> allRentals = new ArrayList<>();
        allRentals.addAll(depot.getRentals());

        ArrayList<Rental> rentalsForPeriod = new ArrayList<>();

        Date today = Date.valueOf(LocalDate.now());

        switch (str)
        {
            case "today":
                for (Rental r : allRentals)
                {
                    if (r.getStartDate().equals(today))
                    {
                        rentalsForPeriod.add(r);
                    }
                }
                break;

            case "past":
                for (Rental r : allRentals)
                {
                    if (r.getStartDate().before(today))
                    {
                        rentalsForPeriod.add(r);
                    }
                }
                break;

            case "future":
                for (Rental r : allRentals)
                {
                    if (r.getStartDate().after(today))
                    {
                        rentalsForPeriod.add(r);
                    }
                }
                break;

            case "all":
                rentalsForPeriod = allRentals;
                break;
        }

        return rentalsForPeriod;
    }

    public void createRental(Reservation selectedReservation, Camper selectedCamper)
    {
        Rental newRental = new Rental(
                -1, selectedReservation.getStartDate(), selectedReservation.getEndDate(),
                selectedReservation.getStartLocation(), selectedReservation.getEndLocation(),
                selectedReservation.getAssistantID());
        newRental.setReservID(selectedReservation.getId());
        newRental.setReservPrice(selectedReservation.getEstimatedPrice());
        selectedCamper.setStatus("not available");
        selectedReservation.setState("rental");
        newRental.setContract(generateContract(selectedReservation, selectedCamper));
        newRental.setRv_id(selectedCamper.getId());
        newRental.setCustomer_id(selectedReservation.getCustomerID());
        newRental.save();
    }

    private String generateContract(Reservation selectedReservation, Camper selectedCamper)
    {
        Customer customer = depot.getCustomer(selectedReservation.getCustomerID());

        //region Contract *******************

        String contract = " " +
                "                               Nordic Motorhomes rental\n" +
                "www.nordicMotorHomeRental.com\n" +
                "Phone: 819 2887 E-mail: nordic@motorhomerentals.com\n" +
                "\n" +
                "Renter's Name: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
                "Renter's CPR: " + customer.getCpr() + "\n" +
                "Renter's address: " + customer.getAddress() + "\n" +
                "Renter's phone number: " + customer.getPhoneNum() + "\n" +
                " Drivers License No. " + customer.getDriverLicenseNum() + "\n\n" +

                "Vehicle License Plate No.   " + selectedCamper.getPlate() + "\n" +
                " \n" +
                "Pick Up Date: " + selectedReservation.getStartDate() + " Return Date: " + selectedReservation.getEndDate() + "\n" +
                " \n" +
                "Pick Up address: " + selectedReservation.getStartLocation() + "\n" +
                " \n" +
                "Return address: " + selectedReservation.getStartLocation() + "\n" +
                "\n" +
                "                              Nordic Motorhomes rental\n" +
                "                              (Rental Agreement Form)\n" +
                "\n" +
                "\n" +
                "RV must be returned in the same condition as it was when picked up. Clean and damage free no exceptions! " +
                " Any damage to any RV caused by the renter or third party are to be reported " +
                "to Little Adventures RV Rentals immediately! and repaired at renters expense.\n" +
                "\n" +
                "(Note:) All RV’S ARE (NON SMOKING) IF CIGARETTE SMOKE SMELL IS " +
                "DETECTED UPON RETURN RENTER MAY COST EXTRA FEE \n" +
                "\n" +
                "* There are No Refunds for early returns….\n" +
                "\n" +
                "* Do not transport any items inside the RV trailers other then items included in the RV.\n" +
                "\n" +
                "* Pets are not allowed inside the tent trailers or travel trailers. (No exceptions). We are animal lovers " +
                "ourselves but have to be considerate to other renters or allergies. If animal hair/smell is detected inside the RV, " +
                "renter may pay extra fee. \n" +
                "\n" +
                "* Cleaning If on return of RV or trailer there is dead bugs or outside is dirty or dusty please stop by a carwash " +
                "and wash trailer or a cleaning fee will apply, If using a pressure washer please take care around vents and stickers " +
                "on trailer or RV, There is also a cleaning bucket with cleaning supplies and a broom for cleaning inside of each RV. " +
                "Please be sure to wipe down countertops,floors,fridge,bathroom & shake out rugs.\n" +
                "\n" +
                "* Damage to RV generator or other must be reported to Camper rv rentals immediately! " +
                "So that we can make arrangements for repair and notify the next renter of posable delay. Repairs must not be performed " +
                "by renter unless authorized by Camper rv rentals. \n" +
                "\n" +
                "* If Renting an RV with a bathroom Please make sure to Completely Dump both the Grey water (sink-water) and Black water (sewer) " +
                "Tanks Before returning or a $200. Dumping Fee Will Apply.\n" +
                "\n" +
                "(Please Read and Initial)\n" +
                "\n" +
                "Dale and owners or Nordic Motorhomes rental will not be held responsible for any motor or transmission failures, " +
                "vehicle problems, towing fines or regulations due to towing any of our RVs, travel trailers, utility trailers. If you are unsure " +
                "of towing regulations, equipment or limitations for your vehicle please check your owners manual or check with your dealer. " +
                "It is strongly advised to have a BCAA Plus RV Roadside Assistance Card in case of a possible breakdown of tow vehicle as " +
                "Nordic Motorhomes rental will not be held liable for any towing fees back to Kamloops. " +
                "Please note all property stored in any of our RVs are the responsibility of the renter we are also not responsible " +
                "for any personal property.\n" +
                "\n" +
                "\n" +
                "(Please initial)________\n" +
                "\n" +
                "Liability: (Please Read, sign and date)\n" +
                "Dale owners or Nordic Motorhomes rentals will not be held responsible for any and all liability " +
                "that may occur while any RV is in the custody of the renter. This includes property damage, personal injury, " +
                "death or any third party liability claims. (Generator or RV at your own risk)\n" +
                "\n" +
                "I, the renter _________________________  release Nordic Motorhomes rental or owners Dale " +
                "of any and all claims of liability or third party liability that may arise from me renting" +
                "Generator, RV or any other items used or rented form Nordic Motorhomes rental.\n" +
                "\n" +
                "\n" +
                "Renter Signature  ___________________________ Date:______________\n" +
                "\n";
//endregion ***************************

        return contract;
    }

    public void deleteRental(Rental selectedRental)
    {
        depot.setCamperStatus(selectedRental.getRv_id());
        depot.setReservationStatus(selectedRental.getReservID());
        selectedRental.delete();
    }

    public ArrayList<Rental> searchRentals(String text)
    {
        return depot.serchRentals(text);
    }

    public ArrayList<Reservation> searchReservations(String text)
    {
        return depot.searchReservations(text);
    }

    public String getCamperBrandAndModel(int rv_id)
    {
        CamperType t = getCamperType(rv_id);

        return t.getBrand();
    }

    public CamperType getCamperType(int rv_id)
    {
        int rvTypeID = 0;

        CamperType type = null;

        ArrayList<Camper> campers = new ArrayList<>();
        campers.addAll(depot.getCampers());

        ArrayList<CamperType> types = new ArrayList<>();
        types.addAll(depot.getMotorhomeTypes());

        for (Camper camper : campers)
        {
            if (camper.getId() == rv_id)
            {
                rvTypeID = camper.getRvTypeID();
            }
        }

        for (CamperType t : types)
        {
            if (t.getId() == rvTypeID)
            {
                type = t;
            }
        }

        return type;
    }

    /*Calculating fee for prolong period as it is the name
    * finds the reservation by id in order to use its start and end date
    * it sets the extra fee in the field*/
    public boolean calculateProlongPeriodPrice(int reservationID, JFXDatePicker datePicker, Label redLabel, TextField extraFeePeriodField)
    {
        redLabel.setVisible(false);

        Reservation reservation = getReservation(reservationID);

        LocalDate newEndDate = datePicker.getValue();

        LocalDate resEndDate = reservation.getEndDate().toLocalDate();

        //count how many days the period will be prolonged
        int days = (int) ChronoUnit.DAYS.between(resEndDate, newEndDate);

        CamperType type = null;

        ArrayList<CamperType> campTypes = depot.getMotorhomeTypes();

        for (CamperType camperType : campTypes)
        {
            if (camperType.getId() == reservation.getRvTypeID())
            {
                type = camperType;
            }
        }

        double extraProlongPeriodfee = days * type.getPrice();

        extraFeePeriodField.setText("" + extraProlongPeriodfee);

        redLabel.setVisible(false);

        return true;

    }

    public boolean calculateChangeStartDate(Reservation reservation, JFXDatePicker datePicker, Label redLabel, TextField extraFeePeriodField)
    {
        redLabel.setVisible(false);

        LocalDate newStartDate = datePicker.getValue();

        LocalDate resEndDate = reservation.getEndDate().toLocalDate();

        LocalDate resStartDate = reservation.getStartDate().toLocalDate();

        if (newStartDate.isAfter(resStartDate) && newStartDate.isBefore(resEndDate))
        {
            redLabel.setText("The price for later pick up will be the same !!!");

            redLabel.setVisible(true);

            extraFeePeriodField.setText(null);

            return true;
        }

        if (newStartDate.isBefore(LocalDate.now()))
        {
            redLabel.setText("Invalid date!!! The date can't be before today");

            redLabel.setVisible(true);

            extraFeePeriodField.setText(null);

            return false;
        }

        if (newStartDate.isAfter(resEndDate))
        {
            redLabel.setText(" You have to cancel the reservation\n and create a new one!!!");

            redLabel.setVisible(true);

            extraFeePeriodField.setText(null);

            return false;
        }


        //count hoe many days the period will be prolonged
        int days = (int) ChronoUnit.DAYS.between(newStartDate, resStartDate);

        System.out.println(days);

        CamperType type = null;

        ArrayList<CamperType> campTypes = depot.getMotorhomeTypes();

        for (CamperType camperType : campTypes)
        {
            if (camperType.getId() == reservation.getRvTypeID())
            {
                type = camperType;
            }
        }


        double extraProlongPeriodfee = days * type.getPrice();

        extraFeePeriodField.setText("" + extraProlongPeriodfee);

        redLabel.setVisible(false);

        return true;

    }

    private Reservation getReservation(int reservationID)
    {
        Reservation reservation = null;
        ArrayList<Reservation> reservations = depot.getReservations();

        for (Reservation r : reservations)
        {
            if (r.getId() == reservationID)
            {
                reservation = r;
            }
        }
        return reservation;
    }

    public void getRentTotal(TextField resPriceField, TextField periodFeeField, TextField kmFeeField, TextField extrasFeeField, TextField totalFeeField)
    {
        double resPrice = 0;
        double periodFee = 0;
        double kmFee = 0;
        double extrasFee = 0;


        try
        {
            resPrice = Double.parseDouble(resPriceField.getText());

        } catch (Exception e)
        {

        }

        try
        {
            periodFee = Double.parseDouble(periodFeeField.getText());

        } catch (Exception e)
        {

        }

        try
        {
            kmFee = Double.parseDouble(kmFeeField.getText());

        } catch (Exception e)
        {

        }

        try
        {
            extrasFee = Double.parseDouble(extrasFeeField.getText());

        } catch (Exception e)
        {

        }

        double total = resPrice + periodFee + kmFee + extrasFee;

        totalFeeField.setText("" + total);
    }

    public boolean checkAvailability(int typeId, LocalDate startDate, LocalDate endDate)
    {
        boolean available = false;

        if ((startDate.getDayOfMonth() < LocalDate.now().getDayOfMonth() && startDate.getMonthValue() <= LocalDate.now().getMonthValue() && startDate.getYear() == LocalDate.now().getYear()) || startDate.getYear() < LocalDate.now().getYear())

        {
            screen.warning("Fill in correct dates", "The start date can not be set before today.");
            return available;
        }

        else if (typeId > 0 && startDate != null && endDate != null && startDate.isBefore(endDate))
        {

            endDate = endDate.plusDays(5);     // SAFETY DELAY for repairs and stuff (5th day is available)
            startDate = startDate.minusDays(5); // SAFETY DELAY for repairs and stuff (5th day is available)
            if (depot.getValidCampers(typeId, startDate, endDate))
            {
                available = true;
            }
        } else
        {
            screen.warning("Fill in correct dates", "You have not filled dates or dates are invalid. Please fill in data again.");
        }
            return available;
        }

        public ArrayList<ExtrasLineItem> getExtrasLineItems ( int id, String state)
        {
            if (state.equals("rental"))
            {
                return selectedRental.getExtrasLineItems(id, state);
            } else
            {
                return selectedReservation.getExtrasLineItems(id, state);
            }

        }

    public void addExtraLineItem(ExtraItem chosenItem,
                                 TableView<ExtrasLineItem> extrasLineItemTable,
                                 int orderId,
                                 String state)
    {
        boolean existInTable = false;
        ExtrasLineItem extraLineItemToUpdate = null;

        existLoop:
        for (ExtrasLineItem exLineItem : extrasLineItemTable.getItems())
        {
            if (exLineItem.getExtraItemID() == chosenItem.getId())
            {
                existInTable = true;
                extraLineItemToUpdate = exLineItem;
                break existLoop;
            }
        }

        if (existInTable)
        {
            extraLineItemToUpdate.update(+1);
            return;
        }

        createExtraLineItem(chosenItem, orderId, state);
    }


    private void createExtraLineItem(ExtraItem chosenItem, int orderId, String state)
    {
        ExtrasLineItem extrasLineItem = new ExtrasLineItem(chosenItem.getName(), chosenItem.getId());
        extrasLineItem.setOrderID(orderId);
        extrasLineItem.save(state);
    }


    public void subtractExtraLineItemQuantity(ExtrasLineItem chosenExLineItem)
    {
        if (chosenExLineItem.getQuantity() <= 1)
        {
            chosenExLineItem.delete();

            return;
        }
        chosenExLineItem.update(-1);
    }

    public void calculateExtraLinesItemsTotal(TableView<ExtrasLineItem> extrasLineItemTable, TextField extraFeeExtrasField)
    {
        double sum = 0;

        if (extrasLineItemTable.getItems().isEmpty())
        {
            extraFeeExtrasField.setText("");
            return;
        }

        for (ExtrasLineItem exLineItem : extrasLineItemTable.getItems())
        {
            sum = sum + exLineItem.getSubTotal();
        }

        extraFeeExtrasField.setText("" + sum);
    }

    public double calculateDeliveryPrice(double kmStart, double kmEnd, int typeId)
    {
        double pricePerKm = CamperType.getPricePerKm(typeId);
        double price = pricePerKm * kmStart + pricePerKm * kmEnd;
        return price;
    }

    public void updateRental(Rental selectedRental, String startLocation, String endLocation, LocalDate endDate, double startKm, double endKm)
    {
        selectedRental.update(endDate, startLocation, endLocation, startKm, endKm);
    }


    /*It's calculating the price for changing the location. Both for the start and end location.
   * It restricts the user input.(The user can only type numbers)
   * At the end the total price is calculated correctly even if has been changed a couple of times */
    public void calculateKmPriceAndTotal(TextField editField, TextField extraFeeField,
                                         TextField totalField, TextField extraFeeKmField,
                                         TextField reservPriceField, TextField extraFeePeriodField,
                                         TextField extraFeeExtrasField)
    {

        //restricts the input
        Screen.restrictNumberInput(editField);

        editField.setOnKeyReleased(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                calculateKmAndSetTotal(editField, extraFeeKmField,
                        reservPriceField, extraFeePeriodField, extraFeeExtrasField, totalField);
            }
        });

    }

    private CamperType getCamperType()
    {
        CamperType camperType = null;


        if (selectedRental != null)
        {
            int camperID = selectedRental.getRv_id();

            camperType = getCamperType(camperID);

            return camperType;
        }

        camperType = depot.getCamperType(selectedReservation.getRvTypeID());

        return camperType;
    }

    public void calculateKmAndSetTotal(TextField editField, TextField extraFeeKmField,
                                       TextField reservPriceField,
                                       TextField extraFeePeriodField, TextField extraFeeExtrasField, TextField totalField)
    {

        double kmPrice = getCamperType().getDeliveryKmPrice();
        double oldInputValue = 0;

        try
        {
            oldInputValue = Double.parseDouble(editField.getPromptText());

        } catch (Exception e)
        {
            oldInputValue = 0;
        }

        double feeProlongPeriod = 0;
        double feeExtras = 0;
        double newInputValue = 0;
        double extraFee = 0;

        //It checks is this the first user input
        if (!extraFeeKmField.getText().isEmpty())
        {
            extraFee = Double.parseDouble(extraFeeKmField.getText());
        }

        try
        {
            newInputValue = Double.parseDouble(editField.getText());

        } catch (Exception e)
        {

        }

        double reservPrice = Double.parseDouble(reservPriceField.getText());


        if (oldInputValue == 0)
        {
            extraFee = newInputValue * kmPrice + extraFee;

        }

        if (oldInputValue == newInputValue)
        {
            return;
        }

        if ((oldInputValue < newInputValue || oldInputValue > newInputValue) && oldInputValue != 0)
        {
            double tempValue = newInputValue;
            tempValue = tempValue - oldInputValue;
            extraFee = tempValue * kmPrice + extraFee;
        }

        extraFeeKmField.setText("" + extraFee);

        try
        {
            feeProlongPeriod = Double.parseDouble(extraFeePeriodField.getText());

        } catch (Exception e)
        {

        }

        try
        {
            feeExtras = Double.parseDouble(extraFeeExtrasField.getText());

        } catch (Exception e)
        {

        }

        double totalPrice = reservPrice + feeProlongPeriod + extraFee + feeExtras;

        //sets the total price in the field
        totalField.setText("" + totalPrice);

        //it sets the new value as a prompt text so we can use it for next time as an old value
        editField.setPromptText("" + newInputValue);
    }

    public boolean checkAreFieldsEmpty(TextField startKmField, TextField endKmField, Label redLabel)
    {
        redLabel.setVisible(false);

        if (
                startKmField.getText().isEmpty() ||
                endKmField.getText().isEmpty())
        {
            return false;
        }
        return true;
    }

    // region ************ static methods
    public Customer getSelectedCustomer()
    {
        Customer selectedCustomer = null;

        for (Customer c : depot.getCustomers())
        {
            if (c.getId() == selectedRentalCustID)
            {
                selectedCustomer = c;
            }
        }

        return selectedCustomer;
    }


    public static void setSelectedRental(Rental selected)
    {
        selectedRental = selected;
    }

    public static void setSelectedReservation(Reservation selected)
    {
        selectedReservation = selected;
    }

    public static Rental getSelectedRental()
    {
        return selectedRental;
    }

    public static Reservation getSelectedReservation()
    {
        return selectedReservation;
    }

    public static void setSelectedCustomerID(int id)
    {
        COController.selectedRentalCustID = id;
    }

    public static ExtraItem getSelectedExtra()
    {
        return selectedExtra;
    }

    public static void setSelectedExtra(ExtraItem selectedExtra)
    {
        COController.selectedExtra = selectedExtra;
    }

    public static void setSelectedTimePeriod(Object timePeriod)
    {
        selectedTimePeriod = timePeriod;
    }

    public static Object getSelectedTimePeriod()
    {
        return selectedTimePeriod;
    }

    //endregion

    public ObservableList<ExtrasLineItem> addToExtraLocal(
            ExtraItem item, ObservableList<ExtrasLineItem> lineItems)
    {
        if (item == null)
        {
            return lineItems;
        }

        final int size = lineItems.size();
        final int itemId = item.getId();

        for (int i = 0; i < size; i++)
        {
            ExtrasLineItem lineItem = lineItems.get(i);

            if (lineItem.getExtraItemID() == itemId)
            {
                int quantity = lineItem.getQuantity() + 1;

                lineItem.setQuantity(quantity);
                lineItem.setSubTotal(quantity * item.getPrice());

                /**
                 * The following is a stupid step but
                 * it is necessary for the ObservableList to
                 * recognize that changes have been made.
                 * Gotta love ObservableList...
                 */
                lineItems.set(i, lineItem);

                return lineItems;
            }
        }

        ExtrasLineItem lineItem = new ExtrasLineItem(item.getName(), itemId);

        lineItem.setQuantity(1);
        lineItem.setSubTotal(item.getPrice());

        lineItems.add(lineItem);
        return lineItems;
    }

    public ObservableList<ExtrasLineItem> subtractExtraLocal(
            ExtrasLineItem lineItem,
            ObservableList<ExtrasLineItem> lineItems,
            ObservableList<ExtraItem> items)
    {
        if (lineItem == null)
        {
            return lineItems;
        }

        final int size = lineItems.size();

        for (int i = 0; i < size; i++)
        {
            ExtrasLineItem currentLineItem = lineItems.get(i);

            final int itemId = lineItem.getExtraItemID();

            if (itemId == currentLineItem.getExtraItemID())
            {
                if (currentLineItem.getQuantity() <= 1)
                {
                    lineItems.remove(i);
                } else
                {
                    int quantity = currentLineItem.getQuantity() - 1;
                    currentLineItem.setQuantity(quantity);

                    for (ExtraItem item : items)
                    {
                        if (itemId == item.getId())
                        {
                            currentLineItem.setSubTotal(quantity * item.getPrice());
                        }
                    }

                    /**
                     * The following is a stupid step but
                     * it is necessary for the ObservableList to
                     * recognize that changes have been made.
                     */
                    lineItems.set(i, currentLineItem);
                }
                break;
            }
        }

        return lineItems;
    }

    public double calcExtrasPrice(ObservableList<ExtrasLineItem> lineItems)
    {
        double finalPrice = 0;

        for (ExtrasLineItem lineItem : lineItems)
        {
            finalPrice += lineItem.getSubTotal();
        }

        return finalPrice;
    }

    // this should be called when you know the order id :)
    public void saveExtraLineItems(int orderId, boolean isReservation,
                                   Collection<ExtrasLineItem> lineItems)
    {
        for (ExtrasLineItem lineItem : lineItems)
        {
            lineItem.setOrderID(orderId);
            lineItem.saveAllInfo(isReservation);
        }
    }

    // Rasmus
    public boolean createService (int rentalId)
    {
        Camper camper = new Camper();

        camper.loadFromRental(rentalId);

        Service service = new Service();

        return service.saveNew(camper, rentalId);
    }

    public int saveNewReservation (ActionEvent event,
                                   Reservation reservation,
                                   Collection<ExtrasLineItem> lineItems)
    {
        int resId = reservation.saveNew();

        if (resId < 1)
        {
            screen.warning("not saved", "Reservation could not be saved");
            return resId;
        }

        saveExtraLineItems(resId, true, lineItems);

        screen.warning("succes", "Reservation successfully created");

        try
        {
            screen.change(event, "orders.fxml");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        depot.addRecordToDateLogs(resId, reservation.getStartDate(), reservation.getEndDate().toLocalDate(), reservation.getRvTypeID());

        return resId;
    }

    public Collection<CamperType> getCamperTypes()
    {
        return getMotorhomeTypes();

        /*ArrayList<String> types = new ArrayList<>();

        for (CamperType type : getMotorhomeTypes())
        {
            types.add(type.toStringChoiceBox());
        }
        return types;*/
    }

    public int getCamperTypeByItsID(int camperTypeID)
    {
        ArrayList<CamperType> types = depot.getMotorhomeTypes();

        for (CamperType type : types)
        {
            if (type.getId() == camperTypeID)
            {
                return type.getId();
            }
        }

        return 0;
    }

    public int validateEndDateChoice(JFXDatePicker endDatePicker, Label redLabel, TextField extraFeePeriodField, String order)
    {
        Order orderTemp = null;

        if (order.toLowerCase().equals("rental"))
        {
            orderTemp = selectedRental;

        } else if (order.toLowerCase().equals("reservation"))
        {
            orderTemp = selectedReservation;
        }

        if (endDatePicker.getValue().isBefore(orderTemp.getStartDate().toLocalDate()))
        {
            redLabel.setText("The end date can not be before the start date !!!");

            redLabel.setVisible(true);

            return 1;
        }

        if (endDatePicker.getValue().equals(orderTemp.getEndDate().toLocalDate()))
        {
            redLabel.setVisible(false);

            return 2;
        }

        if (endDatePicker.getValue().isBefore(orderTemp.getEndDate().toLocalDate()))
        {
            redLabel.setText("The price for earlier drop of \n\t will be the same !!!");

            redLabel.setVisible(true);

            return 3;
        }

        return 4;
    }

    public void updateCustomerInfo(Customer selectedCustomer, TextField firstNameTxt, TextField lastNameTxt, TextField cprTxt, TextField drLicenseTxt, TextField phoneNumTxt, TextField emailTxt, TextField addressTxt, TextField log)
    {
        selectedCustomer.saveChanges(selectedCustomer, firstNameTxt, lastNameTxt, cprTxt, drLicenseTxt, phoneNumTxt, emailTxt, addressTxt, log);
    }

    public void changeOrderCustomerID(String table, int customerId)
    {
        Order selectedOrder = null;

        if (table.equals("reservations"))
        {
            selectedOrder = selectedReservation;
        }

        if (table.equals("rentals"))
        {
            selectedOrder = selectedRental;
        }

        selectedOrder.updateCustomerID(table, customerId);


    }
//Martin
    public int createCustomer(String passT, String fNameT, String lNameT, String cprT, String drLicenseT, String phoneT, String emailT, String addressT, String log)
    {
        Customer customer = new Customer(passT, fNameT, lNameT, cprT, drLicenseT, phoneT, emailT, addressT);

        customer.setLog(log);

        return customer.storeCustomer();
    }

    public ArrayList<Customer> searchCustomers(String text)
    {
        return depot.getCustomersByText(text);
    }

    public void saveReservChanges(Reservation selectedReservation, double newEstPrice, LocalDate stDate, LocalDate endDate, String stLocation, String endLocation, double stKm, double endKm)
    {
        selectedReservation.saveReservChanges(newEstPrice, stDate, endDate, stLocation, endLocation, stKm, endKm);
    }

    public Reservation getReservationByID(int id)
    {

        ArrayList<Reservation> reservations = depot.getReservations();

        for (Reservation r: reservations)
        {
            if(r.getId() == id)
            {
                return r;
            }
        }
        return null;
    }

    public boolean cancelReservation(Reservation reservation, Label redLabel)
    {
        redLabel.setVisible(false);

        if(selectedReservation==null)
        {
            redLabel.setVisible(true);

            redLabel.setText("You have not selected any reservation");

            return false;
        }
        else
        {
            reservation.setState("Cancelled");
            return true;
        }
    }

    public void createInvoice(Rental selectedRental, TextField totalField, TextField extraFeePeriodField, TextField extraFeeKmField, TextField extras)
    {
        Customer customer = depot.getCustomer(selectedRental.getCustomer_id());

        Reservation reservation = getReservation(selectedRental.getReservID());

        double feeKm = 0;

        double periodFee = 0;

        double extrasFee = 0;

        try
        {
            feeKm = Double.parseDouble(extraFeeKmField.getText());

        }catch (Exception e)
        {
            feeKm = 0;
        }

        try
        {
            periodFee = Double.parseDouble(extraFeePeriodField.getText());

        }catch (Exception e)
        {
            periodFee = 0;
        }

        try
        {
            extrasFee = Double.parseDouble(extras.getText());

        }catch (Exception e)
        {
            extrasFee = 0;
        }


        //region invoice text
        String text = "****************************************************     Nordic Motor Home Rental – INVOICE     *********************************************************\n" +
                "\n" +
                "Universitetsvej 1, 4000 Roskilde\n" +
                "111-222-333\n" +
                "nordic@motorhome.rental\n" +
                "VAT number: DK 11222277\n" +
                "To: \n" +
                "First name: " + customer.getFirstName() + " \n" +
                "Last name   " + customer.getLastName() + "\n" +
                "CPR :       "+ customer.getCpr() +" \n  " +
                "Phone :     "+ customer.getPhoneNum() +"\n" +
                "Address :   "+ customer.getAddress() +"\n" +
                "\n" +
                "Reservation :\n" +
                " - delivery km at the start : ***" + reservation.getExtraKmStart() + "***\n" +
                " - delivery km at the end :   ***" + reservation.getExtraKmEnd() + "***\n" +
                " - estimated price : ************" + reservation.getEstimatedPrice() +"**************\n" +
                "\n" +
                "Rental Fees: \n" +
                " - for changing the location : *****"+ feeKm +"*****\n" +
                " - for prolonging the period : *****"+ periodFee +"*****\n" +
                " - for added more extras :     *****"+ extrasFee +"***** \n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\tTotal\t\t***********" + Double.parseDouble(totalField.getText()) * 0.8 + "***********" +
                "\n" +
                "\t\t\t\t\t\tVAT 25%\t\t***********"+ Double.parseDouble(totalField.getText()) * 0.2 +"**********\n" +
                "\n" +
                "\t\t\t\t\t\tTotal\t\t***********" + Double.parseDouble(totalField.getText()) + "***********\n" +
                "\n" +
                "Payment terms\n" +
                "Payment within 14 days via money transfer only to the following account:\n" +
                "TO:\n"+
                "Nordic Motor Home Rental\n" +
                "Danske Bank\n" +
                "Account No: 8885555888555888555\n" +
                "Date:     *****"+ LocalDate.now() +"*****"+ "\n" +
                "Due date: *****"+ LocalDate.now().plusWeeks(2) +"*****" + "\n";

        //endregion

        Invoice newInvoice = new Invoice(selectedRental.getReservID(), text, Date.valueOf(LocalDate.now()));

        newInvoice.save();
    }

    public ArrayList<Invoice> getInvoices(int resID)
    {
        return depot.getInvoices(resID);
    }

    public boolean validatePayment()
    {
        return true;
    }

    public Reservation getRservation(int reservID)
    {
        Reservation reservation = null;

        ArrayList<Reservation> reservations = depot.getReservations();

        for(Reservation r: reservations)
        {
            if(r.getId() == reservID)
            {
                reservation = r;
            }
        }

        return  reservation;
    }

    public Rental getRenalByReservID(int id)
    {
        Rental rental = null;

        ArrayList<Rental> rentals = depot.getRentals();

        for (Rental r: rentals)
        {
            if(r.getReservID() == id)
            {
                rental = r;
            }
        }

        return rental;
    }

    public void addRecordToDateLogs(LocalDate endDate, int camperTypeID)
    {
        depot.addRecordToDateLogs(selectedRental.getReservID(), selectedRental.getStartDate(), endDate, camperTypeID);
    }

    public void deleteRecordDateLogs(int reservID)
    {
        depot.deleteRecordDateLogs(reservID);
    }

    public void updateDateLog(int reservID, LocalDate startDate,LocalDate endDate, int camperTypeID)
    {
        depot.updateDateLog(reservID, startDate, endDate, camperTypeID);
    }

    public void createCancelationInvoice(int reservationID)
    {
        String percentage = "";

        Reservation reservation = getReservation(reservationID);

        Customer customer = depot.getCustomer(reservation.getCustomerID());

        double cancellationPrice = 0.0;

        int days = (int)ChronoUnit.DAYS.between(LocalDate.now(), reservation.getStartDate().toLocalDate());

        if(days == 1)
        {
            cancellationPrice = reservation.getEstimatedPrice()*0.95;

            percentage = " " + (95) + "% ";

        }else if(days > 1 && days < 16)
        {
            cancellationPrice = reservation.getEstimatedPrice()*0.80;

            percentage = " " + (80) + "% ";

        }else if(days > 15 && days < 50)
        {
            cancellationPrice = reservation.getEstimatedPrice()*0.50;

            percentage = " " + (50) + "% ";

        }else if(days > 50)
        {
            cancellationPrice = reservation.getEstimatedPrice()*0.20;

            percentage = " " + (20) + "% ";

        }

        if(cancellationPrice < 200)
        {
            cancellationPrice = 200;
        }


        //region invoice text
        String text = "****************************************************     Nordic Motor Home Rental – INVOICE FOR CANCELLATION    *********************************************************\n" +
                "\n" +
                "Universitetsvej 1, 4000 Roskilde\n" +
                "111-222-333\n" +
                "nordic@motorhome.rental\n" +
                "VAT number: DK 11222277\n" +
                "To: \n" +
                "First name: " + customer.getFirstName() + " \n" +
                "Last name   " + customer.getLastName() + "\n" +
                "CPR :       "+ customer.getCpr() +" \n  " +
                "Phone :     "+ customer.getPhoneNum() +"\n" +
                "Address :   "+ customer.getAddress() +"\n" +
                "\n" +
                "Reservation :\n" +
                " - delivery km at the start : ***" + reservation.getExtraKmStart() + "***\n" +
                " - delivery km at the end :   ***" + reservation.getExtraKmEnd() + "***\n" +
                " - estimated price : ************" + reservation.getEstimatedPrice() +"**************\n" +
                "\n" +
                "\t\t\t\t\t\t\n" +
                "*******************************************The reservation was canceled "+ days +" day/days before the start date " +reservation.getStartDate() + "    **********************************************************\n"+
                "\n" +
                "\t\t\t\t\t\tCancelation Fee"+ percentage +"\t\t*********** " + cancellationPrice*0.8 + " ***********" +
                "\n" +
                "\t\t\t\t\t\t          VAT 25%\t\t************* "+ cancellationPrice*0.2 +" **********\n" +
                "\n" +
                "\t\t\t\t\t\t            Total\t\t************* " + cancellationPrice + " ***********\n" +
                "\n" +
                "Payment terms\n" +
                "Payment within 14 days via money transfer only to the following account:\n" +
                "TO:\t  Nordic Motor Home Rental\n" +
                "Danske Bank\n" +
                "Account No: 8885555888555888555\n" +
                "Date:     *****"+ LocalDate.now() +"*****"+ "\n" +
                "Due date: *****"+ LocalDate.now().plusWeeks(2) +"*****" + "\n";

        //endregion

        Invoice newInvoice = new Invoice(reservationID, text, Date.valueOf(LocalDate.now()));

        newInvoice.save();
    }

    public void updateInvoice(Invoice selectedInvoice)
    {
        selectedInvoice.update();
    }

    public Reservation  createReservation ()
    {
        Reservation reservation = new Reservation();
        return reservation;
    }

//    public void setExtraFeeKmField(TextField startKmField, TextField endKmField, TextField extraFeeKmField, int rvTypeID)
//    {
//
//
//    }
}
