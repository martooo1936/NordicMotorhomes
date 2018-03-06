package model;

import db.DepotWrapper;
import db.ExtraItemWrapper;

import java.sql.Date;
import java.time.LocalDate;

public class Rental extends Order
{
    private DepotWrapper depotWrapper = DepotWrapper.getInstance();
    private ExtraItemWrapper exWrapper = ExtraItemWrapper.getInstance();

    private double reservPrice;
    private String contract;
    private double extraKilometers;
    private double gasFee;
    private double damagedPrice;
    private int reservID;
    private int rv_id;
    private int customer_id;
    private  double extraKmStart;
    private  double extraKmEnd;

    public Rental(int id, Date startDate, Date endDate, String startLocation, String endLocation, int assistantID)
    {
        super(id, startDate, endDate, startLocation, endLocation, assistantID);
    }

    public String getContract()
    {
        return contract;
    }

    public void setContract(String contract)
    {
        this.contract = contract;
    }

    public double getExtraKilometers()
    {
        return extraKilometers;
    }

    public void setExtraKilometers(double extraKilometers)
    {
        this.extraKilometers = extraKilometers;
    }

    public double getGasFee()
    {
        return gasFee;
    }

    public void setGasFee(double gasFee)
    {
        this.gasFee = gasFee;
    }

    public double getDamagedPrice()
    {
        return damagedPrice;
    }

    public void setDamagedPrice(double damagedPrice)
    {
        this.damagedPrice = damagedPrice;
    }

    public double getReservPrice()
    {
        return reservPrice;
    }

    public void setReservPrice(double reservPrice)
    {
        this.reservPrice = reservPrice;
    }

    public int getReservID()
    {
        return reservID;
    }

    public void setReservID(int reservID)
    {
        this.reservID = reservID;
    }

    public void save()
    {
        depotWrapper.createRental(this);
    }

    public void delete()
    {
        depotWrapper.deleteRental(this.getId());
        exWrapper.deleteExtraLineItems(this.getId());
    }

    public int getRv_id()
    {
        return rv_id;
    }

    public void setRv_id(int rv_id)
    {
        this.rv_id = rv_id;
    }

    public int getCustomer_id()
    {
        return customer_id;
    }

    public void setCustomer_id(int customer_id)
    {
        this.customer_id = customer_id;
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

    public void update(LocalDate endDate,String startLocation, String endLocation, double startKm, double endKm)
    {
        depotWrapper.updateRental(this.getId(), endDate,startLocation, endLocation, startKm, endKm);
    }
}
