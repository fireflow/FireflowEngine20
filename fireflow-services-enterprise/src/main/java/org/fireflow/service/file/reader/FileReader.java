package org.fireflow.service.file.reader;

import java.util.List;

import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.Message;
import org.fireflow.service.file.FileObject;

public interface FileReader {
	/**
	 * 从特定的目录读取文件；
	 * 如果startTime为正数，则读取startTime之后修改过的文件；如果为0或者负数，否则读取所有的文件。
	 * @param startTime 开始时间，该时间是从 Unix epoch time（即 1970-01-01T00:00:00Z ISO 8601）到某一时间点的毫秒数。
	 * 例如，在Java系统中用(new java.util.Date()).getTime()则可以获得该时间值<br/>
	 * 将.net系统中的某一时间点转换成本系统的计时法，请使用如下方法：<br/>
	 * return Decimal.ToInt64(Decimal.Divide(DateTime.Now.Ticks - new DateTime(1970, 1, 1, 8, 0, 0).Ticks, 10000))<br/>
	 * 或者<br/>
	 * return Decimal.ToInt64(Decimal.Divide(DateTime.UtcNow.Ticks - 621355968000000000, 10000));
	 * 
	 * @return
	 */
	public Message<List<FileObject>> readFile(Long startTime)throws ServiceInvocationException;
}
