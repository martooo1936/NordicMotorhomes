package model;

public abstract class Person {
    protected int id;
    private String pass;
    private String firstName;
    private String lastName;
    private String address;
    private String cpr;
    private String driverLicense;
    private String eMail;
    private String phoneNum;
    private String status;



    public Person( String pass,String firstName, String lastName, String address, String cpr, String driverLicense, String eMail, String phoneNum)
    {

       this.pass = pass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.cpr = cpr;
        this.eMail = eMail;
        this.phoneNum = phoneNum;
        this.driverLicense = driverLicense;
    }

    public Person() {

    }



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCpr()
    {
        return cpr;
    }

    public void setCpr(String cpr)
    {
        this.cpr = cpr;
    }

    public String getEMail()
    {
        return eMail;
    }

    public void seteMail(String eMail)
    {
        this.eMail = eMail;
    }

    public String getPhoneNum()
    {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum)
    {
        this.phoneNum = phoneNum;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }
}
