package com.example.yuyintest;

import java.util.ArrayList;

public class ChatInfo {

	public int bg;
	public int ed;
	public boolean ls;
	public int sn;
	public ArrayList<Ws> ws;

	public class Ws {
		public int bg;
		public ArrayList<Cw> cw;
	}

	public class Cw {
		public String w;
	}

}
