package com.freddan.wigell_travels.VO;

public class Currency {

    private double sek;
    private double eur;

    public Currency() {
    }

    public Currency(double sek, double eur) {
        this.sek = sek;
        this.eur = eur;
    }

    public double getSek() {
        return sek;
    }

    public void setSek(double sek) {
        this.sek = sek;
    }

    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }
}