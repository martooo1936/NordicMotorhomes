package model;

import db.ExtraItemWrapper;

public class ExtraItem
{
    private ExtraItemWrapper wrapper =
            ExtraItemWrapper.getInstance();

    private Integer id;
    private String name;
    private Double price;

    public ExtraItem(Integer id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ExtraItem()
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
        ExtraItem item = wrapper.load(id);

        if (item == null)
        {
            return false;
        }

        setId(id);
        setName(item.getName());
        setPrice(item.getPrice());

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

    //region GETTERS AND SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }



    //endregion
}
