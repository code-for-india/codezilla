package cfahackathon.codezilla.com.codezilla.objects;

/**
 * Created by ashis_000 on 3/19/2016.
 */
public class HomeObject {

    String location_string;
    double rating, distance, longitude, latitude, location_id;


    public String getLocation_string() {
        return location_string;
    }

    public void setLocation_string(String location_string) {
        this.location_string = location_string;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLocation_id() {
        return location_id;
    }

    public void setLocation_id(double location_id) {
        this.location_id = location_id;
    }
}
