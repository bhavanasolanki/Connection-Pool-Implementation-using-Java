package myConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyRunnable implements Runnable {
	
	Integer num;
	ConnPoolBlockingQueue pool_object;
	
	MyRunnable(Integer num, ConnPoolBlockingQueue pool_object){
		this.num=num;
		this.pool_object=pool_object;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Thread entered run()-"+Thread.currentThread().getName()+" with num="+num);
		Connection conn=null;
		try {
			conn=pool_object.getConnectionFromPool();
			System.out.println("Connection provided to Thread-"+num+" ::: "+conn);
			PreparedStatement pstmt=conn.prepareStatement("Insert into pool_tbl(Id) values(?)");
			pstmt.setInt(1, num);
			
			pstmt.execute();
			
			pool_object.freeConnection(conn);
		} catch (SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
