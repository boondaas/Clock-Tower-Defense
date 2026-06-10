package org.boondaas;

import java.awt.Color;

public class Voucher {

    public byte id;
    public byte rarity;

    public short price;
    public String name;
    public String description;

    public Color color;

    public Voucher(byte id, byte rarity, short price, Color color, String name, String description) {
        this.id = id;
        this.rarity = rarity;
        this.price = price;
        this.color = color;
        this.name = name;
        this.description = description;
    }

    public static final byte COMMON = 0;
    public static final byte UNCOMMON = 1;
    public static final byte RARE = 2;
    public static final byte LEGENDARY = 3;

}
