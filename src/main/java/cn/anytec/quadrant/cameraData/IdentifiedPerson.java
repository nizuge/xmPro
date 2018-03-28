package cn.anytec.quadrant.cameraData;

public class IdentifiedPerson {

    private String meta;
    private long appearTime;
    private long initTime;

    public IdentifiedPerson(String meta){
        this.meta = meta;
        this.appearTime = System.currentTimeMillis();
        this.initTime = System.currentTimeMillis();
    }

    public String getMeta() {
        return meta;
    }

    public long getAppearTime() {
        return appearTime;
    }

    public void setAppearTime(long appearTime) {
        this.appearTime = appearTime;
    }

    public long getInitTime() {
        return initTime;
    }
}
