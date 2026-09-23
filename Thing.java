import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;


public class Thing implements Serializable {
    String[] names;
    String description;
    Thing[] contents;
    int capacity;

    boolean container;
    boolean open;
    boolean deadly;
    boolean silent;
    boolean takeable;

    Thing[] keepTrackOf;

    public void doTurn(int turns) {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                this.contents[i].doTurn(turns);
            }
        }
    }

    public void addToContents(Thing item) {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] == null) {
                this.contents[i] = item;
                return;
            }
        }
    }

    public void removeFromContents(Item item) {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] == item) {
                this.contents[i] = null;
                return;
            }
        }
    }

    public boolean isEmpty() {
        for (int i=0; i<this.contents.length; i++) {
            if (this.contents[i] != null && !this.contents[i].silent && !(this.contents[i].getClass() == NPC.class)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Thing target) {
        for (int i=0; i<this.contents.length; i++) {
            if (this.contents[i] == target) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRoom() {
        for (int i=0; i<this.contents.length; i++) {
            if (this.contents[i] == null) {
                return true;
            }
        }
        return false;
    }

    public String nameForList() {
        String ans = "";

        if ("aeiou".indexOf(this.names[0].charAt(0)) != -1) {
            ans += "an ";
        } else {
            ans += "a ";
        }

        ans += this.names[0];

        if (this.container) {
            if (this.open) {
                ans += " (open)";
            }
            else {
                ans += " (closed)";
            }
        }

        return ans;
    }

    public String listContents() {
        ArrayList<Thing> contents = new ArrayList<Thing>();

        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null && !this.contents[i].silent && this.contents[i].getClass() != NPC.class) {
                contents.add(this.contents[i]);
            }
        }

        Thing[] ans = contents.toArray(new Thing[0]);
        
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<ans.length; i++) {

            stringBuilder.append("\n" + ans[i].nameForList());

            if (ans[i].container) {
                if (ans[i].open) {
                    if (!ans[i].isEmpty()) {
                        stringBuilder.append("\nThe " + ans[i].toString() + " contains:");
                    }
                    stringBuilder.append(ans[i].listContents().replaceAll("\n", "\n    "));
                }
            }
        }

        return stringBuilder.toString();
    }

    public ArrayList<Thing> getAccessibleContents() {
        ArrayList<Thing> ans = new ArrayList<Thing>(Arrays.asList(this.contents));

        for (int i=0; i<this.contents.length; i++) {
            if (this.contents[i] == null) {
                continue;
            }
            if (this.contents[i].container && this.contents[i].open){
                ans.addAll(this.contents[i].getAccessibleContents());
            }
        }

        return ans;
    }

    @Override
    public String toString() {
        return this.names[0];
    }

    public String getTaken(Player player) {
        if (!this.takeable) {
            return "Good luck with that!";
        }
        player.game.free(this);
        player.addToContents(this);
        return "Ok, you are now holding the " + this.names[0] + ".";
    }

    public String getDropped(Player player, Thing indirect) {
        if (indirect == null) {
            player.game.free(this);
            player.location.addToContents(this);
            return "Dropped.";
        }

        player.game.free(this);
        indirect.addToContents(this);
        return "Ok, the " + this.names[0] + " is now in the " + indirect.names[0] + ".";
    }

    public String getOpened() {
        if (!this.container) {
            return "Good luck with that!";
        }
        if (this.open) {
            return "The " + this.names[0] + " is already open.";
        }
        this.open = true;
        return "Ok, the " + this.names[0] + " is now open.";
    }

    public String getClosed() {
        if (!this.container) {
            return "Good luck with that!";
        }
        if (!this.open) {
            return "The " + this.names[0] + " is already closed.";
        }
        this.open = false;
        return "Ok, the " + this.names[0] + " is now closed.";
    }

    public String getHit(Thing indirect) {
        return "Violence isn't the anwser.";
    }

    public String getThrownAt(Player player, Thing direct) {
        player.game.free(direct);
        player.location.addToContents(direct);
        return "The " + direct + " flies past the " + this.names[0] + ", and clatters to the ground.";
    }

    public String getSmelled() {
        return "The " + this.names[0] + " smells just as you would expect.";
    }

    public String getEaten() {
        return "I don't think the " + this.names[0] + " would agree with you.";
    }

    public String getDrank(Player player) {
        return "The " + this.names[0] + " isn't really the sort of thing that you drink.";
    }

    public String getRead() {
        return "The " + this.names[0] + " doesn't have any wisdom for you.";
    }

    public String getKissed() {
        return "Mwah!";
    }

    public String getHugged() {
        return "The " + this.names[0] + " feels your affection.";
    }

    public String getListened() {
        return "The " + this.names[0] + " isn't making any noise.";
    }

    public String getSorried() {
        return "I'm sure the " + this.names[0] + " forgives you.";
    }

    public String getTasted() {
        return "The " + this.names[0] + " tastes about how you'd expect.";
    }

    public String getTouched() {
        return "The " + this.names[0] + " feels about how you'd expect.";
    }

    public String getLocked(Thing locker) {
        return "The " + this.names[0] + " can't be locked.";
    }

    public String getUnlocked(Thing unlocker) {
        return "The " + this.names[0] + " can't be unlocked";
    }
}


class Room extends Thing {

    Room n;
    Room s;
    Room e;
    Room w;
    Room ne;
    Room se;
    Room sw;
    Room nw;
    Room u;
    Room d;
    Room i;
    Room o;

    public Room(String[] names, String description, int capacity) {
        this.description = description;
        this.names = names;
        this.contents = new Thing[capacity];
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        String ans = "\u001B[1m" + this.names[0] + "\u001B[22m" + '\n' + this.description;
        if (!this.isEmpty())
            ans += "\n\nHere you see:" + this.listContents().replaceAll("\n", "\n    ");
        
        for (int i=0; i<this.contents.length; i++) {
            if (this.contents[i] == null) {
                continue;
            }
            if (this.contents[i].getClass() == NPC.class) {
                ans += "\n\n" + this.contents[i].names[0] + " is here.";
            }
        }

        return ans;
    }

    // I know there's probably a better way to do this, but I don't have the energy to figure it out
    public String north (Player player) {
        if (this.n != null) {
            player.location = this.n;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String south(Player player) {
        if (this.s != null) {
            player.location = this.s;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String east(Player player) {
        if (this.e != null) {
            player.location = this.e;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String west(Player player) {
        if (this.w != null) {
            player.location = this.w;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String northeast(Player player) {
        if (this.ne != null) {
            player.location = this.ne;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String southeast(Player player) {
        if (this.se != null) {
            player.location = this.se;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String southwest(Player player) {
        if (this.sw != null) {
            player.location = this.sw;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String northwest(Player player) {
        if (this.nw != null) {
            player.location = this.nw;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String up(Player player) {
        if (this.u != null) {
            player.location = this.u;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String down(Player player) {
        if (this.d != null) {
            player.location = this.d;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String in(Player player) {
        if (this.i != null) {
            player.location = this.i;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
    public String out(Player player) {
        if (this.o != null) {
            player.location = this.o;
            player.look(null, null);
            return "";
        }
        return "You can't go that way!\n";
    }
}


class Item extends Thing {

    public Item(String[] names, String description, int capacity) {
        this.names = names;
        this.description = description;
        this.contents = new Thing[capacity];
        this.capacity = capacity;
        
        this.container = false;
        this.open = false;
        this.deadly = false;
        this.silent = false;
        this.takeable = true;
    }    
}


class Player extends Thing {
    
    Room location;
    Game game;
    double bac;

    public Player(Room location, String description, int capacity) {
        this.contents = new Thing[capacity];
        this.capacity = capacity;
        this.names = new String[] {"self", "me", "player", "myself"};
        this.description = description;
        this.location = location;

        this.bac = 0.0;

        // this.container = true;
        // this.open = true;
        this.silent = true;
    }

    public void handleInput(Action action) throws Exception {

        Method verb = Player.class.getMethod(action.verb.names[0], Thing.class, Thing.class);

        if (action.direct == null) {
            System.out.println(verb.invoke(this, action.direct, action.indirect));
        }
        else if (action.direct.length == 1) {
            if (this.canAccess(action.direct[0])) {
                System.out.println(verb.invoke(this, action.direct[0], action.indirect));
            } else {
                System.out.println("You can't see any " + action.direct[0].names[0] + " here!");
            }
        }
        else {
            for (int i=0; i<action.direct.length; i++) {
                if (this.canAccess(action.direct[i])) {
                    System.out.print(action.direct[i].names[0] + ": ");
                    System.out.println(verb.invoke(this, action.direct[i], action.indirect));
                } else {
                    System.out.println("You can't see any " + action.direct[i].names[0] + " here!");
                }
            }
        }

        // this.doTurn(this.game.turns);
        return;
    }

    public boolean canAccess(Thing target) {
        ArrayList<Thing> ans = this.location.getAccessibleContents();
        ans.addAll(this.getAccessibleContents());
        Thing[] accessible = ans.toArray(new Thing[0]);

        if (target == this) {
            return true;
        }

        for (int i=0; i<accessible.length; i++) {
            if (accessible[i] == target) {
                return true;
            }
        }
        return false;
    }

    // public boolean holding(Thing target) {
    //     for (int i=0; i<this.contents.length; i++) {
    //         if (target == this.contents[i]) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    public String hello(Thing direct, Thing indirect) {
        return "Hello!";
    }

    public String look(Thing direct, Thing indirect) {
        if (direct == null) {
            return this.location.toString();
        }
        String ans = "";
        ans += direct.description + " ";
        if (!direct.container) {
            return ans;
        }
        if (direct.open) {
            if (direct.isEmpty()) {
                return ans + "It is open, but empty.";
            }
            return ans + "It is open, revealing:" + direct.listContents().replaceAll("\n", "\n    ");
        }
        return ans + "It is closed.";
    }

    public String inventory(Thing direct, Thing indirect) {
        if (this.isEmpty()) {
            return "You are empty-handed.";
        }
        return "You are holding:" + this.listContents().replaceAll("\n", "\n    ");
    }

    public String take(Thing direct, Thing indirect) {
        if (!this.hasRoom()) {
            return "You can't carry any more.";
        }
        if (this.contains(direct)) {
            return "You are already holding the " + direct.names[0] + ".";
        }
        return direct.getTaken(this);  // This uses getter/setter style logic to make overriding easier for custom take behavior.
    }

    public String drop(Thing direct, Thing indirect) {
        if (!this.contains(direct)) {
            return "You're not holding the " + direct.names[0] + "!";
        }
        if (indirect == null) {
            return direct.getDropped(this, null);
        }
        if (indirect == direct) {
            return "Don't be silly.";
        }
        if (!indirect.container) {
            return "You are not clever enough to put the " + direct.names[0] + " inside of the " + indirect.names[0] + ".";
        }
        if (!indirect.open) {
            return "The " + indirect.names[0] + " is closed.";
        }
        if (!indirect.hasRoom()) {
            return "The " + indirect.names[0] + " is full.";
        }
        return direct.getDropped(this, indirect);
    }

    public String open(Thing direct, Thing indirect) {
        // if (!direct.container) {
        //     System.out.println("Good luck with that!");
        //     return;
        // }
        // if (direct.open) {
        //     System.out.println("The " + direct.names[0] + " is already open.");
        //     return;
        // }
        return direct.getOpened();
    }

    public String close(Thing direct, Thing indirect) {
        // if (!direct.container) {
        //     System.out.println("Good luck with that!");
        //     return;
        // }
        // if (!direct.open) {
        //     System.out.println("The " + direct.names[0] + " is already closed.");
        //     return;
        // }
        return direct.getClosed();
    }

    public String hit(Thing direct, Thing indirect) {
        if (!this.contains(indirect)) {
            return "You're not holding the " + indirect.names[0] + "!";
        }
        return direct.getHit(indirect);
    }

    public String chuck(Thing direct, Thing indirect) {
        if (!this.contains(direct)) {
            return "You're not holding the " + direct.names[0] + "!";
        }
        if (!this.canAccess(indirect)) {
            return "You can't see any " + indirect.names[0] + " here!";
        }
        if (direct == indirect) {
            return "Don't be silly.";
        }
        return indirect.getThrownAt(this, direct);
    }

    public String smell(Thing direct, Thing indirect) {
        return direct.getSmelled();
    }

    public String eat(Thing direct, Thing indirect) {
        if (!this.contains(direct)) {
            return "You're not holding the " + direct.names[0] + "!";
        }
        return direct.getEaten();
    }

    public String drink(Thing direct, Thing indirect) {
        if (!this.contains(direct)) {
            return "You're not holding the " + direct.names[0] + "!";
        }
        return direct.getDrank(this);
    }

    public String read(Thing direct, Thing indirect) {
        return direct.getRead();
    }

    public String jump(Thing direct, Thing indirect) {
        // if (direct == null) {
            return "Weeee!";
            // return;
        // }
        // System.out.prinln(direct.getJumped());
    }

    public String kiss(Thing direct, Thing indirect) {
        return direct.getKissed();
    }

    public String hug(Thing direct, Thing indirect) {
        return direct.getHugged();
    }

    public String listen(Thing direct, Thing indirect) {
        if (direct == null) {
            return this.location.getListened();
        }
        return direct.getListened();
    }

    public String sing(Thing direct, Thing indirect) {
        return "You're in fine voice today!";
    }

    public String sleep(Thing direct, Thing indirect) {
        return "Zzzzzzzz...";
    }

    public String sorry(Thing direct, Thing indirect) {
        if (direct == null) {
            return "I forgive you.";
        }
        return direct.getSorried();
    }

    public String shout(Thing direct, Thing indirect) {
        return "Aaaarrrrgggghhhh!";
    }

    public String taste(Thing direct, Thing indirect) {
        return direct.getTasted();
    }

    public String touch(Thing direct, Thing indirect) {
        return direct.getTouched();
    }

    public String lock(Thing direct, Thing indirect) {
        if (!this.contains(indirect)) {
            return "You're not holding the " + indirect.names[0] + "!";
        }
        return direct.getLocked(indirect);
    }

    public String unlock(Thing direct, Thing indirect) {
        if (!this.contains(indirect)) {
            return "You're not holding the " + indirect.names[0] + "!";
        }
        return direct.getUnlocked(indirect);
    }

    public String north (Thing direct, Thing indirect) {return this.location.north(this);}
    public String south(Thing direct, Thing indirect) {return this.location.south(this);}
    public String east(Thing direct, Thing indirect) {return this.location.east(this);}
    public String west(Thing direct, Thing indirect) {return this.location.west(this);}
    public String northeast(Thing direct, Thing indirect) {return this.location.northeast(this);}
    public String southeast(Thing direct, Thing indirect) {return this.location.southeast(this);}
    public String southwest(Thing direct, Thing indirect) {return this.location.southwest(this);}
    public String northwest(Thing direct, Thing indirect) {return this.location.northwest(this);}
    public String up(Thing direct, Thing indirect) {return this.location.up(this);}
    public String down(Thing direct, Thing indirect) {return this.location.down(this);}
    public String in(Thing direct, Thing indirect) {return this.location.in(this);}
    public String out(Thing direct, Thing indirect) {return this.location.out(this);}
}


class NPC extends Thing {

    char gender;

    public NPC(String[] names, String description, int capacity) {
        this.description = description;
        this.names = names;
        this.contents = new Thing[capacity];
    }

    @Override
    public void doTurn(int turns) {
        this.handleMovement(turns);
        super.doTurn(turns);
    }

    public void handleMovement(int Turns) {

    }

    @Override
    public String getTaken(Player player) {
        if (this.gender == 'm') {
            return "He seems unwilling to come with you.";
        }
        return "She seems unwilling to come with you.";
    }

    @Override
    public String getSmelled() {
        return "That's bad manners.";
    }

    @Override
    public String getRead() {
        return "Reading people was never your strong suit.";
    }

    @Override
    public String getListened() {
        if (this.gender == 'm') {
            return "You hear him breathing.";
        }
        return "You hear her breathing.";
    }

    @Override
    public String getTasted() {
        if (this.gender == 'm') {
            return "He backs away from you, looking uncomfortable.";
        }
        return "She backs away from you, looking uncomfortable.";
    }

    @Override
    public String getTouched() {
        if (this.gender == 'm') {
            return "He backs away from you, looking uncomfortable.";
        }
        return "She backs away from you, looking uncomfortable.";
    }
}


class Beverage extends Item {

    // double abv;
    boolean full;

    public Beverage(String[] names, String description, int capacity, boolean full) {
        super(names, description, capacity);
        // this.abv = abv;
        this.full = full;
    }

    @Override
    public String getDrank(Player player) {
        if (!this.full) {
            return "The " + this.names[0] + " is empty.";
        }
        player.bac += 0.027;
        this.full = false;
        return "Tasty!";
    }

    @Override
    public String nameForList() {
        if (this.full) {
            return super.nameForList() + " (full)";
        }
        return super.nameForList() + " (empty)";
    }
}


class Action {

    Verb verb;
    Thing[] direct;
    Thing indirect;

    public Action(Verb verb, Thing[] direct, Thing indirect) {
        this.verb = verb;
        this.direct = direct;
        this.indirect = indirect;
    }
}


class Verb implements Serializable {

    String[] names;
    boolean needsDirect;
    boolean needsIndirect;
    boolean acceptsDirect;
    boolean acceptsIndirect;
    boolean acceptsMultipleDirect;
    String[] indirectIndicator;

    public Verb(String[] names, boolean needsDirect, boolean needsIndirect, boolean acceptsDirect, boolean acceptsIndirect, boolean acceptsMultipleDirect) {//, String[] indirectIndicator) {
        this.names = names;
        this.needsDirect = needsDirect;
        this.needsIndirect = needsIndirect;
        this.acceptsDirect = acceptsDirect;
        this.acceptsIndirect = acceptsIndirect;
        this.acceptsMultipleDirect = acceptsMultipleDirect;
        // this.indirectIndicator = indirectIndicator;
    }
}


class Game implements Serializable {

    Verb[] verbs;
    Thing[] items;
    Room[] rooms;
    Parser parser;
    Player player;

    int turns;

    public Game(Verb[] verbs, Thing[] items, Room[] rooms, Parser parser, Player player) {
        this.verbs = verbs;
        this.items = items;
        this.rooms = rooms;
        this.parser = parser;
        this.player = player;
        this.turns = 0;
    }

    public String time() {
        int min = this.turns * 5 + 360;
        int hour = min / 60;
        min -= hour*60;
        hour = hour % 24;
        
        String strMin = String.valueOf(min);
        String strHour = String.valueOf(hour);

        if (min < 10) {
            strMin = "0" + strMin;
        }
        if (hour < 10) {
            strHour = "0" + strHour;
        }

        return strHour + ":" + strMin;
    }

    public void updateTopBar() {
        System.out.print("\u001b[s");
        System.out.print("\u001b[H");
        System.out.print("\u001b[1000C");

        System.out.print("\u001b[30m\u001b[47m\u001b[1m");

        System.out.print(" " + "\u001b[1D");
        for (int i=0; i<1000; i++) {
            System.out.print(" " + "\u001b[2D");
        }

        System.out.print(this.player.location.names[0]);
        System.out.print("\u001b[1000C\u001b[5D");
        System.out.print(time());
        
        System.out.print("\u001b[18D");
        System.out.printf("BAC: %.3f", this.player.bac);
        System.out.print("%");

        System.out.print("\u001b[u");
    }

    public void free(Thing target) {
        for (int i=0; i<this.items.length; i++) {
            for (int j=0; j<this.items[i].contents.length; j++) {
                if (this.items[i].contents[j] == target) {
                    this.items[i].contents[j] = null;
                    return;
                }
            }
        }

        for (int i=0; i<this.rooms.length; i++) {
            for (int j=0; j<this.rooms[i].contents.length; j++) {
                if (this.rooms[i].contents[j] == target) {
                    this.rooms[i].contents[j] = null;
                    return;
                }
            }
        }
    }
}

