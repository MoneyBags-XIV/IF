import java.lang.reflect.Method;
import java.util.ArrayList;


public class Thing {
    String[] names;
    String description;
    Item[] contents;
    int capacity;

    public void doTurn() {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                this.contents[i].doTurn();
            }
        }
    }

    public void addToContents(Item item) {
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

    public boolean isAccessible() {
        return true;
    }

    public String listContents() {
        ArrayList<Item> contents = new ArrayList<Item>();

        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                contents.add(this.contents[i]);
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
                    stringBuilder.append(" (open)\nThe " + ans[i].toString() + " contains:");
                    stringBuilder.append(ans[i].listContents().replaceAll("\n", "\n    "));
                } else {
                    stringBuilder.append(" (closed)");
                }
            }
        }

        return stringBuilder.toString();
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

    boolean container;
    boolean open;
    boolean lockable;
    boolean locked;
    boolean edible;
    boolean drinkable;
    boolean deadly;
    boolean silent;

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
    }    
}


class Player extends Thing {
    
    Room location;

    public Player(Room location, String description, int capacity) {
        this.contents = new Item[capacity];
        this.capacity = capacity;
        this.names = new String[] {"self", "me", "player", "myself"};
        this.description = description;
        this.location = location;
    }

    public void handleInput(Action action) throws Exception {

        Method verb = Player.class.getMethod(action.verb.names[0], Thing[].class, Thing.class);

        verb.invoke(this, action.direct, action.indirect);
        
        this.doTurn();
        return;
    }

    public void look(Thing[] directs, Thing indirect) {
        if (directs == null) {
            System.out.println(this.location.toString());
            System.out.println("Here you see:" + this.location.listContents());
            return;
        }
        if (directs.length == 1) {
            System.out.println(directs[0].description);
            return;
        }
        for (int i=0; i<directs.length; i++) {
            System.out.println(directs[i].names[0] + ": " + directs[i].description);
        }
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

}

