package ca.mcgill.ecse321.rideshare9.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Journey implements Parcelable {

    private long id;
    private int availableSeats;
    private int vehicleId;
    private int driverId;
    private String title;
    private String startTime;
    private String startLocation;
    private String status;
    private List<Stop> stops;

    public static final Parcelable.Creator<Journey> CREATOR = new Parcelable.Creator<Journey>() {

        @Override
        public Journey createFromParcel(Parcel source) {
            return new Journey(source);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };

    public Journey() {
    }

    @SuppressWarnings("unchecked")
    public Journey(Parcel parcel) {
        this.id = parcel.readLong();
        this.availableSeats = parcel.readInt();
        this.vehicleId = parcel.readInt();
        this.driverId = parcel.readInt();
        this.title = parcel.readString();
        this.startTime = parcel.readString();
        this.startLocation = parcel.readString();
        this.status = parcel.readString();
        this.stops = new ArrayList<>();
        this.stops = parcel.readArrayList(Stop.class.getClassLoader());
    }

    public Journey(int id, int availableSeats, int vehicleId, int driverId, String title,
                   String startTime, String startLocation, String status, List<Stop> stops) {
        this.id = id;
        this.availableSeats = availableSeats;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.title = title;
        this.startTime = startTime;
        this.startLocation = startLocation;
        this.status = status;
        this.stops = stops;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addStop(Stop stop) {
        stops.add(stop);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeInt(getAvailableSeats());
        dest.writeInt(getVehicleId());
        dest.writeInt(getDriverId());
        dest.writeString(getTitle());
        dest.writeString(getStartTime());
        dest.writeString(getStartLocation());
        dest.writeString(getStatus());
        dest.writeList(getStops());
    }
}
