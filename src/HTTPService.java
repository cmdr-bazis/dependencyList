import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

import java.net.URL;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HTTPService {
    public LinkedList<String> getDependencies(String packetName) throws IOException, ParseException {
        LinkedList<String> dependencies = new LinkedList<>();

        URL url = new URL("https://pypi.org/pypi/" + packetName + "/json");
        InputStream inputStream = url.openStream();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);
        JSONObject jsonInfo = (JSONObject) jsonObject.get("info");
        String lastVersion = (String) jsonInfo.get("version");
        JSONObject releases = (JSONObject) jsonObject.get("releases");
        JSONArray lastRelease = (JSONArray) releases.get(lastVersion);
        JSONObject lastReleaseInfo = (JSONObject) lastRelease.get(0);
        String urlWhl = (String) lastReleaseInfo.get("url");

        BufferedInputStream streamFile = new BufferedInputStream(new URL(urlWhl).openStream());
        ZipInputStream zipInputStream = new ZipInputStream(streamFile);
        ZipEntry zipFileEntry;

        BufferedReader metadataFile = new BufferedReader(new InputStreamReader(zipInputStream));
        String tempLine = null;
        while ((zipFileEntry = zipInputStream.getNextEntry()) != null) {
            if (zipFileEntry.getName().endsWith("METADATA")) {
                tempLine = metadataFile.readLine();
            }
        }
        while (tempLine != null){
            if (tempLine.contains("Requires-Dist")){
                String[] tempLineArray = tempLine.split(" ");

                boolean deleteExtra = false;
                for (String s : tempLineArray) {
                    if (s.equals("extra")) {
                        deleteExtra = true;
                        break;
                    }
                }

                if (!deleteExtra) dependencies.add(tempLineArray[1]);
            }
            tempLine = metadataFile.readLine();
        }
        return dependencies;
    }

    public void getDependenciesList(LinkedList<String> dependencies) throws IOException, ParseException {
        for (String dependency : dependencies){
            System.out.print("    " + dependency + "\n");
            getDependencyTree(getDependencies(dependency));
        }
    }

    public void getDependencyTree(LinkedList<String> dependencies) throws IOException, ParseException {
        for (String dependency : dependencies) {
            System.out.print(dependency + "\n");
            getDependenciesList(getDependencies(dependency));
        }
    }
}
