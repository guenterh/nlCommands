package org.swissbib.nl.mf.stream.reader;

import org.culturegraph.mf.stream.converter.xml.GenericXmlHandler;
import org.culturegraph.mf.stream.reader.XmlReaderBase;

/**
 * Created by swissbib on 22.11.15.
 */
public class GenericArticleReader extends XmlReaderBase {

    public GenericArticleReader() {
        super(new GenericXmlHandler("article"));
    }




}
