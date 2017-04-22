package fjy.ins.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private String time;
	private String loc;
	private String color;

    public int getId() {
        return id;
    }
	
	public String getLoc(){
		return loc;
	}
	
	public void setLoc(String location){
		this.loc = location;
	}

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
	
	public void setColor(String color){
		this.color = color;
	}
	
	public String getColor(){
		return color;
	}
}
