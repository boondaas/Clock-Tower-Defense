package org.boondaas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Polygon;
import java.util.Objects;
import java.util.Random;


public class Main extends JFrame {

    public Main() {
        setTitle("name");
        setSize(800, 800);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        add(new GamePanel());
        setVisible(true);
    }

    class GamePanel extends JPanel {

        Enemy[] enemies = new Enemy[30];

        int money = 0;
        byte wave = 0;

        BufferedImage duke;

        byte enemySpawnCooldown = 120;
        byte enemySpCdCurrent = enemySpawnCooldown;

        byte TR1Cooldown = 20; //cooldown for troop 1
        byte TR2Cooldown = 80; //cooldown for troop 2
        byte TR3Cooldown = 120; //cooldown for troop 3
        byte workCooldown = 40; //cooldown for working

        byte TR1CdCurr = TR1Cooldown;  //cooldown counters
        byte TR2CdCurr = TR2Cooldown;
        byte TR3CdCurr = TR3Cooldown;
        byte WorkCdCurrent = (byte) (workCooldown + 1);

        boolean GameOver = false;
        boolean Won = false;

        float[] stats = {
                1, 1, 0,
                1, 1, 0,
                0.5f, 1, 0
        };

        int[] statLevels = {
                1, 1, 1,
                1, 1, 1,
                1, 1, 1
        };

        Rectangle[] buttons = {
                new Rectangle(1150, 100, 180, 50), //SHOP

                new Rectangle(280, 150, 180, 50), new Rectangle(480, 150, 180, 50), new Rectangle(680, 150, 180, 50),  //upgrade rectangles
                new Rectangle(280, 250, 180, 50), new Rectangle(480, 250, 180, 50), new Rectangle(680, 250, 180, 50),
                new Rectangle(280, 350, 180, 50), new Rectangle(480, 350, 180, 50), new Rectangle(680, 350, 180, 50),

                new Rectangle(300, 500, 100, 150), new Rectangle(450, 500, 100, 150), new Rectangle(600, 500, 100, 150),  //vouchers

                new Rectangle(280, 250, 320, 50), //troop unlock buttons
                new Rectangle(280, 350, 320, 50),

                new Rectangle(),                                      //duke work button, described in paintComponent()
                new Rectangle(1100, 620, 130, 50)
        };

        short[] upgradePrices = {
                1, 1, 1,
                1, 1, 1,
                1, 1, 1,

                1, 1, 1
        };

        boolean[] states = {
                false,  //isInShop
                false, false,  //troop unlocks
        };

        int[] vouchersOwned = new int[17];
        int[] vouchersInShop = new int[3];
        int voucherSelected = -1;
        boolean[] voucherSlots = {true, true, true};

        String[] rarities = {"COMMON", "UNCOMMON", "RARE", "LEGENDARY"};

        Voucher[] vouchers = {
                new Voucher((byte) 0, (byte) 0, (short) 4, Color.BLUE, "Mr. Beast", "Sets all listed prices shop to 1$"),
                new Voucher((byte) 1, (byte) 2, (short) 4, Color.BLUE, "sans", "Prevents death. Self-destructs"),
                new Voucher((byte) 2, (byte) 2, (short) 4, Color.BLUE, "don't.", "Still don't."),
                new Voucher((byte) 3, (byte) 1, (short) 4, Color.BLUE, "Magnetite Dice", "+15% crit chance"),
                new Voucher((byte) 4, (byte) 1, (short) 4, Color.cyan, "Credit Card", "Doubles money income"),
                new Voucher((byte) 5, (byte) 0, (short) 4, Color.BLACK, "Glitch", "*.[#<\\ˇ%#$ß×¨(+=~¤đ"),                                                                                      //in case you cheated and looked at the code before playing, this voucher gives a random dmg multiplier between 0.5 and 3 to each attack
                new Voucher((byte) 6, (byte) 0, (short) 4, Color.BLUE, "a", "nothing"),         //this cannot even show up...
                new Voucher((byte) 7, (byte) 1, (short) 4, Color.BLUE, "Gift", "Upgrades 3 random stats by 3 levels"),
                new Voucher((byte) 8, (byte) 3, (short) 4, Color.BLUE, "Ascension", "Sets every stat's level to the level of the most upgraded stat"),
                new Voucher((byte) 9, (byte) 1, (short) 4, Color.BLUE, "Bank", "Gains +0.25 dmg for each $ you have"),
                new Voucher((byte) 10, (byte) 0, (short) 4, Color.RED, "GTA 6", "Cannot do anything cuz it doesn't exist..."),
                new Voucher((byte) 11, (byte) 0, (short) 4, Color.BLUE, "Voucher", "X1.5 dmg"),
                new Voucher((byte) 12, (byte) 3, (short) 4, Color.BLUE, "Death", "2% chance for each enemy to die instantly, 10% to start with X0.5 HP"),
                new Voucher((byte) 13, (byte) 0, (short) 4, Color.RED, "Cake Is A Lie", "Gain 50$ instantly"),
                new Voucher((byte) 14, (byte) 1, (short) 4, Color.BLACK, "Megalovania", "0.1% chance each tick to deal 20 dmg to all enemies"),
                new Voucher((byte) 15, (byte) 2, (short) 4, Color.MAGENTA, "Golden Clock", "Enemies drop X3 money"),
                new Voucher((byte) 16, (byte) 0, (short) 4, Color.BLUE, "Coffee", "+10% attack speed")
        };

        Polygon cape = new Polygon(
                new int[]{12, 27, 39, 0},
                new int[]{0, 0, 49, 49},
                4
                );

        Random random = new Random();

        float bankValue = 0;


        public GamePanel() {
            setBackground(new Color(144, 208, 255));


            Timer timer = new Timer(50, actionEvent-> update());
            timer.start();

            try {
                duke = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/duke.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }


            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {

                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    System.out.println(mouseX+"  "+mouseY);

                    if (buttons[0].contains(mouseX, mouseY)) {
                        voucherSlots = new boolean[]{true, true, true};
                        states[0] = !states[0];
                        voucherSelected = -1;
                        for (int i = 0; i < 3; i++) {
                            byte rand = (byte) random.nextInt(100);    //W bruteforce :---)
                            if (rand < 8) {
                                vouchersInShop[i] = 0;
                            } else if (rand < 15) {
                                vouchersInShop[i] = 5;
                            } else if (rand < 22) {
                                vouchersInShop[i] = 9;
                            } else if (rand < 29) {
                                vouchersInShop[i] = 10;
                            } else if (rand < 36) {
                                vouchersInShop[i] = 11;
                            } else if (rand < 43) {
                                vouchersInShop[i] = 13;
                            } else if (rand < 50) {
                                vouchersInShop[i] = 16;
                            } else if (rand < 56) {
                                vouchersInShop[i] = 3;
                            } else if (rand < 62) {
                                vouchersInShop[i] = 4;
                            } else if (rand < 68) {
                                vouchersInShop[i] = 7;
                            } else if (rand < 74) {
                                vouchersInShop[i] = 9;
                            } else if (rand < 80) {
                                vouchersInShop[i] = 14;
                            } else if (rand < 84) {
                                vouchersInShop[i] = 1;
                            } else if (rand < 88) {
                                vouchersInShop[i] = 2;
                            } else if (rand < 92) {
                                vouchersInShop[i] = 15;
                            } else if (rand < 96) {
                                vouchersInShop[i] = 8;
                            } else {
                                vouchersInShop[i] = 12;
                            }

                            upgradePrices[i + 8] = vouchers[vouchersInShop[i]].price;
                        }
                    }

                    if (states[0]) {
                        if (buttons[10].contains(mouseX, mouseY)) {
                            voucherSelected = 0;
                        } else if (buttons[11].contains(mouseX, mouseY)) {
                            voucherSelected = 1;
                        } else if (buttons[12].contains(mouseX, mouseY)) {
                            voucherSelected = 2;
                        } else if (!states[1] && buttons[13].contains(mouseX, mouseY) && money >= 10) {
                            states[1] = true;
                            money -= 10;
                        } else if (!states[2] && buttons[14].contains(mouseX, mouseY) && money >= 15) {
                            states[2] = true;
                            money -= 15;
                        } else if (buttons[16].contains(mouseX, mouseY) && voucherSelected > -1 && voucherSlots[voucherSelected] && money >= upgradePrices[8 + voucherSelected]) {
                            voucherEffect(vouchersInShop[voucherSelected]);

                            money -= upgradePrices[8 + voucherSelected];
                            voucherSlots[voucherSelected] = false;
                            voucherSelected = -1;
                        } else if (buttons[1].contains(mouseX, mouseY) && money >= upgradePrices[0]) {money -= upgradePrices[0]; stats[0]++; upgradePrices[0]++;}
                        else if (buttons[2].contains(mouseX, mouseY) && money >= upgradePrices[1]) {money -= upgradePrices[1]; stats[1]++; upgradePrices[1]++;}
                        else if (buttons[3].contains(mouseX, mouseY) && money >= upgradePrices[2]) {money -= upgradePrices[2]; stats[2]++; upgradePrices[2]++;}
                        else if (buttons[4].contains(mouseX, mouseY) && money >= upgradePrices[3]) {money -= upgradePrices[3]; stats[3]++; upgradePrices[3]++;}
                        else if (buttons[5].contains(mouseX, mouseY) && money >= upgradePrices[4]) {money -= upgradePrices[4]; stats[4]++; upgradePrices[4]++;}
                        else if (buttons[6].contains(mouseX, mouseY) && money >= upgradePrices[5]) {money -= upgradePrices[5]; stats[5]++; upgradePrices[5]++;}
                        else if (buttons[7].contains(mouseX, mouseY) && money >= upgradePrices[6]) {money -= upgradePrices[6]; stats[6]++; upgradePrices[6]++;}
                        else if (buttons[8].contains(mouseX, mouseY) && money >= upgradePrices[7]) {money -= upgradePrices[7]; stats[7]++; upgradePrices[7]++;}
                        else if (buttons[9].contains(mouseX, mouseY) && money >= upgradePrices[8]) {money -= upgradePrices[8]; stats[8]++; upgradePrices[8]++;}

                    } else {
                        if (buttons[15].contains(mouseX, mouseY)) {
                            WorkCdCurrent--;
                        }
                    }

                }
            });
        }

        public void update() {


            if (random.nextFloat() < 0.001f && hasVoucher(14)) {
                for(Enemy e : enemies) {
                    if (e != null) {
                        e.hp -= 20;
                    }
                }
            }

            boolean allDead = true;
            for (Enemy e : enemies) {
                if (e != null) { allDead = false; break; }
            }
            if (wave >= 30 && allDead) {
                GameOver = true;
                Won = true;
            }

            for (int i = 0; i < 3; i++) {
                stats[i * 3] = 0.2f * statLevels[i * 3] + ((i == 2) ? (1.5f) : (1));
                stats[i * 3 + 1] = (float) Math.pow(0.9, statLevels[i * 3 + 1]);
                stats[i * 3 + 2] = 0.02f * (statLevels[i * 3 + 2] + (hasVoucher(3) ? 0.15f : 0f));
            }



            if (!states[0]) {
                if (enemySpCdCurrent > 0) {
                    enemySpCdCurrent--;
                } else if (wave < 30) {
                    enemySpCdCurrent = enemySpawnCooldown;
                    enemies[wave] = new Enemy(1400, (float) (Math.pow(1.17, wave) * (hasVoucher(12) ? (random.nextFloat() < 0.02 ? 0 : (random.nextFloat() < 0.5f ? 0.5f : 1)) : 1)), 2.5f, 0);
                    wave++;
                }


                int leftmost = -1;

                for (int i = 0; i < enemies.length; i++) {

                    if (enemies[i] == null) continue;

                    if (leftmost == -1 || enemies[leftmost] == null || enemies[i].x < enemies[leftmost].x) {
                        leftmost = i;
                    }

                    if (enemies[i] == null) continue;
                    enemies[i].x -= enemies[i].speed;
                    if (enemies[i].x <= 280) {
                        if (!hasVoucher(1)) {
                            GameOver = true;
                            Won = false;
                        } else {
                            vouchersOwned[1] = -1;
                            enemies[i] = null;
                        }
                    }
                    if (enemies[i] == null) continue;
                    if(enemies[i].hp <= 0) {
                        enemies[i] = null;
                        money += (hasVoucher(15) ? 3 : 1) * (hasVoucher(4) ? 2 : 1);
                    }

                }

                //duke work
                if (WorkCdCurrent > 0 && WorkCdCurrent <= workCooldown) {
                    WorkCdCurrent--;
                } else if (WorkCdCurrent <= 0) {
                    WorkCdCurrent = (byte) (workCooldown + 1);
                    money += hasVoucher(4) ? 2 : 1;
                }



                if (TR1CdCurr > 0) {
                    TR1CdCurr--;
                } else {

                    TR1CdCurr = (byte)(TR1Cooldown * stats[1] * (hasVoucher(16) ? 0.9f : 1f)); // attack speed

                    for (Enemy e : enemies) {

                        if (e == null) continue;

                        if (e.x > 500 && e.x < 600) {

                            float dmg = stats[0];

                            if (random.nextFloat() < stats[2]) {
                                dmg *= 2;
                            }

                            if (hasVoucher(12)) dmg *= 1.5f; // Voucher
                            if (hasVoucher(6)) dmg = 0.5f + random.nextFloat() * 2.5f;

                            dmg += hasVoucher(9) ? money / 4 : 0;
                            dmg *= (hasVoucher(5) ? random.nextFloat() * 2.5f + 0.5f : 1);
                            dmg *= hasVoucher(11) ? 1.5f : 1;

                            e.hp -= dmg;
                        }
                    }
                }

                if (states[1]) {

                    if (TR2CdCurr > 0) {
                        TR2CdCurr--;
                    } else {

                        TR2CdCurr = (byte)(TR2Cooldown * stats[4] * (hasVoucher(15) ? 0.9f : 1));

                        if (leftmost != -1) {

                            Enemy e = enemies[leftmost];

                            float dmg = stats[3];

                            if (random.nextFloat() < stats[5]) {
                                dmg *= 2;
                            }

                            if (hasVoucher(12)) dmg *= 1.5f;

                            dmg += hasVoucher(9) ? money / 4 : 0;
                            dmg *= (hasVoucher(5) ? random.nextFloat() * 2.5f + 0.5f : 1);
                            dmg *= hasVoucher(11) ? 1.5f : 1;
                            e.hp -= dmg;
                        }
                    }
                }

                if (states[2]) {

                    if (TR3CdCurr > 0) {
                        TR3CdCurr--;
                    } else {

                        TR3CdCurr = (byte)(TR3Cooldown * stats[7] * (hasVoucher(15) ? 0.9f : 1));

                        if (leftmost != -1) {

                            for (int i = 0; i < 30; i++) {
                                if (enemies[leftmost] == null) {
                                    leftmost++;
                                } else break;
                                if (enemies[leftmost] == null && leftmost >= 30) IO.println("leftmost error");
                            }

                            Enemy e = enemies[leftmost];

                            float dmg = stats[6];

                            if (random.nextFloat() < stats[8]) {
                                dmg *= 2;
                            }

                            if (hasVoucher(12)) dmg *= 1.5f;

                            dmg += hasVoucher(9) ? money / 4 : 0;
                            dmg *= (hasVoucher(5) ? random.nextFloat() * 2.5f + 0.5f : 1);
                            dmg *= hasVoucher(11) ? 1.5f : 1;
                            e.hp -= dmg;
                        }
                    }
                }



            }



            repaint();
        }

        public void voucherEffect(int id) {
            switch (id) {
                case 0 -> {for (int i = 0; i < 12; i++) upgradePrices[i] = 1;}
                case 1 -> {vouchersOwned[id] = id;}
                case 2 -> {
                    System.err.println("The voucher literally said don't.");

                    for(int i = 0; i < 10000; i++) {
                        System.err.println("don't.");
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}

                    System.exit(1);
                }
                case 3 -> {vouchersOwned[id] = id;}
                case 4 -> {vouchersOwned[id] = id;}
                case 5 -> {vouchersOwned[id] = id;}
                case 6 -> {vouchersOwned[id] = id;}
                case 7 -> {for (int i = 0; i < 3; i++) {
                    int rand = random.nextInt(9);
                    statLevels[rand] += 3;
                }}
                case 8 -> {
                    int max = 1;
                    for (int i = 0; i < 9; i++) {
                        if (statLevels[i] > max) {
                            max = statLevels[i];
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        statLevels[i] = max;
                    }
                }
                case 9 -> {vouchersOwned[id] = id;}
                case 10 -> {}
                case 11 -> {vouchersOwned[id] = id;}
                case 12 -> vouchersOwned[id] = id;
                case 13 -> {money += 50;}
                case 14 -> vouchersOwned[id] = id;
                case 15 -> vouchersOwned[id] = id;
                case 16 -> vouchersOwned[id] = id;
            }
        }

        //text wrapper
        public static String[] splitByWords(String text, int maxLen) {

            String[] words = text.split(" ");
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();

            StringBuilder current = new StringBuilder();

            for (String w : words) {

                if (w.length() > maxLen) {

                    if (current.length() > 0) {
                        lines.add(current.toString());
                        current.setLength(0);
                    }

                    lines.add(w);
                    continue;
                }

                if (current.length() + w.length() + (current.length() == 0 ? 0 : 1) > maxLen) {
                    lines.add(current.toString());
                    current.setLength(0);
                }

                if (current.length() > 0) {
                    current.append(' ');
                }
                current.append(w);
            }

            if (current.length() > 0) {
                lines.add(current.toString());
            }

            return lines.toArray(new String[0]);
        }

        private boolean hasVoucher(int id) {
            return vouchersOwned[id] == id;
        }



        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            Graphics2D gTrans = (Graphics2D) g2.create();

            g2.setStroke(new BasicStroke(3));

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Monospaced", Font.BOLD, 50));
            FontMetrics fm = g2.getFontMetrics();

            int groundHeight = (int) (getHeight() * 0.7f);
            buttons[15] = new Rectangle(268, groundHeight - 348, 36, 48);

            g2.setColor(new Color(60, 147, 0));
            g2.fillRect(0, groundHeight, getWidth(), (int) (getHeight() * 0.3f));
            g2.setColor(new Color(96, 96, 96, 255));
            g2.fillRect(130, groundHeight - 160, 300, 160); //castle body
            g2.fillRect(245, groundHeight - 250, 70, 90);   //main tower
            g2.setColor(new Color(85, 85, 85, 255));
            g2.fillRect(115, groundHeight - 210, 90, 50);   //tower bumps
            g2.fillRect(355, groundHeight - 210, 90, 50);
            g2.fillRect(230, groundHeight - 300, 100, 50);
            g2.setColor(new Color(138, 78, 0));                         //gate
            g2.fillRect(245, groundHeight - 80, 70, 80);
            g2.fillOval(245, groundHeight - 115, 70, 70);
            g2.setColor(new Color(55, 55, 55));
            g2.fillRect(275, groundHeight - 220, 10, 20);     //cool lil window thingys
            g2.fillOval(275, groundHeight - 225, 10, 10);
            g2.fillRect(160, groundHeight - 130, 10, 20);
            g2.fillOval(160, groundHeight - 135, 10, 10);
            g2.fillRect(390, groundHeight - 130, 10, 20);
            g2.fillOval(390, groundHeight - 135, 10, 10);


            g2.drawImage(duke, 268, groundHeight - 348, 36, 48, this);


            gTrans.setColor(new Color(131, 0, 202));
            gTrans.translate(500, groundHeight - 49);
            gTrans.fillPolygon(cape);



            if (states[1]) {
                gTrans.setTransform(g2.getTransform());
                gTrans.translate(380, groundHeight - 259);
                gTrans.fillPolygon(cape);
            }


            g2.setColor(new Color(255, 191, 172));
            g2.fillOval(505, groundHeight - 80, 30, 30);
            if (states[1]) {
                g2.fillOval(385, groundHeight - 290, 30, 30);
                g2.setColor(new Color(85, 45, 0));
                QuadCurve2D bow = new QuadCurve2D.Float(
                        425, groundHeight - 215,
                        445, groundHeight - 240,
                        425, groundHeight - 265
                );
                g2.draw(bow);
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(425, groundHeight - 215, 425, groundHeight - 265);
            }
            g2.setColor(Color.GRAY);
            g2.drawLine(540, groundHeight - 20, 540, groundHeight - 60);
            g2.drawLine(535, groundHeight- 25, 545, groundHeight - 25);


            if (states[2]) {
                g2.fillRect(200, groundHeight - 50, 60, 30);
                g2.setColor(new Color(99, 44, 0));
                g2.fillOval(175, groundHeight - 50, 50, 50);
            }



            gTrans.dispose();


            //draw clocks
            for (Enemy e : enemies) {
                if (e == null) continue;
                drawClock( e.x,groundHeight - 50, 50, g2);
            }








            if (states[0]) {
                g2.setColor(new Color(37, 37, 37, 144));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(234, 106, 33));
                for (int i = 0; i < 4; i++) g2.fill(buttons[i]);

                if (states[1]) {
                    for (int i = 4; i < 7; i++) g2.fill(buttons[i]);
                } else {
                    g2.fill(buttons[13]);
                    g2.setColor(new Color(0, 0, 0));
                    g2.drawString("UNLOCK", 310, 289);
                    g2.drawString("10$", 500, 289);
                    g2.setColor(new Color(234, 106, 33));
                }

                if (states[2]) {
                    for (int i = 7; i < 10; i++) g2.fill(buttons[i]);
                } else {
                    g2.fill(buttons[14]);
                    g2.setColor(new Color(0, 0, 0));
                    g2.drawString("UNLOCK", 310, 389);
                    g2.drawString("15$", 500, 389);
                    g2.setColor(new Color(234, 106, 33));
                }

                g2.setFont(new Font("MONOSPACED", Font.BOLD, 30));
                g2.drawString("Sword guy", 70, buttons[1].y + 39);
                g2.drawString("Bow guy", 70, buttons[4].y + 39);
                g2.drawString("Cannon", 70, buttons[7].y + 39);

                g2.drawString("DMG", buttons[1].x + 50, 100);
                g2.drawString("Atk speed", buttons[2].x, 100);
                g2.drawString("Crit chance", buttons[3].x + 10, 100);


                for (int i = 0; i < 3; i++) {
                    g2.setColor(voucherSlots[i] ? vouchers[vouchersInShop[i]].color : Color.LIGHT_GRAY);
                    g2.fill(buttons[i + 10]);
                }

                g2.drawImage(duke, 1000, 180, 200, duke.getHeight() * (200 / duke.getWidth()), this);
                g2.setColor(new Color(112, 43, 2));
                g2.fillRect(900, 400, 400, 300);
                g2.setColor(new Color(239, 198, 175));
                g2.fillRect(910, 410, 380, 280);


                g2.setFont(new Font("MONOSPACED", Font.BOLD, 50));
                g2.setColor(Color.BLACK);
                g2.drawString("EXIT", 1180, 139);



                if (voucherSelected >= 0) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("MONOSPACED", Font.BOLD, 30));
                    g2.drawString(vouchers[vouchersInShop[voucherSelected]].name, 930, 449);
                    g2.setFont(new Font("MONOSPACED", Font.BOLD, 26));
                    String[] DescrLined = splitByWords(vouchers[vouchersInShop[voucherSelected]].description, 20);
                    for (int i = 0; i < DescrLined.length; i++) {
                        g2.drawString(DescrLined[i], 930, 500 + 30 * i);
                    }
                    g2.setColor(new Color(234, 106, 33));
                    g2.fill(buttons[16]);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("MONOSPACED", Font.BOLD, 40));
                    g2.drawString("BUY", 1125, 660);
                    g2.setFont(new Font("MONOSPACED", Font.BOLD, 50));


                }


            } else {
                g2.setColor(new Color(234, 106, 33));
                g2.fill(buttons[0]);



                g2.setColor(Color.BLACK);
                g2.drawString("SHOP", 1180, 139);


            }

            drawCoin(1300, 40, g2);
            g2.setFont(new Font("Monospaced", Font.BOLD, 36));
            fm = g2.getFontMetrics();
            g2.setColor(new Color(255, 193, 97));
            g2.drawString(money + "", 1280 - fm.stringWidth(money + ""), 67);



            if (GameOver) {
                gameOver(g2);
            }

        }

        private static void drawCoin(int x, int y, Graphics2D g2) {
            g2.setColor(new Color(230, 176, 0));
            g2.fillOval(x, y, 30, 30);
        }

        public void drawClock(int x, int y, int size, Graphics2D g2) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 255, 255));
            g2.fillOval(x, y, size, size);
            g2.setColor(new Color(0, 0, 0));
            g2.drawLine(x + size / 2, y + 3, x + size / 2, y + size / 2);
            g2.drawLine(x + size / 2, y + size / 2, (int) (x + size * 0.75), y + size / 4);
        }


        public void gameOver(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, 1500, 800);
            g2.setColor(Color.RED);
            g2.setFont(new Font("Monospaced", Font.ITALIC, 70));
            g2.drawString(Won ? "Duke Won" : "Duke Ran Out Of Time", (getWidth() - g2.getFontMetrics().stringWidth("Duke Ran Out Of Time")) / 2, 300);
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}