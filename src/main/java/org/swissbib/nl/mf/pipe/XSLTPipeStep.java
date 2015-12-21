package org.swissbib.nl.mf.pipe;

import org.culturegraph.mf.framework.DefaultObjectPipe;
import org.culturegraph.mf.framework.ObjectReceiver;
import org.swissbib.documentprocessing.plugins.CreateSecondISBN;
import org.swissbib.documentprocessing.plugins.IDocProcPlugin;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by swissbib on 21.12.15.
 */
public class XSLTPipeStep extends DefaultObjectPipe<String, ObjectReceiver<String>> {


    private String templatePath;
    private Pattern linePattern = Pattern.compile("<record>.*?</record>",Pattern.CASE_INSENSITIVE);
    Transformer transformer;



    public void setInitializePlugins(boolean initialize) {
        if (initialize) {
            //InputStream configStream = XSLTPipeStep.class.getClassLoader().getResourceAsStream("/home/swissbib/environment/code/nationallizenzen/mfCommands/src/main/resources/config.properties");
            InputStream configStream = XSLTPipeStep.class.getClassLoader().getResourceAsStream("config.properties");
            Properties configProps = new Properties();
            try {
                configProps.load(configStream);
                HashMap<String, String> configuration = new HashMap<>();
                ArrayList <IDocProcPlugin> plugins  = new ArrayList<>();

                Enumeration<Object> propKeys = configProps.keys();

                while (propKeys.hasMoreElements()) {
                    String key = (String) propKeys.nextElement();
                    configuration.put(key.toUpperCase(), (String) configProps.getProperty(key));
                }


                //are there any configured plugins?
                if (configuration.containsKey("PLUGINS.TO.LOAD".toUpperCase())) {

                    String pluginsConf = configuration.get("PLUGINS.TO.LOAD");
                    ArrayList<String> pluginClassNames = new ArrayList<String>();
                    pluginClassNames.addAll(Arrays.asList(pluginsConf.split("###")));


                    for (String cName : pluginClassNames) {
                        try {
                            Class tClass = Class.forName(cName);
                            IDocProcPlugin docProcPlugin = (IDocProcPlugin) tClass.newInstance();
                            docProcPlugin.initPlugin(configuration);
                            plugins.add(docProcPlugin);
                        } catch (ClassNotFoundException nfE) {
                            nfE.printStackTrace();
                        } catch (InstantiationException instE) {
                            instE.printStackTrace();
                        } catch (IllegalAccessException illegalAccessE) {
                            illegalAccessE.printStackTrace();
                        }
                        //patterns.add(Pattern.compile(regex));
                    }


                }
            } catch (IOException ioException ) {
                ioException.printStackTrace();
            }


        }
    }


    public void setTemplate(String templatePath) {

        System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");


        this.templatePath = templatePath;

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource source = null;

        if (new File(templatePath).exists()) {
            source = new StreamSource(templatePath);
        }


        try {
            transformer = transformerFactory.newTransformer(source);
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();

        }

    }

    public void setUseLineWith(String regex) {
        linePattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void process(String obj) {
        if (linePattern.matcher(obj).find()) {
            Source recordSource =  new StreamSource(new StringReader(obj));
            StringWriter recordTargetBuffer = new StringWriter();
            StreamResult recordTarget = new StreamResult(recordTargetBuffer);

            try {
                transformer.transform(recordSource, recordTarget);
                getReceiver().process(recordTargetBuffer.toString());

            } catch (TransformerException transException) {
                transException.printStackTrace();
            }


        }

    }
}
