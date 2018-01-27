package com.example.abdelhaf.weather.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abdelhaf on 1/23/2018.
 */

public class WeatherModel implements Serializable {


    String cod;
    double message;
    double cnt;
    public ArrayList<list> list;
    public city city;

    public class list implements Serializable {

        public double dt;
        public main main;
        public ArrayList<weather> weather;
        public clouds clouds;
        public wind wind;
        public rain rain;
        public sys sys;
        public String dt_txt;


        public class main implements Serializable {
            public double temp;
            public double temp_min;
            public double temp_max;
            public double pressure;
            public double sea_level;
            public double grnd_level;
            public double humidity;
            public double temp_kf;


        }
        public class weather implements Serializable {
            public int id;
            public String main;
            public String description;
            public String icon;


        }
        public class clouds implements Serializable {
            public double all;

        }
        public class wind implements Serializable {
            public double speed;
            public double deg;

        }

        public class rain implements Serializable {


        }
        public class sys implements Serializable {
            public String pod;


        }


    }



    public class city implements Serializable {
        public double id;
        public String name;
        public coord coord;
        public String country;
        public double population;

        public class coord implements Serializable {
            public double lat;
            public double lon;

        }
    }



}
