package wlu.cp670.ExerciseLog.Utils;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


/*reference:
    https://docs.oracle.com/javase/7/docs/api/javax/xml/validation/package-summary.html
 */
public class XMLValidator {
    private static final String TAG = " XMLValidator";
    private XMLValidator() {
    }

    public static boolean againstSchema(InputStream xsdStream, InputStream xmlStream){

        try {
            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            DocumentBuilder parser = parserFactory.newDocumentBuilder();
            Document document = parser.parse(xmlStream);

            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Source schemaFile = new StreamSource(xsdStream);
            Schema schema = factory.newSchema(schemaFile);

            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));

        } catch (SAXException | IllegalArgumentException e){
            Log.e(TAG, e.getMessage());
            return false;
        } catch (ParserConfigurationException pce){
            Log.e(TAG, pce.getMessage());
            return false;
        }   catch (IOException ioe){
            Log.e(TAG, ioe.getMessage());
            return false;
        }

        return true;

    }

    public static boolean againstSchema(String xsdFileName, String xmlFileName, Context context) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        DocumentBuilder parser = parserFactory.newDocumentBuilder();
        Document document = parser.parse(new File(context.getFilesDir(), xmlFileName));

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new File(context.getFilesDir(), xsdFileName));
        Schema schema = factory.newSchema(schemaFile);

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {
            // instance document is invalid!
        }
        return true;
    }

}
