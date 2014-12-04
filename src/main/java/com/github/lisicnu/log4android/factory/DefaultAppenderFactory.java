package com.github.lisicnu.log4android.factory;

import com.github.lisicnu.log4android.appender.Appender;
import com.github.lisicnu.log4android.appender.LogCatAppender;
import com.github.lisicnu.log4android.format.PatternFormatter;

public enum DefaultAppenderFactory {
	;
	
	public static Appender createDefaultAppender() {
		Appender appender = new LogCatAppender();
		appender.setFormatter(new PatternFormatter());
		
		return appender;
	}
}
