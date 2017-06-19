package com.example.marcin.lista;

public class Row {

    private long id;
    private String name;
    private int amount;

    public Row(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public Row(String name, int amount, long id) {
        this(name, amount);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}