package sam.rus.bankapi.entity;

import java.util.Objects;

public class Card {
    long id;
    long cardNumber;
    String expires;
    String firstName;
    String lastName;
    long billId;
    String status;

    public Card() {
    }

    public Card(String expires, String firstName, String lastName, long billId) {
        this.expires = expires;
        this.firstName = firstName;
        this.lastName = lastName;
        this.billId = billId;
    }

    public Card(
            long id, long cardNumber, String expires, String firstName, String lastName, long billId, String status) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.expires = expires;
        this.firstName = firstName;
        this.lastName = lastName;
        this.billId = billId;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
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
        Card card = (Card) o;
        return id == card.id && cardNumber == card.cardNumber && billId == card.billId && Objects.equals(expires, card.expires) && Objects.equals(firstName, card.firstName) && Objects.equals(lastName, card.lastName) && Objects.equals(status, card.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, expires, firstName, lastName, billId, status);
    }
}
