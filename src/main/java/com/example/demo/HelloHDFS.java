package com.example.demo;

import java.io.FileInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

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
