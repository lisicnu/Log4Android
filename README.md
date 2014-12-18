Log4Android 使用说明
===========

工程已经发布到maven仓库


使用方式:

在dependencies 中加入: 

  compile 'com.github.lisicnu:log4android:1.0.0'
  

##说明##
 本工程基于 LOG4android 二次开发.

增加文件写入的支持, 包括文件名格式, 指定文件大小,  使用方式 和 log4j一样.


使用混淆时, 加入以下语句:

* -keep class com.github.lisicnu.log4android.format.*
* -keep class com.github.lisicnu.log4android.appender.*

配置文件, 参考 microlog.properties


##使用说明##

调用 以下函数初始化:

LogManager.init(context);
LogManager.isDebug 此变量表示是否是测试模式, 测试模式会将日志输出到logcat 窗口中, 不会写入到其他地方.


使用代码:
* LogManager.d(String tag, Object msg);
* LogManager.v(String tag, Object msg);
* LogManager.i(String tag, Object msg);
* LogManager.w(String tag, Object msg);
* LogManager.e(String tag, Object msg);
* LogManager.fetal(String tag, Object msg);


##配置文件说明##

{

 microlog.properties instructions.

 microlog.appender: 
			DatagramAppender: The DatagramAppender uses a DatagramSocket  to 
							  send Datagram to a server. This can be used on 
							  Android or in a Java SE environment.

			FileAppender:  An appender to log to a file in on the SDCard,can
						   assign with absolute path.
							
			LogCatAppender: With android log entry. always enabled.

			SyslogAppender: System log format.
							e.g. <priority> [date time host] tag message



 microlog.formatter:
			SimpleFormatter: A simple formatter that only outputs the level, 
							 the message and the Throwable object if available

			PatternFormatter: 
							  usually use with [key].pattern

							  about [pattern] see below:
  							  The available pattern conversions are:
							  %i : the client id
							  %c : prints the name of the Logger
							  %d : prints the date (absolute time)
							  %m : prints the logged message
							  %P : prints the priority, i.e. Level of the message.
							  %r : prints the relative time of the logging. (The 
  							  	   first logging is done at time 0.)
							  %t : prints the thread name.
							  %T : prints the Throwable object.
							  %% : prints the '%' sign.

 microlog.appender.FileAppender.File:
			This is for set output folder, can set with absolute path.

			start with "/" meaning absolute path, otherwise it means a file folder 
			where located in context external cache storage's folder.

			if start with mnt/sdcard/xxx, this expression means save file into xxx
			folder.
			
			if input is folder, must start with "/", otherwise , all of input will
  			be recognized as fileName. If input is file, must with extension.
		
			if wrap file, the files will auto added to "/mnt/sdcard/logs/"

 microlog.appender.FileAppender.Options: [%f|%p] [%s]			
			extension: format file name. with extensions. [not tested yet.]
			e.g. [auto]
				%a : append file.  default: new file. [will delete exist file.]
				%f : use date time format.  i.e. "yyyyMMddHHmmss".
				%p : use prefix and plus index to format.
					 usage: %p, when use this  parameter, key[File] will be 
  							recognized as prefix.
					 result: prefix0.log, prefix1.log.							
				%s : wrap size, default is 0M, means not wrap file. unit is M.
					 A float number.

			arguments example: 
				1. %f-yyyyMMddHHmmss
				2. %p
				3. %s-0.4
 
 microlog.level:
			log level.

 microlog.tag: 
			not used yet.

 microlog.rootLogger:
                   not supported yet, if not set, will auto use simple style.
					e.g. simple style: will auto add logcat appender.

 microlog.addDefaultLogger:
                   default true, if appender not set, this will auto add LogCatAppender.

}

##配置文件style##

* microlog.rootLogger=rootLogger // 暂时未使用
* microlog.formatter=PatternFormatter
* microlog.formatter.PatternFormatter.pattern=%i %c %d [%P] %m %T
* microlog.appender=FileAppender
* microlog.appender.FileAppender.File=logs
* microlog.appender.FileAppender.Options=%a %f-yyyyMMdd %s-2
* microlog.level=DEBUG
* microlog.addDefaultLogger=false
* microlog.tag= // 暂时未使用.




