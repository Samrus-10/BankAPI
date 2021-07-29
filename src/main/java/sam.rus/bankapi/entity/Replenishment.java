package sam.rus.bankapi.entity;

import java.util.Objects;

public class Replenishment {
    long id;
    double sum;
    long billId;

    public Replenishment() {
    }

    public Replenishment(double sum, long billId) {
        this.sum = sum;
        this.billId = billId;
    }

    public Replenishment(long id, double sum, long billId) {
        this.id = id;
        this.sum = sum;
        this.billId = billId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replenishment that = (Replenishment) o;
        return id == that.id && Double.compare(that.sum, sum) == 0 && billId == that.billId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sum, billId);
    }
}
