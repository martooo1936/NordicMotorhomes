package db;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.Customer;
import model.Employee;
import model.Person;

import java.sql.*;
import java.util.ArrayList;

//Martin
public class PersonWrapper
{
    private static final String TABLE  = "`nordic_motorhomes`.`persons`";
    private static PersonWrapper personWrapper;
    private Connection conn = null;
    private static final String CUSTABLE = "`nordic_motorhomes`.`customers`";

    public static synchronized PersonWrapper getInstance()
    {
        if(personWrapper == null)
        {
            personWrapper = new PersonWrapper();
        }
        return personWrapper;
    }

    public PersonWrapper()
    {

    }

    /*Returns the signed in person by checking e-mail and pass.
    * It checks also which type of person it is and creates the object out of this type
    * The method is using prepared statement and MD5 build in function*/
    public Person getPerson(String eMail, String pass)
    {

        Person person = null;

        try
        {

            conn = DBCon.getConn();

            String sql = "SELECT * FROM `persons`WHERE `e_mail`= ? AND `pass`= MD5(?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, eMail);

            ps.setString(2, addSalt(pass));

            ps.execute();

            ResultSet rs = ps.getResultSet();

            while (rs.next())
            {
                String status = rs.getString("status");

                if (status.equals("mechanic") || status.equals("assistant") ||
                        status.equals("admin") || status.equals("accountant") || status.equals("cleaner"))
                {
                    Employee employee = new Employee(
                            rs.getString("pass"),
                            rs.getString("driver_license"),
                            rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("address"), rs.getString("cpr"),
                            rs.getString("e_mail"), rs.getString("phone"));
                    employee.setStatus(rs.getString("status"));
                    employee.setId(rs.getInt("id"));
                    employee.setAccNo(rs.getString("account_number"));
                    employee.setRegNr(rs.getString("reg_number"));
                    person = employee;

                }

                if (status.equals("customer"))
                {
                    Customer customer = new Customer(
                            rs.getString("pass"),
                            rs.getString("driver_license"),
                            rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("address"), rs.getString("cpr"),
                            rs.getString("e_mail"), rs.getString("phone"));
                    customer.setStatus(rs.getString("status"));
                    customer.setId(rs.getInt("id"));
                    person = customer;
                }

                return person;
            }

            //conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return person;
    }
    //Martin
     public int saveNewEmployee(Employee employee)//Martin
     {
        conn = DBCon.getConn();
       int personId = -1;

       String sql =  "INSERT INTO " + TABLE + " (" +
               "`first_name`, `last_name`, `address`, `driver_license`,`cpr`,`e_mail`,`phone`,`account_number`,`reg_number`,`pass`,`status`" +
               ") VALUES (" +
               "?, ?, ?, ?,?,?,?,?,?,MD5(?),?" +
               ");";


         try
         {
              PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
              pstmt.setString(1,employee.getFirstName());
              pstmt.setString(2,employee.getLastName());
              pstmt.setString(3,employee.getAddress());
              pstmt.setString(4,employee.getCpr());
              pstmt.setString(5,employee.getDriverLicense());
              pstmt.setString(6,employee.getEMail());
              pstmt.setString(7,employee.getPhoneNum());
              pstmt.setString(8,employee.getAccNo());
              pstmt.setString(9,employee.getRegNr());

              String password = addSalt(employee.getPass());
              pstmt.setString(10, password);

              pstmt.setString(11,employee.getStatus());
              pstmt.execute();
              ResultSet rs = pstmt.getGeneratedKeys();
              if (rs.next()){
                  personId = rs.getInt(1);
              }
              pstmt.close();
         }
         catch (SQLException e){
             e.printStackTrace();

         }
        return  personId;
     }
     public ArrayList<Employee> readEmployee() /*(int employeeID)*/ // Martin
     {
         ArrayList <Employee> employees = new ArrayList<>();

         try
         {

             System.out.println("loading");
             conn = DBCon.getConn();

             String sql = "SELECT * FROM `persons`";

             PreparedStatement ps = conn.prepareStatement(sql);



             ResultSet rs = ps.executeQuery();

             while (rs.next())
             {
                 Employee empployee = new Employee(
                         rs.getString("pass"),
                         rs.getString("first_name"),
                         rs.getString("last_name"), rs.getString("address"),
                         rs.getString("cpr"), rs.getString("driver_license"),
                         rs.getString("e_mail"), rs.getString("phone"));
                 empployee.setAccNo(rs.getString("account_number"));
                 empployee.setRegNr(rs.getString("reg_number"));
                 empployee.setStatus(rs.getString("status"));
                 empployee.setId(rs.getInt("id"));



                 employees.add(empployee);
             }
             ps.close();

         } catch (SQLException e)
         {
             e.printStackTrace();
         }

         return employees;

     }


    // Rasmus
    public String addSalt (String txt)
    {
        final String salt = "6&pjlRTm8K+BqXEa";
        int saltIndex = 0;
        String newTxt = "";

        for (char c : txt.toCharArray())
        {
            newTxt += c + "" + salt.charAt(saltIndex);

            if (saltIndex == salt.length() - 1)
            {
                saltIndex = 0;
            }
            else
            {
                saltIndex++;
            }
        }

        return newTxt;
    }

    public Customer getCustomer(int customerID) // Martin


    {
        Customer customer = null;

        try
        {

            conn = DBCon.getConn();
            String sql = "SELECT * FROM `customers`WHERE `id`= " + customerID;

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.execute();

            ResultSet rs = ps.getResultSet();

            while (rs.next())
            {
                    customer = new Customer(rs.getString("pass"),
                            rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("address"), rs.getString("cpr"),
                            rs.getString("driver_license"),
                            rs.getString("e_mail"), rs.getString("phone"));
                    customer.setDriverLicenseNum(rs.getString("driver_license"));
                    customer.setId(rs.getInt("id"));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return customer;
    }
      //Martin
    public void save(int id, TextField firstName, TextField lastName, TextField cpr, TextField drLicense, ChoiceBox possition, TextField eMail, TextField address, TextField phoneNum, TextField accNo, TextField regNr)
    {

        conn = DBCon.getConn();

        String sql = "UPDATE  `nordic_motorhomes`.`persons` SET  `first_name` =  ?,\n" +
                "`last_name` =  ?,\n" +
                "`address` =  ?,\n" +
                "`cpr` =  ?,\n" +
                "`driver_license` =  ?,\n" +
                "`e_mail` =  ?,\n" +
                "`phone` =  ?,\n" +
                "`account_number` =  ?,\n" +
                "`reg_number` =  ?,\n" +
                "`status` =  ? WHERE  `persons`.`id` =" + id;
        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,firstName.getText());
            ps.setString(2,lastName.getText());
            ps.setString(3,address.getText());
            ps.setString(4,cpr.getText());
            ps.setString(5,drLicense.getText());
            ps.setString(6,eMail.getText());
            ps.setString(7,phoneNum.getText());
            ps.setString(8,accNo.getText());
            ps.setString(9,regNr.getText());
            ps.setString(10,possition.getValue().toString());

            ps.execute();

            ps.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }


    //Martin
    public void updatePassword(int id, String newPass)
    {
        conn = DBCon.getConn();

        String sqlTxt = "UPDATE  `nordic_motorhomes`.`persons` SET  `pass` = MD5(?) WHERE  `persons`.`id` =" + id;


        try
        {
            PreparedStatement prepStmt =
                    conn.prepareStatement(sqlTxt);

            prepStmt.setString(1, newPass);

            prepStmt.executeUpdate();

            prepStmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();

        }

    }
     //Martin
    public void deleteEmployee(int id)
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



        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }


    //Martin
    public ArrayList<Customer> getCustomers()
    {
        ArrayList<Customer> customers = new ArrayList<>();


        try
        {


            conn = DBCon.getConn();

            String sql = "SELECT * FROM `customers`";

            PreparedStatement ps = conn.prepareStatement(sql);



            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                Customer customer = new Customer(
                        rs.getString("pass"),
                        rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("address"),
                        rs.getString("cpr"), rs.getString("driver_license"),
                        rs.getString("e_mail"), rs.getString("phone"));
                customer.setId(rs.getInt("id"));
                customer.setLog(rs.getString("log"));



                customers.add(customer);
                System.out.println(customers);
            }
            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }




        return customers;




    }

    //Martin
    public void updateCustomer(Customer c, TextField firstNameTxt, TextField lastNameTxt, TextField cprTxt, TextField drLicenseTxt, TextField phoneNumTxt, TextField emailTxt, TextField addressTxt, TextField log)
    {

        conn = DBCon.getConn();

        String sql = "UPDATE  `nordic_motorhomes`.`customers` SET  " +
                "`first_name` =  ?,\n" +
                "`last_name` =  ?,\n" +
                "`address` =  ?,\n" +
                "`cpr` =  ?,\n" +
                "`e_mail` =  ?,\n" +
                "`phone` =  ?,\n" +
                "`driver_license` =  ?, " +
                "`log` =  ? " +
                "WHERE  `customers`.`id` =" + c.getId();
        System.out.println(c.getId());

        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,firstNameTxt.getText());
            ps.setString(2,lastNameTxt.getText());
            ps.setString(3,addressTxt.getText());
            ps.setString(4,cprTxt.getText());
            ps.setString(5,emailTxt.getText());
            ps.setString(6,phoneNumTxt.getText());
            ps.setString(7,drLicenseTxt.getText());
            ps.setString(8,log.getText());


            ps.executeUpdate();

            ps.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }


    }


    //Martin
    public int saveNewCustomer(Customer customer)
    {

        conn = DBCon.getConn();
        int customerId = -1;

        String sql =  "INSERT INTO " + CUSTABLE + " (" +
                "`pass`, `first_name`, `last_name`, `address`,`cpr`,`e_mail`,`phone`,`driver_license`,`log`" +
                ") VALUES (" +
                "?, ?, ?, ?,?,?,?,?,?" +
                ");";


        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,customer.getPass());
            pstmt.setString(2,customer.getFirstName());
            pstmt.setString(3,customer.getLastName());
            pstmt.setString(4,customer.getAddress());
            pstmt.setString(5,customer.getPhoneNum());
            pstmt.setString(6,customer.getEMail());
            pstmt.setString(7,customer.getCpr());
            pstmt.setString(8,customer.getDriverLicense());
            pstmt.setString(9,customer.getLog());



            pstmt.execute();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                customerId = rs.getInt(1);
            }
            pstmt.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();

        }
        return  customerId;


    }


    //was used to hash the passwords
//    public void hashPassword()
//    {
//        try
//        {
//
//            conn = DBCon.getConn();
//
//            String sql = "UPDATE  `nordic_motorhomes`.`persons` SET  `pass` =  MD5(?) WHERE  `persons`.`e_mail` =  'jak@yahoo.com'";
//
//            PreparedStatement ps = conn.prepareStatement(sql);
//
//            ps.setString(1, "jakpass");
//
//            ps.executeUpdate();
//
//            conn.close();
//        } catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//
//    }
}
