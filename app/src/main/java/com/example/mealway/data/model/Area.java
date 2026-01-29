package com.example.mealway.data.model;

import com.google.gson.annotations.SerializedName;

public class Area {
    @SerializedName("strArea")
    private String strArea;

    public Area() {}

    public Area(String strArea) {
        this.strArea = strArea;
    }

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getFlagUrl() {
        return "https://www.themealdb.com/images/icons/flags/big/64/" + getCountryCode(strArea) + ".png";
    }

    private String getCountryCode(String areaName) {
        if (areaName == null) return "unknown";

        switch (areaName) {
            case "American": return "us";
            case "British": return "gb";
            case "Algerian": return "dz";
            case "Argentinian": return "ar";
            case "Australian": return "au";
            case "Canadian": return "ca";
            case "Chinese": return "cn";
            case "Croatian": return "hr";
            case "Dutch": return "nl";
            case "Egyptian": return "eg";
            case "Filipino": return "ph";
            case "French": return "fr";
            case "Greek": return "gr";
            case "Indian": return "in";
            case "Irish": return "ie";
            case "Italian": return "it";
            case "Jamaican": return "jm";
            case "Japanese": return "jp";
            case "Kenyan": return "ke";
            case "Malaysian": return "my";
            case "Mexican": return "mx";
            case "Moroccan": return "ma";
            case "Norwegian": return "no";
            case "Polish": return "pl";
            case "Portuguese": return "pt";
            case "Russian": return "ru";
            case "Saudi Arabian": return "sa";
            case "Slovakian": return "sk";
            case "Spanish": return "es";
            case "Syrian": return "sy";
            case "Thai": return "th";
            case "Tunisian": return "tn";
            case "Turkish": return "tr";
            case "Ukrainian": return "ua";
            case "Uruguayan": return "uy";
            case "Vietnamese": return "vn";
            default: return "unknown";
        }
    }
}
