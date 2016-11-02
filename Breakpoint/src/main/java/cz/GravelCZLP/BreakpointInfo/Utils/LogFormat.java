/*
* Class By GravelCZLP at 2. 11. 2016
*/

package cz.GravelCZLP.BreakpointInfo.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormat extends Formatter {

	@Override
	public String format(LogRecord log) {
		SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyy HH:mm:ss");
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(log.getMillis());
		String toSend = "[ " + log.getLoggerName() + 
				" ] || [ " + logTime.format(cal.getTime()) + 
				" ]  [ " + log.getLevel() + " ] :" + log.getMessage();
		return toSend;
	}

}
