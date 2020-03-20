package cn.com.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class jdbcUtils {
	//���ݿ����ӳض���
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	//��ȡ������
	public static Connection getConnection() throws SQLException {
		Connection con = tl.get();
		//��con!=nullʱ��ʾ�Ѿ����������񣬷��ؿ������������
		if(con!=null) return con;
		return dataSource.getConnection();
	}
	//��ȡ���ݿ����ӳض���
	public static ComboPooledDataSource getDataSource() {
		return dataSource;
	}
	
	/*
	 * ������
	 */
	//��������
	public static void beginTransaction() throws SQLException {
		Connection connection = tl.get();
		if(connection!=null) throw new SQLException("�Ѿ����������񣬾Ͳ�Ҫ�ظ������ˣ�");
		//��connection��ֵ
		connection = getConnection();
		//����Ϊ�ֶ��ύ
		connection.setAutoCommit(false);
		//���浱ǰ�̵߳����ӵ�threadlocal��
		tl.set(connection);
	}
	//�ύ����
	public static void commitTransaction() throws SQLException {
		Connection connection = tl.get();
		if(connection==null) throw new SQLException("��û�п������񣬲����ύ");
		//�ύ����
		connection.commit();
		//�ر�����
		connection.close();
		//�����Ѿ���������tl���Ƴ�
		tl.remove();
		
	}

	//�ع�����
	public static void rollbackTransaction() throws SQLException {
		Connection con = tl.get();
		if(con==null) throw new SQLException("��û�п������񣬲��ܻع���"); 
		con.rollback();
		con.close();
		tl.remove();//��tl���Ƴ�����
	}
	
	//�ͷ�����
	public static void releaseConnection(Connection con) throws SQLException{
		Connection connection = tl.get();
		//���con ==null��˵������û��������ôconnectionһ����������ר�õģ�
		if(connection == null) con.close();
		//���con !=null��˵����������ô��Ҫ�жϲ��������Ƿ���con��ȣ������ȣ�˵���������Ӳ�����������
		if(connection != con) con.close();
	}
	
	
}
