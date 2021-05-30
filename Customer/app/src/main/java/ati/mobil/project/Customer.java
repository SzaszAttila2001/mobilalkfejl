package ati.mobil.project;

import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private String href;
    private String id;
    private String name;
    private String status;
    private String statusReason;
    private Date validStart;
    private Date validEnd;
    private String party;
    private String payment_method;
     //TODO: Set the parties.

    //public Customer(String m_href, String m_id, String m_name, String m_status, String m_statusReason, Date m_validStart, Date m_validEnd) {
    //    this.href = m_href;
    //    this.id = m_id;
    //    this.name = m_name;
    //    this.status = m_status;
    //    this.statusReason = m_statusReason;
    //    this.validStart = m_validStart;
    //    this.validEnd = m_validEnd;
    //}

    public Customer(String m_name, String m_status, String m_statusReason, Date m_validStart, Date m_validEnd, String m_party, String m_payment_method) {
        this.name = m_name;
        this.status = m_status;
        this.statusReason = m_statusReason;
        this.validStart = m_validStart;
        this.validEnd = m_validEnd;
        this.party = m_party;
        this.payment_method = m_payment_method;
    }

    public Customer(){}


    public String getName() {
        return name;
    }

    public void setName(String m_name) {
        this.name = m_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String m_status) {
        this.status = m_status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String m_statusReason) {
        this.statusReason = m_statusReason;
    }

    public Date getValidStart() {
        return validStart;
    }

    public void setValidStart(Date m_validStart) {
        this.validStart = m_validStart;
    }

    public Date getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(Date m_validEnd) {
        this.validEnd = m_validEnd;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}
