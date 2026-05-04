package models;

import java.util.UUID;

public class Customer {
    private int customerId; // for db because we have customerId as primary key
    private String fullName;
    private String mobilePhone;
    private String email;

    public Customer(String fullName,String mobilePhone,String email){
        this.fullName = fullName;
        this.mobilePhone = mobilePhone;
        this.email = email;
    }
    public int getCustomerId(){
        return customerId;
    }
    public String getFullName(){
        return fullName;
    }
    public String getMobilePhone(){
        return mobilePhone;
    }
    public String getEmail(){
        return email;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public void setMobilePhone(String mobilePhone){
        this.mobilePhone = mobilePhone;
    }
    public void setEmail(String email){
        this.email = email;
    }
}
