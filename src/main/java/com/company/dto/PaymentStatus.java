package com.company.dto;

import com.sun.istack.internal.Nullable;

public class PaymentStatus {
    @Nullable
    private String error;
    public PaymentStatus(String error){
        this.error = error;
    }
    public PaymentStatus(){
        error = null;
    }
    public boolean ok(){
        return error!=null;
    }
    public String getMessage(){
        return error;
    }
}
