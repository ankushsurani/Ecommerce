package com.eworld.helper;

public class Msg {
	
	private String content;
	
	private String type;

	public Msg(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}

