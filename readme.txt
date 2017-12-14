Hadoop入门实战
一、单机版部署
下载
wget https://archive.apache.org/dist/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz

解压安装
tar -zxvf hadoop-2.7.3.tar.gz
mv hadoop-2.7.3 hadoop
mv hadoop /usr/local

修改配置文件core-site.xml(/usr/local/hadoop/etc/hadoop/)
<configuration>
    <property>
            <name>fs.defaultFS</name>
            <value>hdfs://asp-nj-srv98:9000</value>
      </property>
    <property>
            <name>hadoop.tmp.dir</name>
            <value>/var/hadoop/</value>
    </property>
</configuration>


启动Hadoop
bin/hdfs namenode –format （格式化namenode）
sbin/start-dfs.sh （启动NameNode 和 DataNode 守护进程）
sbin/start-yarn.sh（启动ResourceManager 和 NodeManager 守护进程）

关闭Hadoop
sbin/stop-dfs.sh
sbin/stop-yarn.sh

判断Hadoop启动状态
hdfs dfsadmin -report
http://10.5.2.241:50070/

二、项目搭建
pom.xml引入相关jar包
<dependencies>
	<dependency>
		<groupId>org.apache.hadoop</groupId>
		<artifactId>hadoop-common</artifactId>
		<version>2.7.3</version>
	</dependency>

	<dependency>
		<groupId>org.apache.hadoop</groupId>
		<artifactId>hadoop-hdfs</artifactId>
		<version>2.7.3</version>
	</dependency>

	<dependency>
		<groupId>jdk.tools</groupId>
		<artifactId>jdk.tools</artifactId>
		<version>1.8</version>
		<scope>system</scope>
		<systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
	</dependency>
</dependencies>

三、Hadoop文件操作命令
hadoop上传文件
bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/1.txt /
bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/1.txt /hadoop

查看HDFS里面文件
bin/hadoop fs -ls /

在hdfs中创建文件夹：bin/hadoop fs -mkdir -p /user/hadoop/input
创建文件：bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/2.txt /user/hadoop/input/
查看：bin/hadoop fs -ls /user/hadoop/input/*

修改文件的权限：
bin/hadoop fs -chmod -R 777 /user/hadoop/input/
查看文件权限修改情况：bin/hadoop fs -ls /user/hadoop/input/*

查看运行结果：
bin/hadoop fs -cat /user/hadoop/input/2.txt

删除HDFS中的文件夹：
bin/hadoop fs -rm -r /hadoop
查看删除情况：bin/hadoop fs -ls /

四、环境准备（太坑。。。）
1、服务器telnet 127.0.0.1 9000 通
	telnet 10.5.2.241 9000 不通
2、客户端 telnet 10.5.2.241 9000 不通
3、服务器 core-site.xml(fs.defaultFS --> hdfs://asp-nj-srv98:9000)
4、客户端 修改 hosts文件 (10.5.2.241 asp-nj-srv98)
5、服务器 修改 /etc/hosts(127.0.0.1   localhost、10.5.2.241 asp-nj-srv98)
6、客户端 telnet 10.5.2.241 9000 通
	服务器端 telnet 10.5.2.241 9000 通
7、    创建文件夹：bin/hadoop fs -mkdir -p /user/hadoop/input
	创建文件：bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/1.txt /user/hadoop/input/
	bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/2.txt /user/hadoop/input/
	修改文件权限：bin/hadoop fs -chmod -R 777 /user/hadoop/input/
	查看文件权限：bin/hadoop fs -ls /user/hadoop/input/
8、测试期间关闭权限检查
需要在namenode的hdfs-site.xml上，添加配置：(dfs.permissions.enabled -->false)

五、HDFS代码开发
public class HelloHDFS {
	public static void main(String[] args) throws Exception {
		//版本1
//		URL url = new URL("http://www.baidu.com");
//		InputStream is = url.openStream();
//		IOUtils.copyBytes(is, System.out, 1024*1025, true);
		//版本2
//		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
//		URL url = new URL("hdfs://asp-nj-srv98:9000/1.txt");
//		InputStream is = url.openStream();
//		IOUtils.copyBytes(is, System.out, 1024*1024, true);
		//版本3
		try {
			Configuration conf = new Configuration();
	        conf.set("fs.defaultFS", "hdfs://asp-nj-srv98:9000");
	        conf.set("dfs.replication", "1");//默认为3
	        FileSystem fileSystem = FileSystem.get(conf);
	        
			boolean success = fileSystem.mkdirs(new Path("/user/hadoop/input/sb"));
			System.out.println("创建文件是否成功:" + success);
			
			success = fileSystem.exists(new Path("/user/hadoop/input/sb"));
			System.out.println("文件是否存在:" + success);
	        
	        success = fileSystem.delete(new Path("/user/hadoop/input/sb"), true);
	        System.out.println("删除文件是否成功：" + success);
	        
	        FSDataOutputStream out = fileSystem.create(new Path("/user/hadoop/input/test.data"));
	        FileInputStream in = new FileInputStream("d:/test.txt");
	        byte[] buf = new byte[4096];
	        int len = in.read(buf);
	        while(len != -1) {
	            out.write(buf,0,len);
	            len = in.read(buf);
	        }
	        in.close();
	        out.close();
	        
	        FileStatus[] statuses = fileSystem.listStatus(new Path("/"));
	        System.out.println("statuses.length:"+statuses.length);
	        for(FileStatus status : statuses) {
	        	System.out.println("status.getPath():"+status.getPath());
	        	System.out.println("status.getPermission():"+status.getPermission());
	        	System.out.println("status.getReplication()"+status.getReplication());
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}  
	}
}

六、Yarn配置
配置yarn-size.xml（resourceManager和dataManager每一个节点都需要配置yarn-size.xml，配置如下：）
<configuration>
 <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>asp-nj-srv98</value>
 </property>
 
 <property>  
    <name>yarn.nodemanager.aux-services</name>  
    <value>mapreduce_shuffle</value>  
 </property>  
 
 <property>
    <name>yarn.nodemanager.auxservices.mapreduce.shuffle.class</name>
    <value>org.apache.hadoop.mapred.ShuffleHandler</value>
 </property>  
</configuration>

配置mapred-site.xml，只需要在master的/usr/local/hadoop/etc/hadoop目录下，cp mapred-site.xml.template mapred-site.xml
编辑mapred-site.xml
<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
</configuration>

重启hdfs、yarn
Yarn访问地址：http://10.5.2.241:8088/

七、文件配置
1、上传一个测试文件到hadoop的/user/hadoop/input/目录上
bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/3.txt /user/hadoop/input/
2、find /usr/local/hadoop -name *example*.jar 查找示例程序文件
find /usr/local/hadoop -name *example*.jar
3、通过hadoop jar xxx.jar wordcount /input /output来运行示例程序
bin/hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar 
wordcount /user/hadoop/input/ /output
4、查看执行结果
bin/hadoop fs -text /output/part-r-00000
