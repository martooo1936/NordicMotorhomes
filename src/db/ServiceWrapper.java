package db;

import model.Service;

import java.sql.*;

public class ServiceWrapper
{

    // All by Rasmus

    private static final String TABLE = "`nordic_motorhomes`.`service`";
    private static ServiceWrapper thisWrapper;
    private Connection conn = null;

    public static synchronized ServiceWrapper getInstance()
    {
        if(thisWrapper == null)
        {
            thisWrapper = new ServiceWrapper();
        }
        return thisWrapper;
    }

    private ServiceWrapper()
    {
    }

    public int saveNew(Service service)
    {
        conn = DBCon.getConn();

        int newId = -1;

        String sqlTxt = "INSERT INTO " + TABLE + " (" +
                "`camper_id`, `rental_id`, `km_count`" +
                ") VALUES (" +
                "?, ?, ?" +
                ");";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt, Statement.RETURN_GENERATED_KEYS);

            prepStmt.setInt(1, service.getCamperId());
            prepStmt.setInt(2, service.getRentalId());
            prepStmt.setDouble(3, service.getKmCount());

            prepStmt.execute();

            ResultSet rs = prepStmt.getGeneratedKeys();

            if (rs.next())
            {
                newId = rs.getInt(1);
            }

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return newId;
    }

    public Service load(int id)
    {
        conn = DBCon.getConn();

        String sqlTxt =
                "SELECT " +
                "service.camper_id, service.rental_id, rvs.plate, service.km_count, " +
                "service.km_checked, service.enough_gas, " +
                "service.repair_done, service.repair_cost, service.cleaned " +

                "FROM service, rvs " +

                "WHERE service.camper_id = rvs.id " +
                "AND service.id = " + id;

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            ResultSet rs = prepStmt.executeQuery();

            if (!rs.next())
            {
                return null;
            }

            int camperId = rs.getInt("camper_id");
            int rentalId = rs.getInt("rental_id");
            String camperPlate = rs.getString("plate");
            double kmCount = rs.getDouble("km_count");
            boolean kmChecked = rs.getInt("km_checked") != 0;
            boolean enoughGas = rs.getInt("enough_gas") != 0;
            boolean noRepair = rs.getInt("repair_done") != 0;
            double repairCost = rs.getDouble("repair_cost");
            boolean cleaned = rs.getInt("cleaned") != 0;

            prepStmt.close();

            return new Service(id, camperId, rentalId, camperPlate, kmCount,
                    kmChecked, enoughGas, noRepair, repairCost, cleaned);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Service service)
    {
        conn = DBCon.getConn();

        String sqlTxt =
                "UPDATE " + TABLE + " SET " +

                "`km_count` = ?," +
                "`km_checked` = ?," +
                "`enough_gas` = ?," +
                "`repair_done` = ?," +
                "`repair_cost` = ?," +
                "`cleaned` = ?" +

                " WHERE `id` = " + service.getId() + ";";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setDouble(1, service.getKmCount());
            prepStmt.setBoolean(2, service.getKmChecked());
            prepStmt.setBoolean(3, service.getEnoughGas());
            prepStmt.setBoolean(4, service.getRepairDone());
            prepStmt.setDouble(5, service.getRepairCost());
            prepStmt.setBoolean(6, service.getCleaned());

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete(int id)
    {
        conn = DBCon.getConn();

        String sqlTxt = "DELETE FROM " + TABLE +
                " WHERE `id` = '" + id + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.execute();

            prepStmt.close();

            return true;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
