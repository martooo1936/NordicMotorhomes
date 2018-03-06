package db;

import model.ExtraItem;
import model.ExtrasLineItem;
import model.Order;

import java.sql.*;
import java.util.ArrayList;

public class ExtraItemWrapper
{
    private static final String TABLE = "`nordic_motorhomes`.`extras`";
    private static ExtraItemWrapper thisWrapper;
    private Connection conn = null;

    public static synchronized ExtraItemWrapper getInstance()
    {
        if(thisWrapper == null)
        {
            thisWrapper = new ExtraItemWrapper();
        }
        return thisWrapper;
    }

    private ExtraItemWrapper()
    {
    }




    // Rasmus
    public int saveNew(ExtraItem item)
    {
        conn = DBCon.getConn();

        int newId = -1;

        String sqlTxt = "INSERT INTO " + TABLE + " (" +
                "`name`, `price`" +
                ") VALUES (" +
                "?, ?" +
                ");";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt, Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, item.getName());
            prepStmt.setDouble(2, item.getPrice());

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
    public ExtraItem load(int id)
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

            String name = rs.getString("name");
            double price = rs.getDouble("price");

            prepStmt.close();

            return new ExtraItem(id, name, price);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Rasmus
    public boolean update(ExtraItem item)
    {
        conn = DBCon.getConn();

        String sqlTxt = "UPDATE " + TABLE + " SET " +
                "`name` = ?," +
                "`price` = ?" +
                " WHERE `id` = '" + item.getId() + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, item.getName());
            prepStmt.setDouble(2, item.getPrice());

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

    public ArrayList<ExtrasLineItem> getExtrasLineItems(int id, String state) {
        conn = DBCon.getConn();
        ArrayList<ExtrasLineItem> lineItems = new ArrayList<>();
        String sql = "";

        if (state.equals("rental")) {
            sql = "SELECT extras_line_item.id, extras.name " +
                    "AS item_name, extras_line_item.item_id, extras_line_item.quantity, " +
                    "SUM(extras.price*extras_line_item.quantity) AS subtotal\n" +
                    "FROM extras_line_item, extras\n" +
                    "WHERE extras_line_item.rental_id = "+ id +"" +
                    " AND extras.id = extras_line_item.item_id\n" +
                    "GROUP BY extras_line_item.id";
        }
        if(state.equals("reservation")) {
            sql = "SELECT extras_line_item.id, extras.name " +
                    "AS item_name, extras_line_item.item_id, extras_line_item.quantity, " +
                    "SUM(extras.price*extras_line_item.quantity) AS subtotal\n" +
                    "FROM extras_line_item, extras\n" +
                    "WHERE extras_line_item.reserv_id = "+ id +" " +
                    "AND extras.id = extras_line_item.item_id\n" +
                    "GROUP BY extras_line_item.id";
        }
        try {
            PreparedStatement prepStmt = conn.prepareStatement(sql);
            ResultSet rs = prepStmt.executeQuery();

            while (rs.next()) {
                ExtrasLineItem extrasLineItem = new ExtrasLineItem(
                        rs.getString("item_name"),
                        rs.getInt(3));
                extrasLineItem.setId(rs.getInt(1));
                extrasLineItem.setQuantity( rs.getInt(4));
                extrasLineItem.setSubTotal(rs.getDouble("subtotal"));
                lineItems.add(extrasLineItem);
            }
            prepStmt.close();
            return lineItems;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveAllInfo(ExtrasLineItem lineItem, boolean isReservation)
    {
        String sqlTxt = "";

        if(isReservation)
        {
            sqlTxt = "" +
                    "INSERT INTO `nordic_motorhomes`.`extras_line_item` (`id`, `item_id`, `rental_id`, `reserv_id`, `quantity`) " +
                    "VALUES (NULL, '"+ lineItem.getExtraItemID() +"', '0', '"+ lineItem.getOrderID() +"', '" + lineItem.getQuantity() + "');";
        }
        else
        {
            sqlTxt = "" +
                    "INSERT INTO `nordic_motorhomes`.`extras_line_item` (`id`, `item_id`, `rental_id`, `reserv_id`, `quantity`) " +
                    "VALUES (NULL, '"+ lineItem.getExtraItemID() +"', '"+ lineItem.getOrderID() +"', '0', '" + lineItem.getQuantity() + "');";
        }

        conn = DBCon.getConn();

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Rasmus
    public void save(ExtrasLineItem extrasLineItem, String order)
    {
        String sqlTxt = "";

        if(order.equals("rental"))
        {
            sqlTxt = "" +
                    "INSERT INTO `nordic_motorhomes`.`extras_line_item` (`id`, `item_id`, `rental_id`, `reserv_id`, `quantity`) " +
                    "VALUES (NULL, '"+ extrasLineItem.getExtraItemID() +"', '"+ extrasLineItem.getOrderID() +"', '0', '1');";
        }
        else
        {
            sqlTxt = "" +
                    "INSERT INTO `nordic_motorhomes`.`extras_line_item` (`id`, `item_id`, `rental_id`, `reserv_id`, `quantity`) " +
                    "VALUES (NULL, '"+ extrasLineItem.getExtraItemID() +"', '0', '"+ extrasLineItem.getOrderID() +"', '1');";
        }

        conn = DBCon.getConn();

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateExtrasLineItem(int id, int newQuantity)
    {
        conn = DBCon.getConn();

        String sqlTxt = "" +
                "UPDATE  `nordic_motorhomes`.`extras_line_item` " +
                "SET  `quantity` =  '"+ newQuantity +"' WHERE  `extras_line_item`.`id` = " + id;

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteExtraLineItem(int id)
    {
        conn = DBCon.getConn();

        String sqlTxt = "DELETE FROM `extras_line_item` WHERE `id` = '" + id + "';";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.execute();

            prepStmt.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteExtraLineItems(int rentalID)
    {
        conn = DBCon.getConn();

        String sqlTxt = "DELETE FROM `extras_line_item` WHERE `rental_id` = ?;";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setInt(1, rentalID);

            prepStmt.execute();

            prepStmt.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
