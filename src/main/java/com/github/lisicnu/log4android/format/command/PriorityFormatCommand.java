/*
 * Copyright 2008 The Microlog project @sourceforge.net
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lisicnu.log4android.format.command;

import com.github.lisicnu.log4android.Level;

/**
 * Convert the <code>Level</code> to message.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class PriorityFormatCommand implements FormatCommandInterface {

	/**
	 * @see com.github.lisicnu.log4android.format.command.FormatCommandInterface#init(String)
	 */
	public void init(String initString){
		// Do nothing.
	}

	/**
	 * @see com.github.lisicnu.log4android.format.command.FormatCommandInterface#execute(String,
	 *      String, long, com.github.lisicnu.log4android.Level, Object,
	 *      Throwable)
	 */
	public String execute(String clientID, String name, long time, Level level,
	        Object message, Throwable throwable){
		String levelString = "";

		if (level != null) {
			levelString = level.toString();
		}

		return levelString;
	}

}
