package pentris.game;

public class Username {
    private String name = "";
    private String botName = "";

    private static Username username;

    public static Username getInstance(){
        if (username == null){
            username = new Username();
        }
        return username;
    }

    private Username(){
    }

    public void setUsername(String username){
        this.name = username;
    }

    public String getUsername(){
        return this.name;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }
}
