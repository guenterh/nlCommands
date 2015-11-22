package nationallizenzen;


import org.culturegraph.mf.formeta.formatter.FormatterStyle;
import org.culturegraph.mf.stream.converter.FormetaEncoder;
import org.culturegraph.mf.stream.sink.ObjectWriter;
import org.culturegraph.mf.stream.source.FileOpener;
import org.swissbib.nl.mf.stream.reader.*;

/**
 * Created by swissbib on 22.11.15.
 */
public class OxfordArticleReaderTest {

    public static void main (String[] args) {


        FileOpener opener = new FileOpener();

        GenericArticleReader articleReader = new GenericArticleReader();


        FormetaEncoder encoder = new FormetaEncoder();
        encoder.setStyle(FormatterStyle.MULTILINE);
        ObjectWriter<String> writer = new ObjectWriter<>(
                "src/test/java/nationallizenzen/oxford.formeta");

        opener.setReceiver(articleReader)//
                .setReceiver(encoder)//
                //.setReceiver(encoder)//
                .setReceiver(writer);

        opener.process("src/test/resources/nl/oxford/sau039.xml");
        opener.closeStream();


    }

}
