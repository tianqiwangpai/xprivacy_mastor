package com.hy.xp.app.task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XpmodelByXml implements XpmodeParser
{

	@Override
	public List<xpmodel> parse(InputStream is) throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		MyHandler handler = new MyHandler();
		parser.parse(is, handler);
		return handler.getxpmodels();
	}

	private class MyHandler extends DefaultHandler
	{
		private List<xpmodel> xpmodels;
		private xpmodel xpmodel;
		private StringBuilder builder;

		public List<xpmodel> getxpmodels()
		{
			return xpmodels;
		}

		@Override
		public void startDocument() throws SAXException
		{
			super.startDocument();
			xpmodels = new ArrayList<xpmodel>();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, qName, attributes);
			if (localName.equals("task")) {
				xpmodel = new xpmodel();
			}
			builder.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			super.endElement(uri, localName, qName);
			if (localName.equals("model")) {
				xpmodel.setModel(builder.toString());

			} else if (localName.equals("product")) {
				xpmodel.setProduct(builder.toString());

			} else if (localName.equals("flag")) {
				xpmodel.setFlag(builder.toString());

			} else if (localName.equals("manufacturer")) {
				xpmodel.setManufacturer(builder.toString());

			} else if (localName.equals("density")) {
				xpmodel.setDensity(builder.toString());

			} else if (localName.equals("task")) {
				xpmodels.add(xpmodel);
			}
		}
	}
}
