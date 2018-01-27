package com.example.abdelhaf.weather.domain.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by abdelhaf on 1/25/2018.
 */

public class CountriesModel implements Serializable {



    public ArrayList<Countries> countries;

    public class Countries implements Serializable {


        public ArrayList<String> cities;


//        public class city implements Serializable {
//            public String name;
//
//
//
//        }}
    }
}
