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
        }
        if (direct.takeable) {
            this.game.free(direct);
            this.addToContents(direct);
            System.out.println("Taken.");
            return;
        }
        System.out.println("Good luck with that!");
    }

    public void drop(Thing direct, Thing indirect) {
        if (!this.holding(direct)) {
            System.out.println("You're not holding the " + direct.names[0] + "!");
            return;
        }
        if (indirect == null) {
            this.game.free(direct);
            this.location.addToContents(direct);
            System.out.println("Dropped.");
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
        this.game.free(direct);
        indirect.addToContents(direct);
        System.out.println("Ok, the " + direct.names[0] + " is now in the " + indirect.names[0] + ".");
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
        direct.open = true;
        System.out.println("Ok, the " + direct.names[0] + " is now open.");
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
        direct.open = false;
        System.out.println("Ok, the " + direct.names[0] + " is now closed.");
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

