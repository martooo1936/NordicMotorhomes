package db;

import model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DepotWrapper
{
    private static DepotWrapper depotWrapper;

    private DepotWrapper() {}

    private Connection conn = DBCon.getConn();



    public static synchronized DepotWrapper getInstance()
    {
        if (depotWrapper==null)
        {
            depotWrapper = new DepotWrapper();
        }
        return depotWrapper;
    }

    // Rasmus
    public ArrayList<Service> getServices()
    {
        ArrayList<Service> services = new ArrayList<>();

        try
        {
            String sql =
                    "SELECT " +
                            "service.id, service.camper_id, service.rental_id, rvs.plate, " +
                            "service.km_count, " +
                            "service.km_checked, service.enough_gas, " +
                            "service.repair_done, service.repair_cost, service.cleaned " +

                            "FROM service, rvs " +

                            "WHERE service.camper_id = rvs.id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                int id = rs.getInt("id");
                int camperId = rs.getInt("camper_id");
                int rentalId = rs.getInt("rental_id");
                String camperPlate = rs.getString("plate");
                double kmCount = rs.getDouble("km_count");
                boolean kmChecked = rs.getInt("km_checked") != 0;
                boolean enoughGas = rs.getInt("enough_gas") != 0;
                boolean noRepair = rs.getInt("repair_done") != 0;
                double repairCost = rs.getDouble("repair_cost");
                boolean cleaned = rs.getInt("cleaned") != 0;

                services.add(new Service(id, camperId, rentalId, camperPlate,
                        kmCount, kmChecked, enoughGas, noRepair, repairCost, cleaned));
            }

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return services;
    }

    //Martin
    public ArrayList<Employee> getEmployee(){
        ArrayList <Employee> employees = new ArrayList<>();
        try
        {
            String sql = "SELECT * FROM `nordic_motorhomes`.`persons`";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                employees.add(
                        new Employee(

                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("address"),
                                rs.getString("cpr"),
                                rs.getString("driver_license"),
                                rs.getString("e_mail"),
                                rs.getString("phone"),
                                rs.getString("pass")


                        )
                );
            }
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return employees;
    }


    public ArrayList<CamperType> getMotorhomeTypes()
    {
        ArrayList<CamperType> list = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM `rvs_type`";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                CamperType type = new CamperType(rs.getInt("id"), rs.getString("brand"),
                        rs.getString("model"), rs.getInt("capacity"),
                        rs.getDouble("base_price"), rs.getString("description"));
                type.setDeliveryKmPrice(rs.getDouble("km_price"));
                list.add(type);
            }
            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Camper> getCampers()
    {
        ArrayList<Camper> campers = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM `nordic_motorhomes`.`rvs`";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                campers.add(
                        new Camper(
                        rs.getInt("id"),
                        rs.getInt("rv_type"),
                        rs.getString("plate"),
                        rs.getString("status"),
                        rs.getDouble("km_count")
                        )
                );
            }
            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return campers;
    }
    //Nikolay
    public ArrayList<Reservation> getReservations()
    {
        ArrayList<Reservation> reservations = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM reservations";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Reservation r = new Reservation(
                        rs.getInt("id"), rs.getDate("start_date"), rs.getDate("end_date"),
                                              rs.getString("start_location"), rs.getString("end_location"),
                                               rs.getInt("assistant_id"), rs.getDate("creation_date"),
                        rs.getString("state"), rs.getDouble("estimated_price"),
                        rs.getDouble("extra_km_start"), rs.getDouble("extra_km_end"));
                r.setRvTypeID(rs.getInt("rv_type"));
                r.setCustomerID(rs.getInt("customer_id"));

                System.out.println(r.getExtraKmStart());
                System.out.println(r.getExtraKmEnd());
                System.out.println();

                reservations.add(r);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }


        return reservations;
    }

    public ArrayList<Camper> getAvailableCampers()
    {

        ArrayList<Camper> availableCampers = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM rvs WHERE status = 'available'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                availableCampers.add
                        (new Camper(
                        rs.getInt("id"), rs.getInt("rv_type"),
                        rs.getString("plate"), rs.getString("status"),
                        rs.getDouble("km_count"))
                        );

            }

            ps.close();

            return availableCampers;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }



        return null;
    }
    //Nikolay
    public ArrayList<Rental> getRentals()
    {
        ArrayList<Rental> rentals = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM rentals";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Rental r = new Rental(
                        rs.getInt("id"), rs.getDate("start_date"),
                        rs.getDate("end_date"), rs.getString("start_location"),
                        rs.getString("end_location"), rs.getInt("assistant_id"));

                r.setReservPrice(rs.getDouble("reserv_price"));
                r.setContract(rs.getString("contract"));
                r.setExtraKilometers(rs.getDouble("extra_km"));
                r.setGasFee(rs.getDouble("gas_fee"));
                r.setDamagedPrice(rs.getDouble("damaged_price"));
                r.setReservID(rs.getInt("reserv_id"));
                r.setRv_id(rs.getInt("rv_id"));
                r.setCustomer_id(rs.getInt("customer_id"));
                r.setExtraKmStart(rs.getDouble("extra_km_start"));
                r.setExtraKmEnd(rs.getDouble("extra_km_end"));

                rentals.add(r);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return rentals;
    }

    public Rental loadRental(int rentalId)
    {

        try
        {
            String sql = "SELECT * FROM rentals" +
                    " WHERE `id` = '" + rentalId + "'";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                Rental r = new Rental(
                        rs.getInt("id"), rs.getDate("start_date"),
                        rs.getDate("end_date"), rs.getString("start_location"),
                        rs.getString("end_location"), rs.getInt("assistant_id"));

                r.setReservPrice(rs.getDouble("reserv_price"));
                r.setContract(rs.getString("contract"));
                r.setExtraKilometers(rs.getDouble("extra_km"));
                r.setGasFee(rs.getDouble("gas_fee"));
                r.setDamagedPrice(rs.getDouble("damaged_price"));
                r.setReservID(rs.getInt("reserv_id"));
                r.setRv_id(rs.getInt("rv_id"));
                r.setCustomer_id(rs.getInt("customer_id"));
                r.setExtraKmStart(rs.getDouble("extra_km_start"));
                r.setExtraKmEnd(rs.getDouble("extra_km_end"));
                ps.close();
                return r;
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<ExtraItem> getExtras()
    {
        ArrayList<ExtraItem> list = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM `extras`";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                list.add(new ExtraItem(rs.getInt("id"),rs.getString("name"),rs.getDouble("price")));
            }
            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return list;
    }
    //Nikolay
    public void createRental(Rental rental)
    {

        String sql = "INSERT INTO `nordic_motorhomes`.`rentals` (`" +
                "id`, `start_date`, `end_date`, `start_location`, `end_location`, `assistant_id`, " +
                "`reserv_price`, `contract`, `extra_km`, `gas_fee`, `damaged_price`, `reserv_id`, `rv_id`, `customer_id`) " +
                "" +
                "VALUES (NULL, '"+rental.getStartDate()+"', '"+ rental.getEndDate() +"'," +
                " ?, ?, " +
                "?, ?, " +
                " ?, '0', '0', '0', '"+ rental.getReservID() +"', '"+ rental.getRv_id() +"', '"+ rental.getCustomer_id() +"');";

        try
        {

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(1, rental.getStartLocation());
            ps.setString(2, rental.getEndLocation());
            ps.setInt(3, rental.getAssistantID());
            ps.setDouble(4, rental.getReservPrice());
            ps.setString(5, rental.getContract());

            ps.execute();

            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public void saveCamperStatusChanges(int id, String status)
    {
        String sqlTxt = "UPDATE  `nordic_motorhomes`.`rvs` SET  `status` = ? WHERE  `rvs`.`id` = " + id;

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, status);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void saveReservationStateChanges(int id, String state)
    {
        String sqlTxt = "UPDATE  `nordic_motorhomes`.`reservations` SET  `state` = ? WHERE  `reservations`.`id` =" + id;

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, state);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Nikolay
    public void deleteRental(int id)
    {
        String sql = "DELETE FROM rentals WHERE id = " + id;

        try
        {
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Nikolay
    public ArrayList<Rental> getRentalsBySearchText(String text)
    {
        ArrayList<Rental> rentals = new ArrayList<>();

        String sql = "SELECT * FROM rentals WHERE id LIKE ? OR start_date LIKE ? OR end_date LIKE ? OR start_location LIKE ? OR reserv_id LIKE ?;";

        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "%"+text+"%");
            ps.setString(2, "%"+text+"%");
            ps.setString(3, "%"+text+"%");
            ps.setString(4, "%"+text+"%");
            ps.setString(5, "%"+text+"%");

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Rental r = new Rental(
                        rs.getInt("id"), rs.getDate("start_date"),
                        rs.getDate("end_date"), rs.getString("start_location"),
                        rs.getString("end_location"), rs.getInt("assistant_id"));

                r.setReservPrice(rs.getDouble("reserv_price"));
                r.setContract(rs.getString("contract"));
                r.setExtraKilometers(rs.getDouble("extra_km"));
                r.setGasFee(rs.getDouble("gas_fee"));
                r.setDamagedPrice(rs.getDouble("damaged_price"));
                r.setReservID(rs.getInt("reserv_id"));
                r.setRv_id(rs.getInt("rv_id"));
                r.setCustomer_id(rs.getInt("customer_id"));
                r.setExtraKmStart(rs.getDouble("extra_km_start"));
                r.setExtraKmEnd(rs.getDouble("extra_km_end"));
                rentals.add(r);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return rentals;
    }
    //Nikolay
    public ArrayList<Reservation> getReservationsBySearchText(String text)
    {
        ArrayList<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT * FROM reservations WHERE id LIKE ? OR start_date LIKE ? OR end_date LIKE ? OR start_location LIKE ?;";

        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "%"+text+"%");
            ps.setString(2, "%"+text+"%");
            ps.setString(3, "%"+text+"%");
            ps.setString(4, "%"+text+"%");

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Reservation r = new Reservation(
                        rs.getInt("id"), rs.getDate("start_date"), rs.getDate("end_date"),
                        rs.getString("start_location"), rs.getString("end_location"),
                        rs.getInt("assistant_id"), rs.getDate("creation_date"),
                        rs.getString("state"), rs.getDouble("estimated_price"),
                        rs.getDouble("extra_km_start"), rs.getDouble("extra_km_end"));
                r.setRvTypeID(rs.getInt("rv_type"));
                r.setCustomerID(rs.getInt("customer_id"));

                reservations.add(r);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }


        return reservations;
    }

    // Rasmus
    public int saveNewReservation(Reservation reservation)
    {

        conn = DBCon.getConn();

        int newId = -1;

        String sqlTxt =
                "INSERT INTO `reservations` (" +

                "`start_date`, `end_date`, `start_location`, `end_location`, `assistant_id`, " +
                "`creation_date`, `state`, `estimated_price`, `rv_type`, `customer_id`" +

                ") VALUES (" +

                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                ");";
        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt, Statement.RETURN_GENERATED_KEYS);

            prepStmt.setDate(1, reservation.getStartDate());
            prepStmt.setDate(2, reservation.getEndDate());
            prepStmt.setString(3, reservation.getStartLocation());
            prepStmt.setString(4, reservation.getEndLocation());
            System.out.println("assistant id: " + reservation.getAssistantID());
            prepStmt.setInt(5, reservation.getAssistantID());
            prepStmt.setDate(6, reservation.getCreationDate());
            prepStmt.setString(7, reservation.getState());
            prepStmt.setDouble(8, reservation.getEstimatedPrice());
            System.out.println("type id: " + reservation.getRvTypeID());
            prepStmt.setInt(9, reservation.getRvTypeID());
            System.out.println("customer id: " + reservation.getCustomerID());
            prepStmt.setInt(10, reservation.getCustomerID());

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

    //Nikolay
    public void addRecordInDateLogs(int reserv_id, Date startDate, Date endDate, int camperTypeID)
    {
        String sqlText = "" +
                "INSERT INTO `nordic_motorhomes`.`date_logs` " +
                "(`reserv_id`, `start_date`, `end_date`, `rv_type_id`) " +
                "VALUES (?, ?, ?, ?);";

        try
        {

            PreparedStatement ps =
                    conn.prepareStatement(sqlText);

            ps.setInt(1, reserv_id);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            ps.setInt(4, camperTypeID);

            ps.executeUpdate();

            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }


    }
    //Nikolay
    public void updateDateLogs(int reserv_id, Date startDate, Date endDate, int camperTypeID)
    {
        String sqlText = "UPDATE  `nordic_motorhomes`.`date_logs` SET  " +
                "`start_date` =  ?,\n" +
                "`end_date` =  ?,\n" +
                "`rv_type_id` =  ? " +
                "WHERE  `date_logs`.`reserv_id` = " + reserv_id;

        try
        {

            PreparedStatement ps =
                    conn.prepareStatement(sqlText);

            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ps.setInt(3, camperTypeID);

            ps.executeUpdate();

            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }


    }
    //Nikolay
    public void deleteDateLog(int reserv_id)
    {
        String sqlText = "DELETE FROM `nordic_motorhomes`.`date_logs` WHERE `date_logs`.`reserv_id` = ?";

        try
        {

            PreparedStatement ps =
                    conn.prepareStatement(sqlText);

            ps.setInt(1, reserv_id);

            ps.executeUpdate();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //region Old check availability method
    /*public boolean checkAvailability(String selectedType, LocalDate startDate, LocalDate endDate)
    {
        boolean available = false;

        ArrayList<Camper> campers = new ArrayList<>();
        ArrayList<Camper> availableCampers = new ArrayList<>();
        ArrayList<Reservation> reservations = new ArrayList<>();
        int id = 0;
        int countCampers = 0;


        java.sql.Date startingDate = java.sql.Date.valueOf(startDate);
        java.sql.Date endingDate = java.sql.Date.valueOf(endDate);

        //step 1.) get ID from selectedType String

        String sql = "SELECT id FROM rvs_type WHERE brand = ? ;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,selectedType);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //step 2.) select every camper for this id

        sql = "SELECT * FROM rvs WHERE rv_type = ?;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                countCampers++;
            }
            System.out.println("CAMPERS: ");
            for (Camper camper:campers)
            {
                System.out.println(camper.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //counts how many reservations ARE done for requested dates (means how many campers are UNavailable)


        int counter = 0;

        sql = "SELECT * FROM reservations WHERE rv_type = ? AND state != ? AND " +    //DONT CHANGE ANYTHING HERE!!!!
                "((start_date < ? AND end_date > ? AND end_date < ? ) OR " +
                "(start_date > ? AND start_date < ? AND end_date > ? ) OR " +
                "(start_date < ? AND end_date > ? ) OR " +
                "(start_date > ? AND end_date < ? ));";
                try {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, "Cancelled");
                    ps.setDate(3, startingDate);
                    ps.setDate(4, startingDate);
                    ps.setDate(5, endingDate);
                    ps.setDate(6, startingDate);
                    ps.setDate(7, endingDate);
                    ps.setDate(8, endingDate);
                    ps.setDate(9, startingDate);
                    ps.setDate(10, endingDate);
                    ps.setDate(11, startingDate);
                    ps.setDate(12, endingDate);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next())
                    {
                        counter += 1;
                    }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        System.out.println("COUNTER: " + counter);
        System.out.println("COUNT CAMPERS: " + countCampers);
        if (countCampers > counter)
        {
            available = true;
        }
        else
        {
            available = false;
        }
        return available;
    }*/
    //endregion

    // Modified
    public boolean checkAvailability(int typeId, LocalDate startDate, LocalDate endDate)
    {
        boolean available = false;

        ArrayList<Camper> campers = new ArrayList<>();
        ArrayList<Camper> availableCampers = new ArrayList<>();
        ArrayList<Reservation> reservations = new ArrayList<>();
        int countCampers = 0;


        java.sql.Date startingDate = java.sql.Date.valueOf(startDate);
        java.sql.Date endingDate = java.sql.Date.valueOf(endDate);

        //step 1.) get ID from selectedType String



        //step 2.) select every camper for this id

        String sql = "SELECT * FROM rvs WHERE rv_type = ?;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,typeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                countCampers++;
            }
            System.out.println("CAMPERS: ");
            for (Camper camper:campers)
            {
                System.out.println(camper.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //counts how many reservations ARE done for requested dates (means how many campers are UNavailable)


        int counter = 0;

        sql = "SELECT * FROM date_logs WHERE rv_type_id = ? AND " +    //DONT CHANGE ANYTHING HERE!!!!
                "((start_date < ? AND end_date > ? AND end_date < ? ) OR " +
                "(start_date > ? AND start_date < ? AND end_date > ? ) OR " +
                "(start_date < ? AND end_date > ? ) OR " +
                "(start_date > ? AND end_date < ? ));";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, typeId);
            ps.setDate(2, startingDate);
            ps.setDate(3, startingDate);
            ps.setDate(4, endingDate);
            ps.setDate(5, startingDate);
            ps.setDate(6, endingDate);
            ps.setDate(7, endingDate);
            ps.setDate(8, startingDate);
            ps.setDate(9, endingDate);
            ps.setDate(10, startingDate);
            ps.setDate(11, endingDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                counter += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("COUNTER: " + counter);
        System.out.println("COUNT CAMPERS: " + countCampers);
        if (countCampers > counter)
        {
            available = true;
        }
        else
        {
            available = false;
        }
        return available;
    }
    //Nikolay
    public void updateRental(int id, LocalDate endDate,String startLocation, String endLocation, double startKm, double endKm)
    {
        String sqlTxt = "" +
                "UPDATE `nordic_motorhomes`.`rentals` " +
                "SET `end_date` = '"+ endDate +"', `start_location` = ?, " +
                "`end_location` = ?, `extra_km_start` = '"+ startKm +"', `extra_km_end` = '"+ endKm +"' " +
                "WHERE `rentals`.`id` = "+ id +";";

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, startLocation);
            prepStmt.setString(2, endLocation);

            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    //Nikolay
    public ArrayList<Customer> getCustomersByText(String text)
    {
        ArrayList<Customer> selectedCustomers = new ArrayList<>();

        String sql = "SELECT * FROM customers WHERE id LIKE ? OR cpr LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR e_mail LIKE ? OR phone LIKE ?;";

        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "%"+text+"%");
            ps.setString(2, "%"+text+"%");
            ps.setString(3, "%"+text+"%");
            ps.setString(4, "%"+text+"%");
            ps.setString(5, "%"+text+"%");
            ps.setString(6, "%"+text+"%");

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Customer c = new Customer(
                        rs.getString("pass"),
                        rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("address"),
                        rs.getString("cpr"), rs.getString("driver_license"),
                        rs.getString("e_mail"), rs.getString("phone"));
                c.setId(rs.getInt("id"));

                selectedCustomers.add(c);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

       return  selectedCustomers;

    }
    //Nikolay
    public void saveReservChanges(int id, double newEstPrice, LocalDate stDate, LocalDate endDate, String stLocation, String endLocation, double stKm, double endKm)
    {
        String sqlTxt = "UPDATE  `nordic_motorhomes`.`reservations` SET  " +
                "`start_date` = ? ,\n" +
                "`end_date` =  ?,\n" +
                "`start_location` =  ?,\n" +
                "`end_location` =  ?,\n" +
                "`estimated_price` =  ?,\n" +
                "`extra_km_start` =  ?,\n" +
                "`extra_km_end` =  ? " +
                "WHERE  `reservations`.`id` = " + id;

        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setDate(1, java.sql.Date.valueOf(stDate));
            prepStmt.setDate(2, java.sql.Date.valueOf(endDate));
            prepStmt.setString(3, stLocation);
            prepStmt.setString(4, endLocation);
            prepStmt.setDouble(5, newEstPrice);
            prepStmt.setDouble(6, stKm);
            prepStmt.setDouble(7, endKm);


            prepStmt.execute();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }


    }
    //Nikolay
    public void saveInvoice(Invoice invoice)
    {
        String sql = "INSERT INTO `nordic_motorhomes`.`invoices` (`id`, `res_id`, `text`, `date`, `paid`) VALUES (NULL, ?, ?, ?, 'not paid');";

        try
        {

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(1, invoice.getResID());
            ps.setString(2, invoice.getText());
            ps.setDate(3, invoice.getDate());

            ps.executeUpdate();

            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
    //Nikolay
    public ArrayList<Invoice> getInvoices(int resID)
    {
        ArrayList<Invoice> invoices = new ArrayList<>();

        try
        {
            String sql = "SELECT * FROM `invoices` WHERE `res_id` = " + resID;

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Invoice invoice = new Invoice(rs.getInt("res_id"), rs.getString("text"), rs.getDate("date"));

                invoice.setId(rs.getInt("id"));

                invoice.setPaid(rs.getString("paid"));

                invoices.add(invoice);
            }

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return invoices;
    }

    //Nikolay
    public void updateInvoice(Invoice invoice)
    {
        String sqlText = "UPDATE  `nordic_motorhomes`.`invoices` SET  `paid` =  'paid' WHERE  `invoices`.`res_id` = ?";

        try
        {
            PreparedStatement ps =
                    conn.prepareStatement(sqlText);

            ps.setInt(1, invoice.getResID());

            ps.executeUpdate();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //Martin
    public void updateCustomerID(Order order, String table, int customerId)
    {
        conn = DBCon.getConn();

        String sqlTxt = "" +
                "UPDATE  `nordic_motorhomes`.`"+ table +"` " +
                "SET  `customer_id` =  '"+ customerId + "' WHERE  `id` = " + order.getId();
        System.out.println(sqlTxt);
        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.executeUpdate();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}





