package uk.ac.ed.inf.pizzadronz.data;

public class Position{
    private Double lng;
    private Double lat;

    public Position(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public Double getLng() { return lng; }
    public Double getLat() { return lat; }
    public void setLng(Double lng) { this.lng = lng; }
    public void setLat(Double lat) { this.lat = lat; }
}
