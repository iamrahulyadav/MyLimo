package limo.mylimo;

/**
 * Created by Shoaib Anwar on 17-Jan-18.
 */
public class DbHelper {

    String id;
    String pickupaddress;
    String droboffaddress;
    String pickuplatlng;
    String drobofflatlng;
    String pickupdate;
    String pickuptime;
    String estimateddistance;
    String estimatedfare;
    String carType;

    boolean isValueChecked;

    public void setIsValueChecked(boolean isChecked) {
        this.isValueChecked = isChecked;
    }



    public boolean isValueChecked() {
        return isValueChecked;
    }




    public void setId(String id){
        this.id = id;
    }
    public void setPickupaddress(String pickaddress){
        this.pickupaddress = pickaddress;
    }
    public void setDroboffaddress(String droboffaddress){
        this.droboffaddress = droboffaddress;
    }
    public void setPickuplatlng(String pickuplatlng){
        this.pickuplatlng  = pickuplatlng;
    }
    public void setDrobofflatlng(String drobofflatlng){
        this.drobofflatlng = drobofflatlng;
    }
    public void setPickupdate(String date){
        this.pickupdate = date;
    }
    public void setPickuptime(String time){
        this.pickuptime = time;
    }
    public void setEstimateddistance(String estimateddistance){
        this.estimateddistance = estimateddistance;
    }
    public void setEstimatedfare(String estimatedfare){
        this.estimatedfare = estimatedfare;
    }
    public void setCarType(String carType){
        this.carType = carType;
    }



    public String getId(){
        return id;
    }
    public String getPickupaddress(){
        return pickupaddress;
    }
    public String getDroboffaddress(){
        return droboffaddress;
    }
    public String getPickuplatlng(){
        return pickuplatlng;
    }
    public String getDrobofflatlng(){
        return drobofflatlng;
    }
    public String getPickupdate(){
        return pickupdate;
    }
    public String getPickuptime(){
        return pickuptime;
    }
    public String getEstimateddistance(){
        return estimateddistance;
    }
    public String getEstimatedfare(){
        return estimatedfare;
    }
    public String getCarType(){
        return carType;
    }



}