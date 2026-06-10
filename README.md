# Clock Tower Defense

A tiny Java tower defense game made for **Duke's 8-Bit Adventure**.

Defend Duke's castle against an army of increasingly powerful clocks. Survive 30 waves, buy upgrades, collect vouchers, and try not to accidentally purchase **don't.**

---
## How to play

There are 3 troops in the game:
- sword guy
- bow guy
- cannon

You only have the first one at the start and have to buy the rest in the **shop**.

Upgrades are available in the shop separetly for each troop. the kind of upgrades:
- damage
- attack speed
- critical chance

There is another way of getting stronger: **vouchers**

There are 3 random vouchers in the shop, refreshing every time you enter it.

To see what a voucher does,click on them. **Duke** will tell you its name and description. 
You can click **BUY** to purchase it and get its effects.

### Money

You get money from 2 sources: enemy drops and working.

Each enemy drops 1 coin when it dies.

To work, click on Duke on the tower. when the work cooldown ends, you get 1 coin.
---

## Optimization Strategies

Since the goal of the jam is to make the game as small as possible, a lot of design decisions were made with byte count in mind.

### No External Libraries

The entire game uses only:

* Java 25
* Swing
* A single image resource

No game engines, no frameworks, no helper libraries.

---

### Minimal Assets

The game contains exactly one texture:

* Duke (used as the mouse cursor)

Everything else is rendered using Java's built-in drawing functions:

* Rectangles
* Polygons
* Lines
* Text

This completely removes the need for sprite sheets, UI textures, animations, and other large asset files.

---

### Array-Based Storage

Almost all game data is stored in primitive arrays.

Examples:

* Enemy storage
* Upgrade values
* Game state flags
* Owned vouchers

This avoids the memory and class overhead of more complex collection types.

---

### Tiny Enemy System

Enemies only store four values:

```java
int x;
float hp;
float speed;
int type;
```

No pathfinding, no AI trees, no physics engine, no collision system.

They simply walk toward the castle and get shot.

---

### Voucher Lookup Optimization

Instead of searching through a list of owned vouchers:

```java
for (int v : vouchersOwned)
    if (v == id)
        return true;
```

owned vouchers are stored using their own ID as an index:

```java
vouchersOwned[id] = id;
```

which allows lookups such as:

```java
return vouchersOwned[id] == id;
```

This is both smaller and faster.

---

### Reused Logic

Whenever possible, the same code paths are reused for:

* Damage calculation
* Critical hits
* Enemy targeting
* Voucher effects

Duplicated systems were avoided unless they improved readability significantly.

---

### Single-Tick Architecture

The entire game runs on a fixed update loop.

No threads.
No async systems.
No background workers.

One timer.
One update method.
One castle trying not to die.

---

### Minimal Object Creation

Enemies are created when they spawn.

After death they are removed from the array and eventually collected by Java's garbage collector.

No particle systems.
No temporary visual effects.
No object pools.

The clocks don't deserve that level of respect.

---

### String Budget

Voucher descriptions and UI text are intentionally short.

Strings occupy both source code space and compiled class space, so unnecessary text was avoided wherever possible.

Except for jokes.

Jokes are important.

---

## Known Technical Compromises

Some systems may look simpler than they would in a normal project.

This is intentional.

The challenge is inspired by cartridge-era development where every byte mattered more than architectural perfection.

If a feature could be implemented in 5 bytes instead of 50, the 5-byte version usually won.

---

## Final Thoughts

This project was built under a simple philosophy:

> If it works, is readable, and saves bytes, it's probably the correct solution.

Unless it's **don't.**

Then don't.


### If you encounter any bugs, reach me out in Discord: @boondaas
