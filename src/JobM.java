import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobM implements Job {

	public void execute(JobExecutionContext context)
		throws JobExecutionException {
		System.out.println("Job M is runing");
		Main m1=new Main();
		m1.startProcess("jobM");
	}

}