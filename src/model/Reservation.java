package model;

import controller.COController;
import db.DepotWrapper;

import java.sql.Date;
import java.time.LocalDate;

public class Reservation extends Order
{
    private DepotWrapper depotWrapper = DepotWrapper.getInstance();

    private Date creationDate;
    private String state;
    private double estimatedPrice;
    private int rvTypeID;
    private int customerID;
    private double extraKmStart;
    private double extraKmEnd;

    private COController logic = new COController();
    public  Reservation reservation;




    public Reservation()
    {
        super();
    }

    public Reservation(int id, Date startDate, Date endDate, String startLocation, String endLocation,
                       int assistantID, Date creationDate, String state, double estimatedPrice,
                       double extraKmStart, double extraKmEnd)
    {
        super(id, startDate, endDate, startLocation, endLocation, assistantID);
        this.creationDate = creationDate;
        this.state = state;
        this.estimatedPrice = estimatedPrice;
        this.extraKmStart = extraKmStart;
        this.extraKmEnd = extraKmEnd;
    }

    public Reservation (int id, Date startDate, Date endDate, String startLocation, String endLocation, int assistantID)
    {
        super(id, startDate, endDate, startLocation, endLocation, assistantID);
    }

    public void cancelReservation()
    {

    }

    // Rasmus
    public int saveNew()
    {
        return depotWrapper.saveNewReservation(this);
    }

    //region GETTERS AND SETTERS

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        saveStateChanges(state);
    }

    private void saveStateChanges(String state)
    {
        depotWrapper.saveReservationStateChanges(getId(), state);
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public int getRvTypeID()
    {
        return rvTypeID;
    }

    public void setRvTypeID(int rvTypeID)
    {
        this.rvTypeID = rvTypeID;
    }

    public int getCustomerID()
    {
        return customerID;
    }

    public void setCustomerID(int customerID)
    {
        this.customerID = customerID;
    }

    public double getExtraKmStart()
    {
        return extraKmStart;
    }

    public void setExtraKmStart(double extraKmStart)
    {
        this.extraKmStart = extraKmStart;
    }

    public double getExtraKmEnd()
    {
        return extraKmEnd;
    }

    public void setExtraKmEnd(double extraKmEnd)
    {
        this.extraKmEnd = extraKmEnd;
    }

    //endregion

    public String toStringCheck ()
    {
        return this.getId() + ", " + this.getStartDate() + ", " + this.getEndDate();
    }

    public void saveReservChanges(double newEstPrice, LocalDate stDate, LocalDate endDate, String stLocation, String endLocation, double stKm, double endKm)
    {
        depotWrapper.saveReservChanges(this.getId(), newEstPrice, stDate, endDate, stLocation, endLocation, stKm, endKm);
    }

    public double listenerControlStart(String newValue, double endKm, int id)
    {
        if (endKm <= 0)
        {
            endKm = 0;
        }
        if (newValue.equals("") || newValue.equals("0."))
        {
            newValue = "0";

            double pricee = logic.calculateDeliveryPrice(Integer.parseInt(newValue), endKm, id);
            if (pricee < 0)
            {
                return 0.0;

            }
            else
            {
                return pricee;
            }
        }
        else
        {
            double pricee = logic.calculateDeliveryPrice(Integer.parseInt(newValue), endKm, id);
            if (pricee < 0)
            {
                return 0.0;

            }
            else
            {
                return pricee;
            }
        }

    }

    public double listenerControlEnd(double startKm, String newValue, int id)
    {
        if (startKm <= 0)
        {
            startKm = 0;
        }
        if (newValue.equals("") || newValue.equals("0."))
        {
            newValue = "0";

            double pricee = logic.calculateDeliveryPrice(startKm, Integer.parseInt(newValue), id);
            if (pricee < 0)
            {
                return 0.0;

            }
            else
            {
                return pricee;
            }
        }
        else
        {
            double pricee = logic.calculateDeliveryPrice(startKm, Integer.parseInt(newValue), id);
            if (pricee < 0)
            {
                return 0.0;

            }
            else
            {
                return pricee;
            }
        }

    }

}
