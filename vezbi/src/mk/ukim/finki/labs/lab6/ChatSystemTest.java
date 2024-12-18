package mk.ukim.finki.labs.lab6;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;


class NoSuchUserException extends Exception {
    public NoSuchUserException(String msg) {
        super(msg);
    }
}
class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String msg) {
        super(msg);
    }
}

class ChatRoom{
    public String name;
    public Set<String> users;

    public ChatRoom(String name) {
        this.name = name;
        this.users = new TreeSet<>();
    }
    public void addUser(String username){
        this.users.add(username);
    }
    public void removeUser(String username){
        this.users.remove(username);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        if(!users.isEmpty())
        {
            users.forEach(user -> sb.append(user).append("\n"));
        }else{
            sb.append("EMPTY\n");
        }

        return sb.toString();
    }
    public boolean hasUser(String username){
        return this.users.contains(username);
    }
    public int numUsers(){
        return this.users.size();
    }
}
class ChatSystem{
    Map<String, ChatRoom> rooms;
    Set<String> users;
    public ChatSystem() {
        rooms=new TreeMap<>();
        users=new HashSet<>();

    }
    public void register(String userName){
        this.users.add(userName);
        List<ChatRoom> chatRooms = new ArrayList<>(this.rooms.values());
        if(chatRooms.isEmpty()){
            return;
        }
        chatRooms.sort(Comparator.comparing(ChatRoom::numUsers).thenComparing(c->c.name));
        this.rooms.get(chatRooms.get(0).name).addUser(userName);
    }
    public void registerAndJoin(String userName, String roomName){
        this.users.add(userName);
        this.rooms.get(roomName).addUser(userName);
    }
    public void joinRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if(rooms.containsKey(roomName)){
            ChatRoom room = rooms.get(roomName);
            if(users.contains(userName)){
                room.addUser(userName);
            }else{
                throw new NoSuchUserException(String.format("Error na user: %s", userName));
            }
        }else{
            throw new NoSuchRoomException(String.format("Error na soba %s", roomName));

        }

    }
    public void leaveRoom(String username, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if(rooms.containsKey(roomName)){
            ChatRoom room = rooms.get(roomName);
            if(users.contains(username)){
                room.removeUser(username);
            }else{
                throw new NoSuchUserException(String.format("Error na user: %s", username));
            }
        }else{
            throw new NoSuchRoomException(String.format("Error na soba %s", roomName));

        }
    }
    public void followFriend(String username, String friend_username) throws NoSuchUserException {
        if(!users.contains(username))
        {
            throw new NoSuchUserException(String.format("Error na user: %s", username));
        }
        for (ChatRoom value : rooms.values()) {
            if(value.hasUser(friend_username)){
                value.addUser(username);
            }
        }
        
    }
    public void addRoom(String roomName){
        this.rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }
    public void removeRoom(String roomName){
        this.rooms.remove(roomName);
    }
    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if(!rooms.containsKey(roomName)){
            throw new NoSuchRoomException(String.format("Error na soba %s", roomName));
        }else{
            return rooms.get(roomName);
        }
    }
}

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            if (n != 5) {
                System.out.println();
            }
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }
    }

}