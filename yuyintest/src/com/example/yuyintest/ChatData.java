package com.example.yuyintest;

public class ChatData {
	public String content;
	public boolean isReply;// 是不是回复
	public int iconId;

	public ChatData(String content, boolean isReply, int iconId) {
		super();
		this.content = content;
		this.isReply = isReply;
		this.iconId = iconId;
	}

	public ChatData() {
		super();
		// TODO Auto-generated constructor stub
	}

}
