package me.image.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EPAAirBean implements Serializable {
    /**
     * 車牌號碼
     */
    private String license;
    /**
     * 廠牌
     */
    private String brandType;
    /**
     * 排氣量
     */
    private String engineCapacity;
    /**
     * 行程別
     */
    private String strokecycle;
    /**
     * 出廠日
     */
    private String birthDate;
    /**
     * 發照日期
     */
    private String useDate;

    private Boolean isVerify;

    private String message;

    private ServiceCheckBean checkBean = new ServiceCheckBean();

    private List<EPAAirDetailBean> list = new ArrayList<EPAAirDetailBean>();

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getBrandType() {
        return brandType;
    }

    public void setBrandType(String brandType) {
        this.brandType = brandType;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getStrokecycle() {
        return strokecycle;
    }

    public void setStrokecycle(String strokecycle) {
        this.strokecycle = strokecycle;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }


    public Boolean getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(Boolean isVerify) {
        this.isVerify = isVerify;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ServiceCheckBean getCheckBean() {
        return checkBean;
    }

    public void setCheckBean(ServiceCheckBean checkBean) {
        this.checkBean = checkBean;
    }

    public List<EPAAirDetailBean> getList() {
        return list;
    }

    public void setList(List<EPAAirDetailBean> list) {
        this.list = list;
    }


}
