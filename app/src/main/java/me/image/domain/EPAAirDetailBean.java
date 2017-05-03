package me.image.domain;

import java.io.Serializable;


public class EPAAirDetailBean implements Serializable {
    /**
     * 檢測站號
     */
    private String checkPlace;
    /**
     * 檢測種類
     */
    private String checkType;
    /**
     * HC
     */
    private String checkHC;
    /**
     * CO
     */
    private String checkCO;
    /**
     * CO2
     */
    private String checkCO2;
    /**
     * 序號
     */
    private String checkSerial;
    /**
     * 檢測結果
     */
    private String checkResult;
    /**
     * 檢測日期時間
     */
    private String checkDate;
    /**
     * 檢測標籤
     */
    private String checkLable;

    public String getCheckPlace() {
        return checkPlace;
    }

    public void setCheckPlace(String checkPlace) {
        this.checkPlace = checkPlace;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckHC() {
        return checkHC;
    }

    public void setCheckHC(String checkHC) {
        this.checkHC = checkHC;
    }

    public String getCheckCO() {
        return checkCO;
    }

    public void setCheckCO(String checkCO) {
        this.checkCO = checkCO;
    }

    public String getCheckCO2() {
        return checkCO2;
    }

    public void setCheckCO2(String checkCO2) {
        this.checkCO2 = checkCO2;
    }

    public String getCheckSerial() {
        return checkSerial;
    }

    public void setCheckSerial(String checkSerial) {
        this.checkSerial = checkSerial;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckLable() {
        return checkLable;
    }

    public void setCheckLable(String checkLable) {
        this.checkLable = checkLable;
    }


}
