package uk.ac.ed.inf.pizzadronz.data;

public class LngLat {
    private Double lng;
    private Double lat;

    public LngLat(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
}
