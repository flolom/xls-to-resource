package com.hotcocoacup.mobiletools.xlstoresouces;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.hotcocoacup.mobiletools.xlstoresouces.model.KeyValuePair;

public interface Processor {

	void process (OutputStream outputStream, Map<String, List<KeyValuePair>> keyValuePair) throws IOException;
	
}
