package com.example.agrefine.model;

public class ProductRecylerModel {
    private String p_name,p_price,p_discription,img_url,documentId,userid;

    public ProductRecylerModel(String p_name, String p_price, String p_discription, String img_url,String documentId, String userid){
        this.p_name = p_name;
        this.p_price = p_price;
        this.p_discription = p_discription;
        this.img_url = img_url;
        this.documentId=documentId;
        this.userid = userid;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getP_name() {
        return p_name;
    }

    public String getP_price() {
        return p_price;
    }

    public String getP_discription() {
        return p_discription;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getUserid() {
        return userid;
    }
}
