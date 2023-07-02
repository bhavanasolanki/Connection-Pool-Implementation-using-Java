package myConnectionPool;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainConnectionClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Integer pool_size = 5;
			String jdbc_url= "jdbc:mysql://localhost:3306/schema1";
			String username= "root";
			String password= "root"; 
			ConnPoolBlockingQueue pool_object=new ConnPoolBlockingQueue(pool_size,jdbc_url,username,password);
			ExecutorService executor=Executors.newFixedThreadPool(10);
			
			for(int i=0;i<30;i++) {
				executor.execute(new MyRunnable(i+1,pool_object));
			}
			
			executor.shutdown();
			while(!executor.isTerminated()) {
				// wait
			}
		    System.out.println("Thread execution completed!");
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
