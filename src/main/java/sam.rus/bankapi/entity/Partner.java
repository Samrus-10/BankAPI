package sam.rus.bankapi.entity;

import java.util.Objects;

public class Partner {
    long id;
    String name;
    long partnerBill;

    public Partner() {
    }

    public Partner(String name, long partnerBill) {
        this.name = name;
        this.partnerBill = partnerBill;
    }

    public Partner(long id, String name, long partnerBill) {
        this.id = id;
        this.name = name;
        this.partnerBill = partnerBill;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPartnerBill() {
        return partnerBill;
    }

    public void setPartnerBill(long partnerBill) {
        this.partnerBill = partnerBill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner partner = (Partner) o;
        return id == partner.id && partnerBill == partner.partnerBill && Objects.equals(name, partner.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, partnerBill);
    }
}
