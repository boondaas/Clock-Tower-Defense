package org.boondaas;

public class Enemy {
    float hp,speed;
    int type,x;

    public Enemy(int x,float hp,float speed, int type) {
        this.x = x;
        this.hp = hp;
        this.speed = speed;
        this.type = type;
    }
}
