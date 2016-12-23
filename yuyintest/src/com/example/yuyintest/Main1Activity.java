package com.example.yuyintest;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yuyintest.ChatInfo.Cw;
import com.example.yuyintest.ChatInfo.Ws;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class Main1Activity extends Activity {

	private ListView lvChat;
	private ArrayList<ChatData> mDatas;

	private final static int[] pics = new int[] { R.drawable.p1, R.drawable.p2,
			R.drawable.p3, R.drawable.p4, R.drawable.p5 };
	private final static String[] answers = new String[] { "最后一张", "真的没有了",
			"约吗", "流氓", "中午吃什么", "我就喜欢程序员" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		sb = new StringBuffer();
		gson = new Gson();
		SpeechUtility.createUtility(getApplicationContext(),
				SpeechConstant.APPID + "=5839464d");
		lvChat = (ListView) findViewById(R.id.lv_chat);
		mDatas = new ArrayList<ChatData>();
		chatAdapter = new ChatAdapter();
		lvChat.setAdapter(chatAdapter);
	}

	public void sayClick(View v) {
		showDialog();// 弹出语音
	}

	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

		@Override
		public void onResult(RecognizerResult arg0, boolean isLast) {
			String resultString = arg0.getResultString();

			ChatInfo info = gson.fromJson(resultString, ChatInfo.class);
			// {"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[{"sc":0.00,"w":"中午"}]},{"bg":0,"cw":[{"sc":0.00,"w":"吃"}]},{"bg":0,"cw":[{"sc":0.00,"w":"烤鸭"}]}]}
			// {"sn":2,"ls":true,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[{"sc":0.00,"w":"。"}]}]}
			ArrayList<Ws> ws = info.ws;
			for (Ws ws1 : ws) {
				ArrayList<Cw> cw = ws1.cw;
				for (Cw cw2 : cw) {
					String content = cw2.w;
					sb.append(content);
				}
			}
			if (isLast) {
				String content = sb.toString();
				// System.out.println("resultString=" + sb.toString());
				// 发送的消息添加到集合里
				ChatData chatData = new ChatData(content, false, -1);
				mDatas.add(chatData);
				chatAdapter.notifyDataSetChanged();

				// 回复数据
				ChatData replyData = null;
				if (content.contains("你好")) {
					replyData = new ChatData("我很好", true, -1);
				} else if (content.contains("你是谁")) {
					replyData = new ChatData("我是你的小助手", true, -1);
				} else if (content.contains("美女")) {
					int replyCntentIndex = new Random().nextInt(answers.length);
					int replyPicIndex = new Random().nextInt(pics.length);
					replyData = new ChatData(answers[replyCntentIndex], true,
							pics[replyPicIndex]);
				} else if (content.contains("天王盖地虎")) {
					replyData = new ChatData("汗滴禾下土", true, -1);
				} else {
					replyData = new ChatData("你大声点", true, -1);
				}
				mDatas.add(replyData);
				chatAdapter.notifyDataSetChanged();
				// 滑到最后
				lvChat.setSelection(mDatas.size() - 1);
				yuyinhecheng(replyData.content);
				sb = new StringBuffer();
			}
		}

		@Override
		public void onError(SpeechError arg0) {

		}
	};
	private Gson gson;
	private StringBuffer sb;
	private ChatAdapter chatAdapter;

	private void showDialog() {
		// 1.创建RecognizerDialog对象
		RecognizerDialog mDialog = new RecognizerDialog(this, null);
		// 2.设置accent、language等参数
		mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 若要将UI控件用于语义理解， 必须添加以下参数设置， 设置之后onResult回调返回将是语义理解
		// 结果
		// mDialog.setParameter("asr_sch", "1");
		// mDialog.setParameter("nlp_version", "2.0");
		// 3.设置回调接口
		mDialog.setListener(mRecognizerDialogListener);
		// 4.显示dialog，接收语音输入
		mDialog.show();
	}

	private void yuyinhecheng(String content) {
		// 1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
		SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(
				getApplicationContext(), null);
		// 2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
		// 设置发音人（更多在线发音人，用户可参见 附录13.2
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaorong"); // 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "100");// 设置音量，范围 0~100
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
		// 设置合成音频保存位置（可自定义保存位置） ，保存在“./sdcard/iflytek.pcm”
		// 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
		// 仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// "./sdcard/iflytek.pcm");
		// 3.开始合成
		mTts.startSpeaking(content, null);
	}

	private class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mDatas.size();
		}

		@Override
		public ChatData getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_chat, null);
				viewHolder.rlSay = (RelativeLayout) convertView
						.findViewById(R.id.rl_say);
				viewHolder.tvSay = (TextView) convertView
						.findViewById(R.id.tv_say);
				viewHolder.llReply = (LinearLayout) convertView
						.findViewById(R.id.ll_reply);
				viewHolder.tvReply = (TextView) convertView
						.findViewById(R.id.tv_reply);
				viewHolder.ivReply = (ImageView) convertView
						.findViewById(R.id.iv_reply);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			ChatData chatData = getItem(position);
			if (chatData.isReply) {
				// 是回复
				viewHolder.llReply.setVisibility(View.VISIBLE);
				viewHolder.rlSay.setVisibility(View.GONE);
				if (chatData.iconId == -1) {
					// 没有图片
					viewHolder.ivReply.setVisibility(View.GONE);
				} else {
					// 有图片
					viewHolder.ivReply.setVisibility(View.VISIBLE);
					viewHolder.ivReply.setImageResource(chatData.iconId);
				}
				viewHolder.tvReply.setText(chatData.content);
			} else {
				// 发送
				viewHolder.llReply.setVisibility(View.GONE);
				viewHolder.rlSay.setVisibility(View.VISIBLE);
				viewHolder.tvSay.setText(chatData.content);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		RelativeLayout rlSay;
		TextView tvSay;
		LinearLayout llReply;
		TextView tvReply;
		ImageView ivReply;
	}
}
