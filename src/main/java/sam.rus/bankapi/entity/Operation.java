package sam.rus.bankapi.entity;

import java.util.Objects;

public class Operation {
    long id;
    long sourceId;
    long targetId;
    double sum;
    String status;

    public Operation() {
    }

    public Operation(long sourceId, long targetId, double sum) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sum = sum;
    }

    public Operation(long sourceId, long targetId, double sum, String status) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sum = sum;
        this.status = status;
    }

    public Operation(long id, long sourceId, long targetId, double sum, String status) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sum = sum;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return id == operation.id && sourceId == operation.sourceId && targetId == operation.targetId &&
                Double.compare(operation.sum, sum) == 0 && Objects.equals(status, operation.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceId, targetId, sum, status);
    }
}
