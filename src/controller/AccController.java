package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CamperType;
import model.ExtraItem;
import model.Camper;
import model.Depot;

public class AccController
{
    public boolean saveCamperType(int id,
                                  String brand,
                                  String model,
                                  int capacity,
                                  double price,
                                  String description,
                                  double deliveryKmPrice)
    {
        CamperType camperType = new CamperType(
                id, brand, model, capacity, price, description);
        camperType.setDeliveryKmPrice(deliveryKmPrice);

        boolean result = camperType.save();

        return result;
    }

    // Rasmus
    public ObservableList<CamperType> loadCamperTypes()
    {
        Depot depot = new Depot();

        ObservableList<CamperType> types = FXCollections.observableArrayList();
        types.addAll(depot.getMotorhomeTypes());
        return types;
    }

    // Rasmus
    public boolean deleteCamperType(int id)
    {
        CamperType type = new CamperType();
        type.setId(id);

        return type.delete();
    }

    // Rasmus
    public boolean saveCamper(int id, int rvTypeId, String plate,
                              String status, double kmCount)
    {
        Camper camper = new Camper(id, rvTypeId, plate, status, kmCount);

        boolean result = camper.save();

        return result;
    }

    // Rasmus
    public ObservableList<Camper> loadCampers()
    {
        Depot depot = new Depot();

        ObservableList<Camper> campers = FXCollections.observableArrayList();
        campers.addAll(depot.getCampers());
        return campers;
    }

    // Rasmus
    public boolean deleteCamper(int id)
    {
        Camper camper = new Camper();
        camper.setId(id);

        return camper.delete();
    }



    // Rasmus
    public boolean saveExtraItem(int id,
                                  String name,
                                  double price)
    {
        ExtraItem item = new ExtraItem(id, name, price);

        boolean result = item.save();

        return result;
    }

    // Rasmus
    public ObservableList<ExtraItem> loadExtraItems()
    {
        Depot depot = new Depot();

        ObservableList<ExtraItem> items = FXCollections.observableArrayList();
        items.addAll(depot.getExtras());
        return items;
    }

    // Rasmus
    public boolean deleteExtraItem(int id)
    {
        ExtraItem items = new ExtraItem();
        items.setId(id);

        return items.delete();
    }
}
