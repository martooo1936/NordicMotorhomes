package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import model.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServiceController {

    // All by Rasmus


    private Service service = null;

    public ObservableList<Service> loadServices ()
    {
        Depot depot = new Depot();

        ObservableList<Service> services = FXCollections.observableArrayList();
        services.addAll(depot.getServices());
        return services;
    }

    public boolean updateService(int id, double kmCount,
                                 boolean kmChecked, boolean enoughGas,
                                 boolean noRepair, double repairCost, boolean cleaned)
    {
        service = new Service(id, kmCount, kmChecked,
                enoughGas, noRepair, repairCost, cleaned);

        return service.update();
    }

    public boolean markDone(Service service)
    {
        createServiceInvoice(service);
        return service.markDone();
    }

    public void createServiceInvoice(Service service)
    {
        if (service == null)
        {
            return;
        }

        Camper camper = new Camper();
        camper.load(service.getCamperId());

        Depot depot = new Depot();

        Rental rental = depot.loadRental(service.getRentalId());

        Customer customer = depot.getCustomer(rental.getCustomer_id());

        if (camper == null || customer == null)
        {
            return;
        }

        boolean feesAdded = false;

        String expenses = "";

        double totalPrice = 0;
        double extraKm = service.getKmCount() - camper.getKmCount();

        if (extraKm > 0)
        {
            expenses += " - exceeded kilometers : ********" + extraKm + "********\n";
            totalPrice += extraKm;
            feesAdded = true;
        }

        double repairCost = service.getRepairCost();

        if (service.getRepairCost() > 0)
        {
            expenses += " - repair cost : ****************" + repairCost + "****************\n";
            totalPrice += repairCost;
            feesAdded = true;
        }

        if (!service.getEnoughGas())
        {
            expenses += " - not enough gasoline left : ***70***\n";
            totalPrice += 70;
            feesAdded = true;
        }

        if (!feesAdded)
        {
            return;
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
                "Extra fees have been added to your rental due to extra maintenance costs.\n\n" +
                "Following is an overview of the expenses:\n" +
                "\n" +
                expenses +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\tTotal\t\t***********" + totalPrice * 0.8 + "***********" +
                "\n" +
                "\t\t\t\t\t\tVAT 25%\t\t***********"+ totalPrice * 0.2 +"**********\n" +
                "\n" +
                "\t\t\t\t\t\tTotal\t\t***********" + totalPrice + "***********\n" +
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

        Invoice newInvoice = new Invoice(rental.getReservID(), text, Date.valueOf(LocalDate.now()));

        newInvoice.save();
    }

    public Service getService()
    {
        return service;
    }

    public void setService(Service service)
    {
        this.service = service;
    }
}
