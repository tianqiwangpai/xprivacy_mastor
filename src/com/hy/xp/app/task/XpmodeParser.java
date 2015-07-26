package com.hy.xp.app.task;

import java.io.InputStream;
import java.util.List;

public interface XpmodeParser
{

	public List<xpmodel> parse(InputStream is) throws Exception;

	// public String serialize(List<xpmodel> books) throws Exception;
}
