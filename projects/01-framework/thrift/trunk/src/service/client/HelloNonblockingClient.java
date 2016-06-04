package service.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import service.demo.Hello; 

/** 
 * @author ���
 * @version 1.0
 * @datetime 2015-12-30 ����09:06:16 
 * ��˵�� 
 */
public class HelloNonblockingClient {

	public static final String SERVER_IP = "172.168.8.36";
	public static final int SERVER_PORT = 9812;
	public static final int TIMEOUT = 30000;

	public static void main(String[] args) {
		try {
			//���ô���ͨ�������ڷ�����������Ҫʹ��TFramedTransport���������ݷֿ鷢��  
			TTransport transport = new TFramedTransport(new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT));
			//Э��Ҫ�ͷ����һ��
			//HelloTNonblockingServer
			//ʹ�ø��ܶȶ�����Э�� 
			TProtocol protocol = new TCompactProtocol(transport);
			//ʹ�ö�����Э�� 
			//TProtocol protocol = new TBinaryProtocol(transport);
			Hello.Client client = new Hello.Client(protocol);
			transport.open();
			String result = client.helloString("jack");
			System.out.println("result : " + result);
			//�ر���Դ
			transport.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}