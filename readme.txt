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
bin/hadoop fs -chmod 777 /user/hadoop/input/2.txt
查看文件权限修改情况：bin/hadoop fs -ls /user/hadoop/input/*

查看运行结果：
bin/hadoop fs -cat /user/hadoop/input/2.txt

删除HDFS中的文件夹：
bin/hadoop fs -rm -r /hadoop
查看删除情况：bin/hadoop fs -ls /

四、代码开发
1、服务器环境上传1.txt文件
2、