package fjy.ins.model;

public class Info
{
	public String imgUrl;
	public String videoUrl;
	public String title;
	public String author;
	public String path;
	public String summary;
	public int type;
	
	public void setImgUrl(String u){
		this.imgUrl = u;
	}
	
	public void setVideoUrl(String ur){
		this.videoUrl = ur;
	}
	
	public void setTitle(String t){
		this.title = t;
	}
	
	public void setAuthor(String a){
		this.author = a;
	}
	
	public void setPath(String p){
		this.path = p;
	}
	
	public void setSum(String s){
		this.summary = s;
	}
	
	public void setType(int i){
		this.type = i;
	}
	
	public int getType(){
		return type;
	}
	
	public String getImgUrl(){
		return imgUrl;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getVideoUrl(){
		return videoUrl;
	}
	
	public String getAuthor(){
		return author;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getSum(){
		return summary;
	}
}
