package limo.mylimo.SetterGetter;

/**
 * Created by Shoaib Anwar on 13-Feb-18.
 */

public class HistoryData {



    private String order_id;
    private String pickup_lat;
    private String pickup_lng;
    private String pickup_detail;
    private String destination_lat;
    private String destination_lng;
    private String destination_detail;
    private String pickup_city;
    private String destination_city;
    private String car_type;
    private String seats;
    private String date;
    private String time;
    private String price;
    private String user_id;
    private String fullname;
    private String email;
    private String phone;
    private String pincode;
    private String cnic;






    public HistoryData(String order_id, String pickup_lat, String pickup_lng, String pickup_detail, String destination_lat, String destination_lng,
                       String destination_detail, String pickup_city, String destination_city, String car_type, String seats, String date,
                       String time, String price, String user_id, String fullname, String email, String phone, String pincode, String cnic){

        this.order_id = order_id;
        this.pickup_lat = pickup_lat;
        this.pickup_lng = pickup_lng;
        this.pickup_detail = pickup_detail;
        this.destination_lat = destination_lat;
        this.destination_lng = destination_lng;
        this.destination_detail = destination_detail;
        this.pickup_city = pickup_city;
        this.destination_city = destination_city;
        this.car_type = car_type;
        this.seats = seats;
        this.date = date;
        this.time = time;
        this.price = price;
        this.user_id = user_id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.pincode = pincode;
        this.cnic = cnic;


    }

    public String getOrder_id() {
        return order_id;
    }

    public String getPickup_lat() {
        return pickup_lat;
    }

    public String getPickup_lng() {
        return pickup_lng;
    }

    public String getPickup_detail() {
        return pickup_detail;
    }

    public String getDestination_lat() {
        return destination_lat;
    }

    public String getDestination_lng() {
        return destination_lng;
    }

    public String getDestination_detail() {
        return destination_detail;
    }

    public String getPickup_city() {
        return pickup_city;
    }

    public String getDestination_city() {
        return destination_city;
    }

    public String getCar_type() {
        return car_type;
    }

    public String getSeats() {
        return seats;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPrice() {
        return price;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPincode() {
        return pincode;
    }

    public String getCnic() {
        return cnic;
    }


}
