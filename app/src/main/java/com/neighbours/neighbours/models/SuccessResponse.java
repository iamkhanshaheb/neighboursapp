package com.neighbours.neighbours.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessResponse {

@SerializedName("status")
@Expose
private String status;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}