package ci4821.sepdic2019.utils;

import ci4821.sepdic2019.ds.Log;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Parser
 */
public class Parser {
    
    private Yaml yaml;
    private String fileName;

    public Parser(String fileName) {

        this.yaml = new Yaml();
        this.fileName = fileName;
    }
    
    public Map<String, Object> parseFile() {

        InputStream inputStream = null;
        Map<String, Object> parsedData = null;
        
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            inputStream = classLoader.getResourceAsStream(this.fileName);
            parsedData = yaml.load(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return parsedData;
    }
}