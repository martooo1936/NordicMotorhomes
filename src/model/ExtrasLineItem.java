package model;

import db.ExtraItemWrapper;

public class ExtrasLineItem
{
    private ExtraItemWrapper exWrapper = ExtraItemWrapper.getInstance();

    private int id;
    private String extraItemName;
    private int extraItemID;
    private int quantity;
    private double subTotal;
    private int orderID;

    public ExtrasLineItem(String extraItemName, int extraItemID)
    {
        this.extraItemName = extraItemName;
        this.extraItemID = extraItemID;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getExtraItemName()
    {
        return extraItemName;
    }

    public void setExtraItemName(String extraItemName)
    {
        this.extraItemName = extraItemName;
    }

    public int getExtraItemID()
    {
        return extraItemID;
    }

    public void setExtraItemID(int extraItemID)
    {
        this.extraItemID = extraItemID;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getSubTotal()
    {
        return subTotal;
    }

    public void setSubTotal(double subTotal)
    {
        this.subTotal = subTotal;
    }

    public int getOrderID()
    {
        return orderID;
    }

    public void setOrderID(int orderID)
    {
        this.orderID = orderID;
    }

    public void save(String state)
    {
        exWrapper.save(this, state);
    }

    public void update(int newValue)
    {
        int newQuantity = this.quantity + newValue;
        exWrapper.updateExtrasLineItem(this.id, newQuantity);
    }

    public void delete()
    {
        exWrapper.deleteExtraLineItem(this.id);
    }

    public void saveAllInfo(boolean isReservation)
    {
        exWrapper.saveAllInfo(this, isReservation);
    }
}
