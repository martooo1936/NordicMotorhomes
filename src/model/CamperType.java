package model;

import db.CamperTypeWrapper;

public class CamperType
{
    private CamperTypeWrapper wrapper =
            CamperTypeWrapper.getInstance();

    private int id = -1;
    private String brand;
    private String model;
    private int capacity; // Amount of beds
    private double price; // Rental price/day
    private String description;
    private double deliveryKmPrice;

    public CamperType(int id,
                      String brand,
                      String model,
                      int capacity,
                      double price,
                      String description)
    {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
    }

    public CamperType(String brand,
                      String model,
                      int capacity,
                      double price,
                      String description)
    {
        this.brand = brand;
        this.model = model;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
    }

    public CamperType()
    {
    }

    // Rasmus
    public boolean save()
    {
        if (id == -1)
        {
            return wrapper.saveNew(this) != -1;
        }
        else
        {
            return wrapper.update(this);
        }
    }

    // Rasmus
    public boolean reload ()
    {
        return load(id);
    }

    // Rasmus
    public boolean load (int id)
    {
        CamperType camperType = wrapper.load(id);

        if (camperType == null)
        {
            return false;
        }

        setId(id);
        setBrand(camperType.getBrand());
        setModel(camperType.getModel());
        setCapacity(camperType.getCapacity());
        setPrice(camperType.getPrice());
        setDescription(camperType.getDescription());

        return true;
    }

    // Rasmus
    public boolean delete ()
    {
        return delete(this.id);
    }

    // Rasmus
    public boolean delete (int id)
    {
        this.id = id;

        return wrapper.delete(id);
    }

    //region Getters & setters
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }
    //endregion


    // Rasmus
    @Override
    public String toString()
    {
        return brand + ", " + model;
    }

    public String toStringChoiceBox()
    {
        return this.brand;
    }

    public double getDeliveryKmPrice()
    {
        return deliveryKmPrice;
    }

    public void setDeliveryKmPrice(double deliveryKmPrice)
    {
        this.deliveryKmPrice = deliveryKmPrice;
    }

    public static double getPricePerKm(int typeId)
    {
        double price = CamperTypeWrapper.getDeliveryPrice(typeId);
        return price;
    }
}
