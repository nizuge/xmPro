package cn.anytec.quadrant.findface;

public class IdentifyFace {
    private String faceCoordinates;
    private long id;
    private double confidence;
    private long personId;
    private String galleries;
    private String meta;
    private boolean friendOrFoe;
    private byte[] normalizedPhoto;
    private String timestamp;
    private int interviewTimes;
    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getGalleries() {
        return galleries;
    }

    public void setGalleries(String galleries) {
        this.galleries = galleries;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public boolean isFriendOrFoe() {
        return friendOrFoe;
    }

    public void setFriendOrFoe(boolean friendOrFoe) {
        this.friendOrFoe = friendOrFoe;
    }

    public byte[] getNormalizedPhoto() {
        return normalizedPhoto;
    }

    public void setNormalizedPhoto(byte[] normalizedPhoto) {
        this.normalizedPhoto = normalizedPhoto;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getInterviewTimes() {
        return interviewTimes;
    }

    public void setInterviewTimes(int interviewTimes) {
        this.interviewTimes = interviewTimes;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public String getFaceCoordinates() {
        return faceCoordinates;
    }

    public void setFaceCoordinates(String faceCoordinates) {
        this.faceCoordinates = faceCoordinates;
    }
}
