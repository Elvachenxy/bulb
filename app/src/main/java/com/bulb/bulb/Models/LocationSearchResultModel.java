package com.bulb.bulb.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LocationSearchResultModel implements Serializable {
    @SerializedName("results")
    List<ResultModel> results;

    public static class ResultModel implements Serializable {
        @SerializedName("place_id")
        String placeId;
        @SerializedName("name")
        String name;

        @SerializedName("geometry")
        GeometryModel geometry;

        public static class GeometryModel implements Serializable {
            @SerializedName("location")
            LocationModel location;

            public static class LocationModel implements Serializable {
                @SerializedName("lat")
                String latitude;

                @SerializedName("lng")
                String longitude;

                public String getLatitude() {
                    return latitude;
                }

                public String getLongitude() {
                    return longitude;
                }
            }

            public LocationModel getLocation() {
                return location;
            }
        }

        public GeometryModel getGeometry() {
            return geometry;
        }

        public String getName() {
            return name;
        }

        public String getPlaceId() {
            return placeId;
        }
    }

    public List<ResultModel> getResults() {
        return results;
    }
}
