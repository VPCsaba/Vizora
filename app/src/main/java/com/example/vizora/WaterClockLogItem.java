package com.example.vizora;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId; // Firestore mapping segédlet

public class WaterClockLogItem {
   @DocumentId
    private String id;
    private String email;
    private String contractNumber;
    private String waterclockID;
    private int waterCubic;

    public WaterClockLogItem() {

    }


    public WaterClockLogItem(String email, String contractNumber, String waterclockID, int waterCubic) {
        this.email = email;
        this.contractNumber = contractNumber;
        this.waterclockID = waterclockID;
        this.waterCubic = waterCubic;
        // Az id-t itt nem állítjuk be, majd a Firestore generálja
    }


    public WaterClockLogItem(String id, String email, String contractNumber, String waterclockID, int waterCubic) {
        this.id = id; // Itt be tudjuk állítani az ID-t
        this.email = email;
        this.contractNumber = contractNumber;
        this.waterclockID = waterclockID;
        this.waterCubic = waterCubic;
    }



    public String getEmail() {
        return email;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public String getWaterclockID() {
        return waterclockID;
    }

    public int getWaterCubic() {
        return waterCubic;
    }
    public void setEmail(String email) { this.email = email; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public void setWaterclockID(String waterclockID) { this.waterclockID = waterclockID; }
    public void setWaterCubic(int waterCubic) { this.waterCubic = waterCubic; }



    @DocumentId
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "WaterClockLogItem{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", contractNumber='" + contractNumber + '\'' +
                ", waterclockID='" + waterclockID + '\'' +
                ", waterCubic=" + waterCubic +
                '}';
    }
}
