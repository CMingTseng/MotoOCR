package me.image.domain;

import java.io.Serializable;


public class ServiceCheckBean implements Serializable {

    public String id;

    public String type;

    public String ocrResult;

    public Boolean checkResult;

    public String errorMessage;

    public String errorCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOcrResult() {
        return ocrResult;
    }

    public void setOcrResult(String ocrResult) {
        this.ocrResult = ocrResult;
    }

    public Boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


}
