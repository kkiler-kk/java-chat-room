package net.kk.chat.entity;

public class Message {
    private String sendName; //发送者
    private String type; //消息类型
    private String createDate; //发送消息时间
    private String text;  //内容
    private String receiveName; //接收者
    private String url; //发送者图片

    @Override
    public String toString() {
        return "Message{" +
                "sendName='" + sendName + '\'' +
                ", type='" + type + '\'' +
                ", createDate='" + createDate + '\'' +
                ", text='" + text + '\'' +
                ", receiveName='" + receiveName + '\'' +
                '}';
    }

    public Message() {

    }

    public Message(String sendName, String type, String createDate, String text, String receiveName) {
        this.sendName = sendName;
        this.type = type;
        this.createDate = createDate;
        this.text = text;
        this.receiveName = receiveName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }
}
