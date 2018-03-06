package db;

import model.Camper;

import java.sql.*;

public class CamperWrapper
{
    private static final String TABLE = "`nordic_motorhomes`.`rvs`";
    private static CamperWrapper thisWrapper;
    private Connection conn = null;

    public static synchronized CamperWrapper getInstance()
    {
        if(thisWrapper == null)
        {
            thisWrapper = new CamperWrapper();
        }
        return thisWrapper;
    }

    private CamperWrapper()
    {
    }

    // Rasmus
    public int saveNew(Camper camper)
    {
        conn = DBCon.getConn();

        int newId = -1;

        String sqlTxt = "INSERT INTO " + TABLE + " (" +
                "`rv_type`, `plate`, `status`, `km_count`" +
                ") VALUES (" +
                "?, ?, ?, ?" +
                ");";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt, Statement.RETURN_GENERATED_KEYS);

            prepStmt.setInt(1, camper.getRvTypeID());
            prepStmt.setString(2, camper.getPlate());
            prepStmt.setString(3, camper.getStatus());
            prepStmt.setDouble(4, camper.getKmCount());

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

    // Rasmus
    public Camper load(int id)
    {
        conn = DBCon.getConn();

        String sqlTxt = "SELECT * FROM " + TABLE +
                " WHERE `id` = '" + id + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            ResultSet rs = prepStmt.executeQuery();

            if (!rs.next())
            {
                return null;
            }

            int typeId = rs.getInt("rv_type");
            String plate = rs.getString("plate");
            String status = rs.getString("status");
            double kmCount = rs.getDouble("km_count");

            prepStmt.close();

            return new Camper(
                    id, typeId, plate, status, kmCount);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Rasmus
    public Camper loadFromRental(int rentalId)
    {
        conn = DBCon.getConn();

        String sqlTxt = "SELECT * FROM " + TABLE +
                " WHERE `id` = (" +
                "SELECT  rv_id FROM `rentals` WHERE `id` = '" + rentalId +
                "')";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            ResultSet rs = prepStmt.executeQuery();

            if (!rs.next())
            {
                return null;
            }

            int id = rs.getInt("id");
            int typeId = rs.getInt("rv_type");
            String plate = rs.getString("plate");
            String status = rs.getString("status");
            double kmCount = rs.getDouble("km_count");

            prepStmt.close();

            return new Camper(
                    id, typeId, plate, status, kmCount);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Rasmus
    public boolean update(Camper camper)
    {
        conn = DBCon.getConn();

        String sqlTxt = "UPDATE " + TABLE + " SET " +
                "`rv_type` = ?," +
                "`plate` = ?," +
                "`status` = ?," +
                "`km_count` = ?" +
                " WHERE `id` = '" + camper.getId() + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setInt(1, camper.getRvTypeID());
            prepStmt.setString(2, camper.getPlate());
            prepStmt.setString(3, camper.getStatus());
            prepStmt.setDouble(4, camper.getKmCount());

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

    // Rasmus
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

    // Rasmus
    public boolean saveStatusAndKm(int id, String status, double kmCount)
    {
        conn = DBCon.getConn();

        String sqlTxt = "UPDATE " + TABLE + " SET " +
                "`status` = ?," +
                "`km_count` = ?" +
                " WHERE `id` = '" + id + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);


            prepStmt.setString(1, status);
            prepStmt.setDouble(2, kmCount);

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
}
