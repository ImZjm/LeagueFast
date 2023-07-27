package imzjm.league.data;

public class AppFunctionStatus {
    private boolean autoPick = false;

    private int autoPickedChamp = -1;

    private boolean autoAccept = false;

    private int autoBanChamp = -1;

    private boolean autoBan = false;

    private static final AppFunctionStatus INSTANCE = new AppFunctionStatus();

    private AppFunctionStatus(){}

    public static AppFunctionStatus getINSTANCE() {
        return INSTANCE;
    }

    public boolean isAutoPick() {
        return autoPick;
    }

    public void setAutoPick(boolean autoPick) {
        this.autoPick = autoPick;
    }

    public int getAutoPickedChamp() {
        return autoPickedChamp;
    }

    public void setAutoPickedChamp(int autoPickedChamp) {
        this.autoPickedChamp = autoPickedChamp;
    }

    public boolean isAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
    }

    public int getAutoBanChamp() {
        return autoBanChamp;
    }

    public void setAutoBanChamp(int autoBanChamp) {
        this.autoBanChamp = autoBanChamp;
    }

    public boolean isAutoBan() {
        return autoBan;
    }

    public void setAutoBan(boolean autoBan) {
        this.autoBan = autoBan;
    }
}
