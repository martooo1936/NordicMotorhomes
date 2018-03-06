package model;

import db.CamperWrapper;
import db.ServiceWrapper;

public class Service
{
    private ServiceWrapper wrapper = ServiceWrapper.getInstance();
    private CamperWrapper camperWrapper = CamperWrapper.getInstance();

    private int id = -1;
    private int camperId;
    private int rentalId;
    private String camperPlate;

    private double kmCount;

    private boolean kmChecked = false;
    private boolean enoughGas = false;
    private boolean repairDone = false;
    private double repairCost = 0;
    private boolean cleaned = false;

    public Service()
    {

    }

    // this is for when updating th service. camperid and rentalid will not be changed
    public Service(int id, double kmCount, boolean kmChecked,
                   boolean enoughGas, boolean repairDone, double repairCost, boolean cleaned)
    {
        this.id = id;
        this.kmCount = kmCount;
        this.kmChecked = kmChecked;
        this.enoughGas = enoughGas;
        this.repairDone = repairDone;
        this.repairCost = repairCost;
        this.cleaned = cleaned;
    }

    public Service(int id, int camperId, int rentalId, String camperPlate,
                   double kmCount, boolean kmChecked,
                   boolean enoughGas, boolean repairDone, double repairCost,
                   boolean cleaned)
    {
        this.id = id;
        this.camperId = camperId;
        this.rentalId = rentalId;
        this.camperPlate = camperPlate;
        this.kmCount = kmCount;
        this.kmChecked = kmChecked;
        this.enoughGas = enoughGas;
        this.repairDone = repairDone;
        this.repairCost = repairCost;
        this.cleaned = cleaned;
    }

    // Rasmus
    public boolean markDone()
    {
        if (!isReady())
        {
            return false;
        }

        if (camperWrapper.saveStatusAndKm(camperId, "available", kmCount))
        {
            wrapper.delete(id);
            return true;
        }
        return false;
    }

    // Rasmus
    public boolean saveNew(Camper camper, int rentalId)
    {
        setCamperId(camper.getId());
        setRentalId(rentalId);
        setKmCount(camper.getKmCount());

        if (camperWrapper.saveStatusAndKm(camperId, "not available", kmCount))
        {
            wrapper.saveNew(this);
            return true;
        }
        return false;
    }

    // Rasmus
    public boolean update()
    {
        return wrapper.update(this);
    }

    // Rasmus
    public boolean load (int id)
    {
        Service service = wrapper.load(id);

        if (service == null)
        {
            return false;
        }

        setId(id);
        setCamperId(service.getCamperId());
        setRentalId(service.getRentalId());
        setCamperPlate(service.getCamperPlate());

        setKmCount(service.getKmCount());

        setKmChecked(service.getKmChecked());
        setEnoughGas(service.getEnoughGas());
        setRepairDone(service.getRepairDone());
        setRepairCost(service.getRepairCost());
        setCleaned(service.getCleaned());

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

    public int getCamperId()
    {
        return camperId;
    }

    public void setCamperId(int camperId)
    {
        this.camperId = camperId;
    }

    public int getRentalId()
    {
        return rentalId;
    }

    public void setRentalId(int rentalId)
    {
        this.rentalId = rentalId;
    }

    public String getCamperPlate()
    {
        return camperPlate;
    }

    public void setCamperPlate(String camperPlate)
    {
        this.camperPlate = camperPlate;
    }

    public double getKmCount()
    {
        return kmCount;
    }

    public void setKmCount(double kmCount)
    {
        this.kmCount = kmCount;
    }

    public boolean isReady()
    {
        if (kmChecked && cleaned)
        {
            return true;
        }
        return false;
    }

    public boolean getKmChecked()
    {
        return kmChecked;
    }

    public void setKmChecked(boolean kmChecked)
    {
        this.kmChecked = kmChecked;
    }

    public boolean getEnoughGas()
    {
        return enoughGas;
    }

    public void setEnoughGas(boolean enoughGas)
    {
        this.enoughGas = enoughGas;
    }

    public boolean getRepairDone()
    {
        return repairDone;
    }

    public void setRepairDone(boolean repairDone)
    {
        this.repairDone = repairDone;
    }

    public double getRepairCost()
    {
        return repairCost;
    }

    public void setRepairCost(double repairCost)
    {
        this.repairCost = repairCost;
    }

    public boolean getCleaned()
    {
        return cleaned;
    }

    public void setCleaned(boolean cleaned)
    {
        this.cleaned = cleaned;
    }

    public String getMechStatus()
    {
        if (kmChecked && repairDone)
        {
            return "done";
        }
        return "not done";
    }

    public String getCleanStatus()
    {
        if (cleaned)
        {
            return "done";
        }
        return "not done";
    }
    //endregion
}
