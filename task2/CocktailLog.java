/**
 * Author: Xiaoyu Zhu
 * <p>
 * POJO for cocktail log
 * This class is created to display all the logs in jsp
 */
public class CocktailLog {
    String timestamp_request_received;
    String latency;
    String name;
    String category;
    String imageURL;
    String device_type;
    String timestamp_reply;

    //constructor to create log object
    public CocktailLog(String timestamp_request_received, String latency, String name, String category, String response_param, String device_type, String timestamp_reply) {
        this.timestamp_request_received = timestamp_request_received;
        this.latency = latency;
        this.name = name;
        this.category = category;
        this.imageURL = response_param;
        this.device_type = device_type;
        this.timestamp_reply = timestamp_reply;
    }

    //get timestamp_request_received
    public String getTimestamp_request_received() {
        return timestamp_request_received;
    }

    //set timestamp_request_received
    public void setTimestamp_request_received(String timestamp_request_received) {
        this.timestamp_request_received = timestamp_request_received;
    }

    //get latency
    public String getLatency() {
        return latency;
    }

    //set latency
    public void setLatency(String latency) {
        this.latency = latency;
    }

    //get name
    public String getName() {
        return name;
    }

    //set name
    public void setName(String name) {
        this.name = name;
    }

    //get category
    public String getCategory() {
        return category;
    }

    //set category
    public void setCategory(String category) {
        this.category = category;
    }

    //get device type
    public String getDevice_type() {
        return device_type;
    }

    //set device type
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    //get timestamp when the reply was sent back to Android
    public String getTimestamp_reply() {
        return timestamp_reply;
    }

    //set timestamp when the reply was sent back to Android
    public void setTimestamp_reply(String timestamp_reply) {
        this.timestamp_reply = timestamp_reply;
    }

    //get image url
    public String getImageURL() {
        return imageURL;
    }

    //set image url
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

