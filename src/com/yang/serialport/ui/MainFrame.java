package com.yang.serialport.ui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.yang.serialport.manager.SerialPortManager;
import com.yang.serialport.utils.ByteUtils;
import com.yang.serialport.utils.ShowUtils;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;


/**
 * 主界面
 * 
 * @author yangle
 */
@SuppressWarnings("all")
public class MainFrame extends JFrame {

	// 程序界面宽度
	public final int WIDTH = 720;
	// 程序界面高度
	public final int HEIGHT = 850;

	// 数据显示区
	private JTextArea mDataView = new JTextArea();
	private JTextArea mData   = new JTextArea();
	private JScrollPane mScrollDataView = new JScrollPane(mDataView);

	// 串口设置面板
	private JPanel mSerialPortPanel = new JPanel();
	private JLabel mSerialPortLabel = new JLabel("串口");
	private JLabel mBaudrateLabel = new JLabel("波特率");
	private JLabel	set = new JLabel("请需要输入加热温度");
	private JComboBox mCommChoice = new JComboBox();
	private JComboBox mBaudrateChoice = new JComboBox();
	private ButtonGroup mDataChoice = new ButtonGroup();
	private JRadioButton mDataASCIIChoice = new JRadioButton("ASCII", true);
	private JRadioButton mDataHexChoice = new JRadioButton("Hex");

	// 操作面板
	private JPanel mOperatePanel = new JPanel();
	private JTextArea mDataInput = new JTextArea();
	private JButton mSerialPortOperate = new JButton("打开串口");
	private JButton mSendData = new JButton("发送数据");
	private JButton stop = new JButton("停止加热");
	private JButton report = new JButton("生成报告");
	private JButton start = new JButton("开始加热");
	
	
	dp lo = new dp();
	logo dp = new logo();

	
	// 串口列表
	private List<String> mCommList = null;
	// 串口对象
	private SerialPort mSerialport;

	public MainFrame() {
		initView();
		initComponents();
		actionListener();
		initData();
	}

	/**
	 * 初始化窗口
	 */
	
	
	private void initView() {
		// 关闭程序
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		// 禁止窗口最大化
		setResizable(false);

		// 设置程序窗口居中显示
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);

		setTitle("卓越水温控制系统 Designed By Reyunn");
	}

	/**
	 * 初始化控件
	 */
	private void initComponents() {

		
		dp.setBounds(5, 5, 720, 360);
		add(dp);
		// 串口设置
		mSerialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		mSerialPortPanel.setBounds(470, 390, 170, 160);
		mSerialPortPanel.setLayout(null);
		add(mSerialPortPanel);

		mSerialPortLabel.setForeground(Color.gray);
		mSerialPortLabel.setBounds(10, 25, 40, 20);
		mSerialPortPanel.add(mSerialPortLabel);

		mCommChoice.setFocusable(false);
		mCommChoice.setBounds(60, 25, 100, 20);
		mSerialPortPanel.add(mCommChoice);

		mBaudrateLabel.setForeground(Color.gray);
		mBaudrateLabel.setBounds(10, 60, 40, 20);
		mSerialPortPanel.add(mBaudrateLabel);

		mBaudrateChoice.setFocusable(false);
		mBaudrateChoice.setBounds(60, 60, 100, 20);
		mSerialPortPanel.add(mBaudrateChoice);

		mDataASCIIChoice.setBounds(20, 95, 55, 20);
		mDataHexChoice.setBounds(95, 95, 55, 20);
		mDataChoice.add(mDataASCIIChoice);
		mDataChoice.add(mDataHexChoice);
		mSerialPortPanel.add(mDataASCIIChoice);
		mSerialPortPanel.add(mDataHexChoice);
		
		mSerialPortOperate.setFocusable(false);
		mSerialPortOperate.setBounds(40, 120, 90, 20);
		mSerialPortPanel.add(mSerialPortOperate);

		// 操作
	mOperatePanel.setBorder(BorderFactory.createTitledBorder("操作"));
	mOperatePanel.setBounds(470,560, 170, 230);
	mOperatePanel.setLayout(null);
	add(mOperatePanel);
	
	stop.setBounds(10, 30, 150, 30);
	mOperatePanel.add(stop);
	
	report.setBounds(10, 70, 150, 30);
	mOperatePanel.add(report);
	
	set.setForeground(Color.gray);
	set.setBounds(25, 130, 170, 20);	
	mOperatePanel.add(set);
	
	mData.setBounds(10, 155, 150, 20);
	mOperatePanel.add(mData);
	start.setBounds(10, 190, 150, 30);
	mOperatePanel.add(start);
		
		lo.setBorder(BorderFactory.createTitledBorder("温度监控"));
		lo.setBounds(20, 390, 400, 400);
		add(lo);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mCommList = SerialPortManager.findPorts();
		// 检查是否有可用串口，有则加入选项中
		if (mCommList == null || mCommList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : mCommList) {
				mCommChoice.addItem(s);
			}
		}

		mBaudrateChoice.addItem("9600");
		mBaudrateChoice.addItem("19200");
		mBaudrateChoice.addItem("38400");
		mBaudrateChoice.addItem("57600");
		mBaudrateChoice.addItem("115200");
	}

	/**
	 * 按钮监听事件
	 */
	private void actionListener() {
		// 串口
		mCommChoice.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				mCommList = SerialPortManager.findPorts();
				// 检查是否有可用串口，有则加入选项中
				if (mCommList == null || mCommList.size() < 1) {
					ShowUtils.warningMessage("没有搜索到有效串口！");
				} else {
					int index = mCommChoice.getSelectedIndex();
					mCommChoice.removeAllItems();
					for (String s : mCommList) {
						mCommChoice.addItem(s);
					}
					mCommChoice.setSelectedIndex(index);
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// NO OP
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// NO OP
			}
		});

		// 打开|关闭串口
		mSerialPortOperate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("打开串口".equals(mSerialPortOperate.getText()) && mSerialport == null) {
					openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
			}
		});

		// 发送数据
		mSendData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendData(e);
			}
		});
	}

	/**
	 * 打开串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void openSerialPort(java.awt.event.ActionEvent evt) {
		// 获取串口名称
		String commName = (String) mCommChoice.getSelectedItem();
		// 获取波特率，默认为9600
		int baudrate = 9600;
		String bps = (String) mBaudrateChoice.getSelectedItem();
		baudrate = Integer.parseInt(bps);

		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				mSerialport = SerialPortManager.openPort(commName, baudrate);
				if (mSerialport != null) {
					mDataView.setText("串口已打开" + "\r\n");
					mSerialPortOperate.setText("关闭串口");
				}
			} catch (PortInUseException e) {
				ShowUtils.warningMessage("串口已被占用！");
			}
		}

		// 添加串口监听
		SerialPortManager.addListener(mSerialport, new SerialPortManager.DataAvailableListener() {

			@Override
			public void dataAvailable() {
				byte[] data = null;
				try {
					if (mSerialport == null) {
						ShowUtils.errorMessage("串口对象为空，监听失败！");
					} else {
						// 读取串口数据
						data = SerialPortManager.readFromPort(mSerialport);

						// 以字符串的形式接收数据
						if (mDataASCIIChoice.isSelected()) {
							mDataView.append(new String(data) + "\r\n");
							lo.getInfo(new String(data));
							lo.repaint();
						}

						// 以十六进制的形式接收数据
						if (mDataHexChoice.isSelected()) {
							mDataView.append(ByteUtils.byteArrayToHexString(data) + "\r\n");
						}
					}
				} catch (Exception e) {
					ShowUtils.errorMessage(e.toString());
					// 发生读取错误时显示错误信息后退出系统
					System.exit(0);
				}
			}
		});
	}

	/**
	 * 关闭串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.closePort(mSerialport);
		mDataView.setText("串口已关闭" + "\r\n");
		mSerialPortOperate.setText("打开串口");
		mSerialport = null;
	}

	/**
	 * 发送数据
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void sendData(java.awt.event.ActionEvent evt) {
		// 待发送数据
		String data = mDataInput.getText().toString();

		if (mSerialport == null) {
			ShowUtils.warningMessage("请先打开串口！");
			return;
		}

		if ("".equals(data) || data == null) {
			ShowUtils.warningMessage("请输入要发送的数据！");
			return;
		}

		// 以字符串的形式发送数据
		if (mDataASCIIChoice.isSelected()) {
			SerialPortManager.sendToPort(mSerialport, data.getBytes());
			
		}

		// 以十六进制的形式发送数据
		if (mDataHexChoice.isSelected()) {
			SerialPortManager.sendToPort(mSerialport, ByteUtils.hexStr2Byte(data));
		}
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}



class  dp extends JPanel{
	 String TargetTemperature = "600";
	 String lowTarget = "500";
	 String llowTarget = "400";
	 int Max=600;
	 ArrayList<temp> tp = new ArrayList<temp>();
	 Date startData = null;
	 boolean op = false;
	 String i = new String ("");	 
		public void paint (Graphics g) {
		super.paint(g);
		this.pxy(g);
		this.pw(g);
		this.analyse(g);		
	}
	
	
public void analyse(Graphics g){
    if(op==false){
g.drawString("本系统尚未开启", 30, 320);
} 
else {
	Date dat = new Date();
	i=(new SimpleDateFormat("hh:mm:ss")).format(startData);
	
  g.drawString("本次加热开始时间 : "+i, 30, 320);
  long time =(dat.getTime()-startData.getTime())/1000;
  g.drawString("本次加热共持续 ：   " + time +" s", 30, 340);
 g.drawString("系统超调量 "+ (float)(Max-600)/10.00, 30, 360);
}


}

	
	
public void	getInfo(String a){
	         System.out.println(a);
	           char ch=a.charAt(0);	
			   if(ch == 'a') {
				a= a.substring(1, 4);
				System.out.println(a);
				int d = Integer.parseInt(a);
				Date date = new Date();
				temp mp = new temp(date, d);
				if(d>Max) Max=d;
				if(d>Integer.parseInt(llowTarget)) tp.add(mp);		
				if(op==false) startData=date;
				op=true;
				}
			   if(ch=='b') {
				a= a.substring(1, 4); 
				int d = Integer.parseInt(a);
				lowTarget  = Integer.toString(d-100);
				llowTarget = Integer.toString(d-200);
				TargetTemperature = a;
			   }
		
			   
	}


public void pw(Graphics g) {
	for(int i = 0;i<tp.size()-1;i++){
	g.drawLine(i*3+33,(int)(322+Integer.parseInt(llowTarget)-1.1*tp.get(i).getTemp()), i*3+36,(int)(322+Integer.parseInt(llowTarget)-1.1*tp.get(i+1).getTemp()));
	}
	}




public void pxy(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		Stroke stroke=new BasicStroke(3.0f);//设置线宽为3.0
		g2.setStroke(stroke);
		g.drawLine(30, 300, 300, 300);
		g.drawLine(30, 40, 30, 300);
		g.drawLine(30, 60, 35, 60);
		g.drawLine(30, 170, 35, 170);
		g.drawLine(30, 280, 35, 280);
		g.drawString(TargetTemperature.substring(0, 2)+"."+TargetTemperature.charAt(2), 4,60);
		g.drawString(lowTarget.substring(0, 2)+"."+lowTarget.charAt(2), 4,170);
		g.drawString(llowTarget.substring(0, 2)+"."+llowTarget.charAt(2), 4,280);
		stroke=new BasicStroke(1.0f);//设置线宽为3.0
		g2.setStroke(stroke);
		g.drawString("目标设置温度:"+TargetTemperature.substring(0, 2)+"."+TargetTemperature.charAt(2), 130,20);
	}	
}


class temp{
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	private int temp;
	private Date time;
	public temp(Date time,int temp) {
		this.setTemp(temp);
		this.setTime(time);
	}
	
}

class logo extends JPanel{
	public void paint (Graphics g) {
		super.paint(g);

  Toolkit kit = Toolkit.getDefaultToolkit();
  Image img = kit.getImage("img/1.jpg");
  g.drawImage(img, 0, 0, this);
		
	}	
}

