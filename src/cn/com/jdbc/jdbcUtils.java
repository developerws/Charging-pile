package cn.com.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class jdbcUtils {
	//数据库连接池对象
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	//获取新连接
	public static Connection getConnection() throws SQLException {
		Connection con = tl.get();
		//当con!=null时表示已经开启了事务，返回开启事务的连接
		if(con!=null) return con;
		return dataSource.getConnection();
	}
	//获取数据库连接池对象
	public static ComboPooledDataSource getDataSource() {
		return dataSource;
	}
	
	/*
	 * 事务处理
	 */
	//开启事务
	public static void beginTransaction() throws SQLException {
		Connection connection = tl.get();
		if(connection!=null) throw new SQLException("已经开启了事务，就不要重复开启了！");
		//给connection赋值
		connection = getConnection();
		//设置为手动提交
		connection.setAutoCommit(false);
		//保存当前线程的连接到threadlocal中
		tl.set(connection);
	}
	//提交事务
	public static void commitTransaction() throws SQLException {
		Connection connection = tl.get();
		if(connection==null) throw new SQLException("还没有开启事务，不能提交");
		//提交事务
		connection.commit();
		//关闭连接
		connection.close();
		//事务已经结束！从tl中移除
		tl.remove();
		
	}

	//回滚事务
	public static void rollbackTransaction() throws SQLException {
		Connection con = tl.get();
		if(con==null) throw new SQLException("还没有开启事务，不能回滚！"); 
		con.rollback();
		con.close();
		tl.remove();//从tl中移除连接
	}
	
	//释放连接
	public static void releaseConnection(Connection con) throws SQLException{
		Connection connection = tl.get();
		//如果con ==null，说明现在没有事务，那么connection一定不是事务专用的！
		if(connection == null) con.close();
		//如果con !=null，说明有事务，那么需要判断参数连接是否与con相等，若不等，说明参数连接不是事务连接
		if(connection != con) con.close();
	}
	
	
}
