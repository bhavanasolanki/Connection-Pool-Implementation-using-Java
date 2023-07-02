package myConnectionPool;

import java.util.Queue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnPoolBlockingQueue {
	Integer pool_size;
	Queue<Connection>q;
	private static Object syncObject = new Object();
	
	/**
     * Creates a pool of connection with fix size equal to pool_size with jdbc parameters
     * Initiates LinkedList with fixed number of connections
     * @param pool_size, jdbcUrl, username, password
     * @throws SQLException thrown when connection is not available and ClassNotFoundException when DriverManager class not found
     */
	public ConnPoolBlockingQueue(Integer pool_size,String jdbcUrl,
                                   String username, String password) throws SQLException, ClassNotFoundException {
		this.pool_size = pool_size;
		q = new LinkedList<Connection>();
		for(int i=0;i<pool_size;i++) {
			Class.forName("com.mysql.cj.jdbc.Driver");  
			Connection conn=DriverManager.getConnection(jdbcUrl,username,password);
			q.add(conn);
		}
	}
	
	/**
     * Returns a connection from this pool if it is available
     * otherwise waits for connection to be available in the queue when other threads free-up the connection and notify
     * @return connection from this pool as it becomes available
     * @throws InterruptedException thrown when thread is interrupted from fetching a connection from connection pool
     */
	public synchronized Connection getConnectionFromPool() throws InterruptedException {
		System.out.println("Thread-"+Thread.currentThread().getName()+" Entered getConnectionFromPool()");
		Connection conn=null;
		if(!q.isEmpty()) 
		{
			System.out.println("Thread-"+Thread.currentThread().getName()+" got conn immediately!");
		    conn=q.peek();
			q.remove();
		}
		else {
			synchronized(syncObject) {
				System.out.println("Thread-"+Thread.currentThread().getName()+" is waiting!");
			    syncObject.wait();
			    conn=q.peek();
			    q.remove();
		}
		}
		return conn;
	}

	/**
     * Adds back the connection to pool after task completion and notifies all waiting threads in getConnectionFromPool()
     * @param connection
     */
	public void freeConnection(Connection conn) {
		synchronized(syncObject) {
			q.add(conn);
			System.out.println("Thread-"+Thread.currentThread().getName()+" freed connection!");
			syncObject.notify();
		}
	}
}
