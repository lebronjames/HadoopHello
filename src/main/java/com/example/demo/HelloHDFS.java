package com.example.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HelloHDFS {
	public static Log log =  LogFactory.getLog(HelloHDFS.class);
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
			log.info("创建文件是否成功:" + success);
			
			success = fileSystem.exists(new Path("/user/hadoop/input/sb"));
	        log.info("文件是否存在:" + success);
	        
	        success = fileSystem.delete(new Path("/user/hadoop/input/sb"), true);
	        log.info("删除文件是否成功：" + success);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        
        
	}
	
	
	
}
