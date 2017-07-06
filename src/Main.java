import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Main {
	private static final String CONFIG_FILE="./config.properties";
	private static InputStream input = null;
	static Properties configProp = new Properties();
	List<String[]> dataList=null;
	static{
		try{
			
			input =	Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
			configProp.load(input);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public List<String> dbConnect(String db_connect_string, String db_userid, String db_password) {
		
		dataList=new ArrayList<String[]>();
		List<String> emailList=new ArrayList<String>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
			System.out.println("connected");
			Statement statement = conn.createStatement();
			String queryString = "select EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,DIRECT_MANAGER,Period_Start_Date,Period_End_Date,submitted,IT_Leader from  dbo.v_TIMESHEET_CURRENT_REPORT where submitted <> 'Finished' ";
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				List<String> tmpList=new ArrayList<String>();
				tmpList.add(rs.getString(8));
				tmpList.add(rs.getString(4));
				tmpList.add(rs.getString(2)+" "+rs.getString(3));
				tmpList.add(rs.getString(5)+" - "+rs.getString(6));
				tmpList.add(rs.getString(7));
				dataList.add(tmpList.toArray(new String[0]));
			//	System.out.println(tmpList);
				emailList.add(rs.getString(1));
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailList;
	}

	
	private static void sendFromGMail(String from, String pass, String[] to,String[] cc, String subject, String body) {
	    Properties props = System.getProperties();
	  String host = "smtp.gmail.com";

	    props.put("mail.smtp.starttls.enable", "true");

	    props.put("mail.smtp.ssl.trust", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");


	    Session session = Session.getDefaultInstance(props);
	    MimeMessage message = new MimeMessage(session);

	    try {


	        message.setFrom(new InternetAddress(from));
	        InternetAddress[] toAddress = new InternetAddress[to.length];
	        InternetAddress[] ccAddress = new InternetAddress[cc.length];

	        // To get the array of addresses
	        for( int i = 0; i < to.length; i++ ) {
	            toAddress[i] = new InternetAddress(to[i]);
	        }

	        for( int i = 0; i < toAddress.length; i++) {
	            message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	        }
	        
	        // To get the array CC addresses
	        for( int i = 0; i < cc.length; i++ ) {
	        	ccAddress[i] = new InternetAddress(cc[i]);
	        }
	        
	        for( int i = 0; i < ccAddress.length; i++) {
	        	message.addRecipient(Message.RecipientType.CC, ccAddress[i]);
	        }



	        message.setSubject(subject);
	    //    message.setText(body);
	  //      String message = "<div style=\"color:red;\">BRIDGEYE</div>";
	        message.setContent(body, "text/html; charset=utf-8");


	        Transport transport = session.getTransport("smtp");


	        transport.connect(host, from, pass);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();

	    }
	    catch (AddressException ae) {
	        ae.printStackTrace();
	    }
	    catch (MessagingException me) {
	        me.printStackTrace();
	    }
	    }
	
	public void startProcess(String body){
		Main m1=new Main();
		List<String> emailList =m1.dbConnect("jdbc:sqlserver://USPGH-ITPPM-P1", "","");
		//emailList=new ArrayList<String>();
		if(configProp.getProperty("devmode")!=null){
			System.out.println("Dev mode on");
			emailList=new ArrayList<String>();
		}
		if(configProp.getProperty("extraemail")!=null){
			emailList.add(configProp.getProperty("extraemail"));	
		}
		
		String[] stockArr = new String[emailList.size()];
		stockArr = emailList.toArray(stockArr);
		 String from = configProp.getProperty("email");
		    String pass = configProp.getProperty("password");
		    String[] to = stockArr; // list of recipient email addresses
		    //list of cc recipients
		    String[] cc=null;
		    String ccList = configProp.getProperty("cc");
		    if(ccList!=null){
			    cc = ccList.split(",");
			    }
		    String subject = configProp.getProperty("subject");
		    String bodytemplate = configProp.getProperty(body);
		    bodytemplate+="<br/>"+createHtmlTable(m1.dataList);
		    sendFromGMail(from, pass, to,cc,subject, bodytemplate);	
		    System.out.println("mail send");
	}
	
	public String createHtmlTable(List<String[]> dataList){
		
		String table="<table border='1'><tr><th>Director</th><th>Direct Manager</th><th>Delinquent Resource</th><th>Period</th><th>Status</th></tr>";
		if(dataList!=null){
			for (String[] string : dataList) {
				table+="<tr>";
				for (int i = 0; i < string.length; i++) {
					table+="<td>"+string[i]+"</td>";	
				}
				table+="</tr>";
			}
		}
		table+="</table>";
		return table;
	}
	
	public static void main(String[] a){
		new Main().startProcess("JobA");
	}
	
	
}
