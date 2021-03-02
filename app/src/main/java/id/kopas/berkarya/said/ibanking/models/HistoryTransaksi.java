package id.kopas.berkarya.said.ibanking.models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class HistoryTransaksi implements Serializable {
    public String tanggal;
    public String ket_transaksi;
    public String debet;
    public String kredit;
    public String saldo;
    public String id_user;

    public HistoryTransaksi(){}

    public HistoryTransaksi(String tanggal, String ket_transaksi, String debet, String kredit, String saldo, String id_user){
        this.tanggal = tanggal;
        this.ket_transaksi = ket_transaksi;
        this.debet = debet;
        this.kredit = kredit;
        this.saldo = saldo;
        this.id_user = id_user;
    }

}


