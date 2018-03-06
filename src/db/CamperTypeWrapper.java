package db;

import model.CamperType;

import java.sql.*;

public class CamperTypeWrapper
{
    private static final String TABLE = "`nordic_motorhomes`.`rvs_type`";
    private static CamperTypeWrapper thisWrapper;
    private Connection conn = null;

    public static synchronized CamperTypeWrapper getInstance()
    {
        if(thisWrapper == null)
        {
            thisWrapper = new CamperTypeWrapper();
        }
        return thisWrapper;
    }

    private CamperTypeWrapper()
    {
    }

    // Rasmus
    public int saveNew(CamperType type)
    {
        conn = DBCon.getConn();

        int newId = -1;

        String sqlTxt = "INSERT INTO " + TABLE + " (" +
                "`brand`, `model`, `capacity`, `base_price`, `description`, `km_price`" +
                ") VALUES (" +
                "?, ?, ?, ?, ?, ?" +
                ");";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt, Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, type.getBrand());
            prepStmt.setString(2, type.getModel());
            prepStmt.setInt(3, type.getCapacity());
            prepStmt.setDouble(4, type.getPrice());
            prepStmt.setString(5, type.getDescription());
            prepStmt.setDouble(6, type.getDeliveryKmPrice());

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
    public CamperType load(int id)
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

            String brand = rs.getString("brand");
            String model = rs.getString("model");
            int capacity = rs.getInt("capacity");
            double price = rs.getDouble("base_price");
            String descr = rs.getString("description");
            double kmPrice = rs.getDouble("km_price");

            prepStmt.close();

            return new CamperType(id, brand, model, capacity, price, descr);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Rasmus
    public boolean update(CamperType type)
    {
        conn = DBCon.getConn();

        String sqlTxt = "UPDATE " + TABLE + " SET " +
                "`brand` = ?," +
                "`model` = ?," +
                "`capacity` = ?," +
                "`base_price` = ?," +
                "`description` = ?," +
                "`km_price` = ?" +
                " WHERE `id` = " + type.getId() + ";";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, type.getBrand());
            prepStmt.setString(2, type.getModel());
            prepStmt.setInt(3, type.getCapacity());
            prepStmt.setDouble(4, type.getPrice());
            prepStmt.setString(5, type.getDescription());
            prepStmt.setDouble(6, type.getDeliveryKmPrice());

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


    public static double getDeliveryPrice(int typeId)
    {
        double price = 0;

        Connection conn = DBCon.getConn();
        String sql = "SELECT km_price FROM rvs_type WHERE id = ? ;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, typeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                price = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return price;
    }
}
