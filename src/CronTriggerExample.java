import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class CronTriggerExample {
	private static final String CONFIG_FILE="./config.properties";
	private static InputStream input = null;
	static Properties configProp = new Properties();
	static{
		try{
			
			input =	Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
			configProp.load(input);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
public static void main(String ar[]) throws ParseException, SchedulerException{
	
	//String cron="0/30 * * * * ?";
	 JobDetail job1 = JobBuilder.newJob(JobA.class).withIdentity("helloJob", "group1").build();
	 Trigger trigger1 = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group1").withSchedule(CronScheduleBuilder.cronSchedule(configProp.getProperty("JobSM_CRON"))).build();
	 
	 Scheduler scheduler1 = new StdSchedulerFactory().getScheduler();
     scheduler1.start();
     scheduler1.scheduleJob(job1, trigger1);

    
      JobDetail job2 = JobBuilder.newJob(JobA.class).withIdentity("helloJob2", "group2").build();
     Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("cronTrigger2", "group2").withSchedule(CronScheduleBuilder.cronSchedule(configProp.getProperty("JobSE_CRON"))).build();
     
     Scheduler scheduler2 = new StdSchedulerFactory().getScheduler();
     scheduler2.start();
     scheduler2.scheduleJob(job2, trigger2);

    JobDetail job3 = JobBuilder.newJob(JobF.class).withIdentity("helloJob3", "group3").build();
     Trigger trigger3 = TriggerBuilder.newTrigger().withIdentity("cronTrigger3", "group3").withSchedule(CronScheduleBuilder.cronSchedule(configProp.getProperty("JobF_CRON"))).build();
     
     Scheduler scheduler3 = new StdSchedulerFactory().getScheduler();
     scheduler3.start();
     scheduler3.scheduleJob(job3, trigger3);
     
     JobDetail job4 = JobBuilder.newJob(JobM.class).withIdentity("helloJob4", "group4").build();
     Trigger trigger4 = TriggerBuilder.newTrigger().withIdentity("cronTrigger4", "group4").withSchedule(CronScheduleBuilder.cronSchedule(configProp.getProperty("JobM_CRON"))).build();
     
     Scheduler scheduler4 = new StdSchedulerFactory().getScheduler();
     scheduler4.start();
     scheduler4.scheduleJob(job4, trigger4);
     

}
}