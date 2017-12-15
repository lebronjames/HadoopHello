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
3、通过bin/hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar wordcount /input /output来运行示例程序
17/12/14 17:51:31 INFO mapreduce.Job: The url to track the job: http://asp-nj-srv98:8088/proxy/application_1513155455003_0003/
17/12/14 17:51:31 INFO mapreduce.Job: Running job: job_1513155455003_0003
17/12/14 17:51:36 INFO mapreduce.Job: Job job_1513155455003_0003 running in uber mode : false
17/12/14 17:51:36 INFO mapreduce.Job:  map 0% reduce 0%
17/12/14 17:51:41 INFO mapreduce.Job:  map 50% reduce 0%
17/12/14 17:51:42 INFO mapreduce.Job:  map 100% reduce 0%
17/12/14 17:51:45 INFO mapreduce.Job:  map 100% reduce 100%
17/12/14 17:51:45 INFO mapreduce.Job: Job job_1513155455003_0003 completed successfully
17/12/14 17:51:45 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=1511
		FILE: Number of bytes written=597225
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=1623
		HDFS: Number of bytes written=1057
		HDFS: Number of read operations=15
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=4
		Launched reduce tasks=1
		Data-local map tasks=4
		Total time spent by all maps in occupied slots (ms)=11939
		Total time spent by all reduces in occupied slots (ms)=1632
		Total time spent by all map tasks (ms)=11939
		Total time spent by all reduce tasks (ms)=1632
		Total vcore-milliseconds taken by all map tasks=11939
		Total vcore-milliseconds taken by all reduce tasks=1632
		Total megabyte-milliseconds taken by all map tasks=12225536
		Total megabyte-milliseconds taken by all reduce tasks=1671168
	Map-Reduce Framework
		Map input records=62
		Map output records=177
		Map output bytes=1849
		Map output materialized bytes=1529
		Input split bytes=456
		Combine input records=177
		Combine output records=111
		Reduce input groups=110
		Reduce shuffle bytes=1529
		Reduce input records=111
		Reduce output records=110
		Spilled Records=222
		Shuffled Maps =4
		Failed Shuffles=0
		Merged Map outputs=4
		GC time elapsed (ms)=430
		CPU time spent (ms)=3060
		Physical memory (bytes) snapshot=1250983936
		Virtual memory (bytes) snapshot=10586755072
		Total committed heap usage (bytes)=925892608
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=1167
	File Output Format Counters 
		Bytes Written=1057
4、查看执行结果
bin/hadoop fs -ls /output
bin/hadoop fs -text /output/part-r-00000

五、java开发MapReduce程序
1、maven项目加入相关依赖包
hadoop-common 2.7.3
hadoop-hdfs 2.7.3
hadoop-mapreduce-client-core 2.7.3
jdk.tools ${JAVA_HOME}/lib/tools.jar

2、Mapper代码开发
public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	//重写Map方法
	@Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
		// 获得每行文档内容，并且进行拆分 
		String line = value.toString();
		String[] words = line.split(" ");
		// 遍历拆分的内容 
		for(String word : words) {
			// 每出现一次则在原来的基础上：+1  
			context.write(new Text(word), new IntWritable(1));
		}
	}
}

3、Reducer代码开发
public class WordReducer extends Reducer<Text,IntWritable,Text,LongWritable> {

	//重写reduce方法 
	@Override
    protected void reduce(Text key, Iterable<IntWritable> values,
            Reducer<Text, IntWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
		long count = 0;
		for(IntWritable v : values) {
			// i.get转换成long类型 
			count += v.get();
		}
		// 输出总计结果 
		context.write(key, new LongWritable(count));
	}
}

4、Hadoop Job WordCount代码开发
public class HadoopWCJob  {

	public static void main(String[] args) throws Exception{
		// 创建job对象  
		Job job = Job.getInstance(new Configuration());
		// 指定程序的入口 
		job.setJarByClass(HadoopWCJob.class);
		
		// 指定自定义的Mapper阶段的任务处理类  
		job.setMapperClass(WordMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		// 数据HDFS文件服务器读取数据路径
		FileInputFormat.setInputPaths(job, "/hadoop/words.txt");
		
		// 指定自定义的Reducer阶段的任务处理类  
		job.setReducerClass(WordReducer.class);
		// 设置最后输出结果的Key和Value的类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		// 将计算的结果上传到HDFS服务 
		FileOutputFormat.setOutputPath(job, new Path("/hadoop/wordsResult"));
		
		// 执行提交job方法，直到完成，参数true打印进度和详情
		job.waitForCompletion(true);
		System.out.println("Finished");  
	}
}

5、项目打成jar包，制定Main Class类，上传至服务器上
选择MapReduceWC项目->右击菜单->Export…，在弹出的提示框中选择Java下的JAR file：
设置导出jar名称和路径，选择Next>:
设置程序的入口，设置完成后，点击Finish：
生成wc.jar如下文件

6、服务器上创建words.txt至/usr/local/hadoop/hadoop_temp/words.txt
hello tom
hello jerry
hello kitty
hello world
hello tom
hello java
hello jerry
hello kitty
hello world
hello c

7、上传该测试文件到hadoop的/hadoop/目录上
bin/hadoop fs -put /usr/local/hadoop/hadoop_temp/words.txt /hadoop/

8、通过bin/hadoop jar /usr/local/hadoop/hadoop_temp/wc.jar来运行示例程序
Running job: job_1513155455003_0004
17/12/15 11:29:02 INFO mapreduce.Job: Job job_1513155455003_0004 running in uber mode : false
17/12/15 11:29:02 INFO mapreduce.Job:  map 0% reduce 0%
17/12/15 11:29:06 INFO mapreduce.Job:  map 100% reduce 0%
17/12/15 11:29:10 INFO mapreduce.Job:  map 100% reduce 100%
17/12/15 11:29:10 INFO mapreduce.Job: Job job_1513155455003_0004 completed successfully
17/12/15 11:29:10 INFO mapreduce.Job: Counters: 49
	File System Counters
		FILE: Number of bytes read=237
		FILE: Number of bytes written=238001
		FILE: Number of read operations=0
		FILE: Number of large read operations=0
		FILE: Number of write operations=0
		HDFS: Number of bytes read=216
		HDFS: Number of bytes written=50
		HDFS: Number of read operations=6
		HDFS: Number of large read operations=0
		HDFS: Number of write operations=2
	Job Counters 
		Launched map tasks=1
		Launched reduce tasks=1
		Data-local map tasks=1
		Total time spent by all maps in occupied slots (ms)=1440
		Total time spent by all reduces in occupied slots (ms)=1486
		Total time spent by all map tasks (ms)=1440
		Total time spent by all reduce tasks (ms)=1486
		Total vcore-milliseconds taken by all map tasks=1440
		Total vcore-milliseconds taken by all reduce tasks=1486
		Total megabyte-milliseconds taken by all map tasks=1474560
		Total megabyte-milliseconds taken by all reduce tasks=1521664
	Map-Reduce Framework
		Map input records=10
		Map output records=20
		Map output bytes=191
		Map output materialized bytes=237
		Input split bytes=106
		Combine input records=0
		Combine output records=0
		Reduce input groups=7
		Reduce shuffle bytes=237
		Reduce input records=20
		Reduce output records=7
		Spilled Records=40
		Shuffled Maps =1
		Failed Shuffles=0
		Merged Map outputs=1
		GC time elapsed (ms)=76
		CPU time spent (ms)=940
		Physical memory (bytes) snapshot=434761728
		Virtual memory (bytes) snapshot=4239069184
		Total committed heap usage (bytes)=320864256
	Shuffle Errors
		BAD_ID=0
		CONNECTION=0
		IO_ERROR=0
		WRONG_LENGTH=0
		WRONG_MAP=0
		WRONG_REDUCE=0
	File Input Format Counters 
		Bytes Read=110
	File Output Format Counters 
		Bytes Written=50
Finished

9、查看执行结果
bin/hadoop fs -ls /hadoop/wordsResult

10、查看WordCount结果
bin/hadoop fs -cat /hadoop/wordsResult/part-r-00000
c	1
hello	10
java	1
jerry	2
kitty	2
tom	2
world	2