package com.militaryservices.app.security;

import com.militaryservices.app.dto.Active;
import com.militaryservices.app.dto.Situation;

public class CheckOptions {

    public static boolean checkOptions(String situation,String active) {
        if(checkSituation(situation) && checkActive(active))
            return true;

        return false;
    }

    public static boolean checkSituation(String situation) {
        try {
            if(Situation.ARMED.name().toLowerCase().equals(situation))
                return true;
            if(Situation.UNARMED.name().toLowerCase().equals(situation))
                return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

        return false;
    }

    public static boolean checkActive(String active) {
        try {
            if(Active.getFreeOfDuty().equals(active))
                return true;
            if(Active.ACTIVE.name().toLowerCase().equals(active))
                return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

        return false;
    }

}
