package edu.socialmedia.ridwan.firebaseDb;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String p_name;
    private String p_company_name;
    private String p_company_phone_number;
    private String p_website;
    private String p_manu_date;
    private String p_exp_date;
    private String p_barcode_image;

    public Product(){

    }

    //add this when you done String p_exp_date, and this String id,

    public Product(String p_name, String p_company_name, String p_company_phone_number, String p_website, String p_manu_date, String p_exp_date,  String p_barcode_image) {
        this.setId(id);
        this.setP_name(p_name);
        this.setP_company_name(p_company_name);
        this.setP_company_phone_number(p_company_phone_number);

        this.setP_website(p_website);
        this.setP_manu_date(p_manu_date);
        this.setP_exp_date(p_exp_date);
        this.setP_barcode_image(p_barcode_image);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_company_name() {
        return p_company_name;
    }

    public void setP_company_name(String p_company_name) {
        this.p_company_name = p_company_name;
    }

    public String getP_company_phone_number() {
        return p_company_phone_number;
    }

    public void setP_company_phone_number(String p_company_phone_number) {
        this.p_company_phone_number = p_company_phone_number;
    }

    public String getP_website() {
        return p_website;
    }

    public void setP_website(String p_website) {
        this.p_website = p_website;
    }

    public String getP_manu_date() {
        return p_manu_date;
    }

    public void setP_manu_date(String p_manu_date) {
        this.p_manu_date = p_manu_date;
    }

    public String getP_exp_date() {
        return p_exp_date;
    }

    public void setP_exp_date(String p_exp_date) {
        this.p_exp_date = p_exp_date;
    }

    public String getP_barcode_image() {
        return p_barcode_image;
    }

    public void setP_barcode_image(String p_barcode_image) {
        this.p_barcode_image = p_barcode_image;
    }
}
