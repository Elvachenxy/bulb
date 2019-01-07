package com.bulb.bulb.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceDetailSearchModel implements Serializable {
    @SerializedName("result")
    PlaceDetailResultModel results;

    public class PlaceDetailResultModel implements Serializable {
        @SerializedName("formatted_address")
        String address;

        @SerializedName("formatted_phone_number")
        String phone;

        @SerializedName("website")
        String website;

        @SerializedName("url")
        String url;

        @SerializedName("name")
        String name;

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getWebsite() {
            return website;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }
    }

    public PlaceDetailResultModel getResults() {
        return results;
    }
}
