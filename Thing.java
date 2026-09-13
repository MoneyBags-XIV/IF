import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;


public class Thing {
    String[] names;
    String description;
    Thing[] contents;
    int capacity;

    boolean container;
    boolean open;
    boolean lockable;
    boolean locked;
    boolean edible;
    boolean drinkable;
    boolean deadly;
    boolean silent;
    boolean takeable;

    Thing[] keepTrackOf;

    public void doTurn() {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                this.contents[i].doTurn();
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
            if (this.contents[i] != null) {
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

    public String listContents() {
        ArrayList<Thing> contents = new ArrayList<Thing>();

        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                if (!this.contents[i].silent){
                    contents.add(this.contents[i]);
                }
            }
        }

        Item[] ans = contents.toArray(new Item[0]);
        
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<ans.length; i++) {
            if ("aeiou".indexOf(ans[i].names[0].charAt(0)) != -1) {
                stringBuilder.append("\nan ");
            } else {
                stringBuilder.append("\na ");
            }
            stringBuilder.append(ans[i].toString());
            if (ans[i].container) {
                if (ans[i].open) {
                    stringBuilder.append(" (open)");
                    if (!ans[i].isEmpty()) {
                        stringBuilder.append("\nThe " + ans[i].toString() + " contains:");
                    }
                    stringBuilder.append(ans[i].listContents().replaceAll("\n", "\n    "));
                } else {
                    stringBuilder.append(" (closed)");
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
        player.game.free(this);
        player.addToContents(this);
        return "Taken.";
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
        this.open = true;
        return "Ok, the " + this.names[0] + " is now open.";
    }

    public String getClosed() {
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
        this.contents = new Item[capacity];
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "\u001B[1m" + this.names[0] + "\u001B[22m" + '\n' + this.description;
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
        this.contents = new Item[capacity];
        this.capacity = capacity;
        
        this.container = false;
        this.open = false;
        this.lockable = false;
        this.locked = false;
        this.edible = false;
        this.drinkable = false;
        this.deadly = false;
        this.silent = false;
        this.takeable = true;
    }    
}


class Player extends Thing {
    
    Room location;
    Game game;

    public Player(Room location, String description, int capacity) {
        this.contents = new Item[capacity];
        this.capacity = capacity;
        this.names = new String[] {"self", "me", "player", "myself"};
        this.description = description;
        this.location = location;

        // this.container = true;
        // this.open = true;
        this.silent = true;
    }

    public void handleInput(Action action) throws Exception {

        Method verb = Player.class.getMethod(action.verb.names[0], Thing.class, Thing.class);

        if (action.direct == null) {
            verb.invoke(this, action.direct, action.indirect);
        }
        else if (action.direct.length == 1) {
            if (this.canAccess(action.direct[0])) {
                verb.invoke(this, action.direct[0], action.indirect);
            } else {
                System.out.println("You can't see any " + action.direct[0].names[0] + " here!");
            }
        }
        else {
            for (int i=0; i<action.direct.length; i++) {
                if (this.canAccess(action.direct[i])) {
                    System.out.print(action.direct[i].names[0] + ": ");
                    verb.invoke(this, action.direct[i], action.indirect);
                } else {
                    System.out.println("You can't see any " + action.direct[i].names[0] + " here!");
                }
            }
        }

        this.doTurn();
        return;
    }

    public boolean canAccess(Thing target) {
        ArrayList<Thing> ans = this.location.getAccessibleContents();
        ans.addAll(this.getAccessibleContents());
        Thing[] accessible = ans.toArray(new Item[0]);

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

    public boolean holding(Thing target) {
        for (int i=0; i<this.contents.length; i++) {
            if (target == this.contents[i]) {
                return true;
            }
        }
        return false;
    }

    public void look(Thing direct, Thing indirect) {
        if (direct == null) {
            System.out.println(this.location.toString());
            if (!this.location.isEmpty())
                System.out.println("Here you see:" + this.location.listContents().replaceAll("\n", "\n    "));
            return;
        }
        System.out.print(direct.description + " ");
        if (!direct.container) {
            System.out.println();
            return;
        }
        if (direct.open) {
            if (direct.isEmpty()) {
                System.out.println("It is open, but empty.");
                return;
            }
            System.out.println("It is open, revealing:" + direct.listContents().replaceAll("\n", "\n    "));
            return;
        }
        System.out.println("It is closed.");
        return;
    }

    public void inventory(Thing direct, Thing indirect) {
        if (this.isEmpty()) {
            System.out.println("You are empty-handed.");
            return;
        }
        System.out.println("You are holding:" + this.listContents().replaceAll("\n", "\n    "));
    }

    public void take(Thing direct, Thing indirect) {
        if (!this.hasRoom()) {
            System.out.println("You can't carry any more.");
            return;
        }
        if (this.holding(direct)) {
            System.out.println("You are already holding the " + direct.names[0] + ".");
            return;
        }
        if (!direct.takeable) {
            System.out.println("Good luck with that!");
            return;
        }
        System.out.println(direct.getTaken(this));  // This uses getter/setter style logic to make overriding easier for custom take behavior.
        return;
    }

    public void drop(Thing direct, Thing indirect) {
        if (!this.holding(direct)) {
            System.out.println("You're not holding the " + direct.names[0] + "!");
            return;
        }
        if (indirect == null) {
            System.out.println(direct.getDropped(this, null));
            return;
        }
        if (indirect == direct) {
            System.out.println("Don't be silly.");
            return;
        }
        if (!indirect.container) {
            System.out.println("You are not clever enough to put the " + direct.names[0] + " inside of the " + indirect.names[0] + ".");
            return;
        }
        if (!indirect.open) {
            System.out.println("The " + indirect.names[0] + " is closed.");
            return;
        }
        if (!indirect.hasRoom()) {
            System.out.println("The " + indirect.names[0] + " is full.");
            return;
        }
        System.out.println(direct.getDropped(this, indirect));
    }

    public void open(Thing direct, Thing indirect) {
        if (!direct.container) {
            System.out.println("Good luck with that!");
            return;
        }
        if (direct.open) {
            System.out.println("The " + direct.names[0] + " is already open.");
            return;
        }
        System.out.println(direct.getOpened());
    }

    public void close(Thing direct, Thing indirect) {
        if (!direct.container) {
            System.out.println("Good luck with that!");
            return;
        }
        if (!direct.open) {
            System.out.println("The " + direct.names[0] + " is already closed.");
            return;
        }
        System.out.println(direct.getClosed());
    }

    public void hit(Thing direct, Thing indirect) {
        if (!this.contains(indirect)) {
            System.out.println("You're not holding the " + indirect.names[0] + "!");
            return;
        }
        System.out.println(direct.getHit(indirect));
    }

    public void chuck(Thing direct, Thing indirect) {
        if (!this.contains(direct)) {
            System.out.println("You're not holding the " + direct.names[0] + "!");
            return;
        }
        if (!this.canAccess(indirect)) {
            System.out.println("You can't see any " + indirect.names[0] + " here!");
            return;
        }
        System.out.println(indirect.getThrownAt(this, direct));
    }

    public void north (Thing direct, Thing indirect) {System.out.print(this.location.north(this));}
    public void south(Thing direct, Thing indirect) {System.out.print(this.location.south(this));}
    public void east(Thing direct, Thing indirect) {System.out.print(this.location.east(this));}
    public void west(Thing direct, Thing indirect) {System.out.print(this.location.west(this));}
    public void northeast(Thing direct, Thing indirect) {System.out.print(this.location.northeast(this));}
    public void southeast(Thing direct, Thing indirect) {System.out.print(this.location.southeast(this));}
    public void southwest(Thing direct, Thing indirect) {System.out.print(this.location.southwest(this));}
    public void northwest(Thing direct, Thing indirect) {System.out.print(this.location.northwest(this));}
    public void up(Thing direct, Thing indirect) {System.out.print(this.location.up(this));}
    public void down(Thing direct, Thing indirect) {System.out.print(this.location.down(this));}
    public void in(Thing direct, Thing indirect) {System.out.print(this.location.in(this));}
    public void out(Thing direct, Thing indirect) {System.out.print(this.location.out(this));}
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


class Verb {

    String[] names;
    boolean needsDirect;
    boolean needsIndirect;
    boolean acceptsDirect;
    boolean acceptsIndirect;
    boolean acceptsMultipleDirect;
    String[] indirectIndicator;

    public Verb(String[] names, boolean needsDirect, boolean needsIndirect, boolean acceptsDirect, boolean acceptsIndirect, boolean acceptsMultipleDirect, String[] indirectIndicator) {
        this.names = names;
        this.needsDirect = needsDirect;
        this.needsIndirect = needsIndirect;
        this.acceptsDirect = acceptsDirect;
        this.acceptsIndirect = acceptsIndirect;
        this.acceptsMultipleDirect = acceptsMultipleDirect;
        this.indirectIndicator = indirectIndicator;
    }
}


class Game {

    Verb[] verbs;
    Thing[] items;
    Room[] rooms;
    Parser parser;
    Player player;

    public Game(Verb[] verbs, Thing[] items, Room[] rooms, Parser parser, Player player) {
        this.verbs = verbs;
        this.items = items;
        this.rooms = rooms;
        this.parser = parser;
        this.player = player;
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

