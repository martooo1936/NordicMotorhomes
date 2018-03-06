package model;

import db.DepotWrapper;

import java.sql.Date;

public class Invoice
{
    private DepotWrapper depotWrapper = DepotWrapper.getInstance();

    private int id;
    private int resID;
    private String text;
    private java.sql.Date date;
    private String paid;


    public Invoice(int resID, String text, Date date)
    {
        this.resID = resID;
        this.text = text;
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getResID()
    {
        return resID;
    }

    public void setResID(int rentalID)
    {
        this.resID = rentalID;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void save()
    {
        depotWrapper.saveInvoice(this);
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getPaid()
    {
        return paid;
    }

    public void setPaid(String paid)
    {
        this.paid = paid;
    }

    @Override
    public String toString()
    {
        return "Invoice nr.: " + id + " \t" + date;
    }

    public void update()
    {
        depotWrapper.updateInvoice(this);
    }
}
