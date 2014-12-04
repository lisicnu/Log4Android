package com.github.lisicnu.log4android.factory;

import com.github.lisicnu.log4android.repository.DefaultLoggerRepository;
import com.github.lisicnu.log4android.repository.LoggerRepository;

public enum DefaultRepositoryFactory {
	;
	
	public static LoggerRepository getDefaultLoggerRepository() {
		return DefaultLoggerRepository.INSTANCE;
	}
}
