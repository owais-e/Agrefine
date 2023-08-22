package com.example.agrefine.model;

public class UserModel {
    String regname,regemail,regmobilenumber;

    public UserModel() {
    }

    public UserModel(String regname, String regemail, String regmobilenumber) {
        this.regname = regname;
        this.regemail = regemail;
        this.regmobilenumber = regmobilenumber;
    }

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname;
    }

    public String getRegemail() {
        return regemail;
    }

    public void setRegemail(String regemail) {
        this.regemail = regemail;
    }


    public String getRegmobilenumber() {
        return regmobilenumber;
    }

    public void setRegmobilenumber(String regmobilenumber) {
        this.regmobilenumber = regmobilenumber;
    }
}
