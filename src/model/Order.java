package model;

import db.DepotWrapper;
import db.ExtraItemWrapper;

import java.sql.Date;
import java.util.ArrayList;

public abstract class Order
{
    private ExtraItemWrapper exWrapper = ExtraItemWrapper.getInstance();
    private DepotWrapper depot = DepotWrapper.getInstance();

    private int id;
    private Date startDate;
    private Date endDate;
    private String startLocation;
    private String endLocation;
    private int assistantID;

    public Order()
    {

    }

    public Order(int id, Date startDate, Date endDate, String startLocation, String endLocation, int assistantID)
    {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.assistantID = assistantID;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getStartLocation()
    {
        return startLocation;
    }

    public void setStartLocation(String startLocation)
    {
        this.startLocation = startLocation;
    }

    public String getEndLocation()
    {
        return endLocation;
    }

    public void setEndLocation(String endLocation)
    {
        this.endLocation = endLocation;
    }

    public int getAssistantID()
    {
        return assistantID;
    }

    public void setAssistantID(int assistantID)
    {
        this.assistantID = assistantID;
    }


    public ArrayList<ExtrasLineItem> getExtrasLineItems(int id, String state)
    {
        return exWrapper.getExtrasLineItems(id,state);
    }

    public void updateCustomerID(String table, int customerId) {
        depot.updateCustomerID(this, table, customerId);
    }
}
