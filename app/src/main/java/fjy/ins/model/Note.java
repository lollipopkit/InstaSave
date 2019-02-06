package fjy.ins.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private String time;
	private String size;
	private String path;
	private String url;

    public int getId() {
        return id;
    }
	
	public String getSize(){
		return size;
	}
	
	public void setSize(String si){
		this.size = si;
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
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
}
