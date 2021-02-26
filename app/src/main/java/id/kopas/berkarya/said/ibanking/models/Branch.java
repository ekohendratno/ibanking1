package id.kopas.berkarya.said.ibanking.models;


import androidx.annotation.Keep;


import java.io.Serializable;

@Keep
public class Branch implements Serializable {
    public String branch;
    public String nama;
    public String link_logo_bank;
    public String status_view;
    public String status_izin;
    public String link_api;

    public Branch(){}

    public Branch(String branch, String nama, String link_logo_bank, String status_view, String status_izin, String link_api){
        this.branch = branch;
        this.nama = nama;
        this.link_logo_bank = link_logo_bank;
        this.status_view = status_view;
        this.status_izin = status_izin;
        this.link_api = link_api;
    }

}
