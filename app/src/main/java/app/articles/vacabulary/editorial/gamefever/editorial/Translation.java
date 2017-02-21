package app.articles.vacabulary.editorial.gamefever.editorial;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.content.ContentValues.TAG;
import static java.lang.System.in;

/**
 * Created by gamef on 22-02-2017.
 */

public class Translation {
    String word ;
    String wordTranslation;

    public Translation(String word) {
        this.word = word;
    }

    public Translation() {
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordTranslation() {
        return wordTranslation;
    }

    public void setWordTranslation(String wordTranslation) {
        this.wordTranslation = wordTranslation;
    }


    public void fetchTranslation(){


        new Hinditranslation().execute();

        Log.d("tag", "fetchTranslation: After fetching translation");

    }

    public void completeFetching(){
        /*call desired method and notify it that translation is fetched sucesfully*/
        Log.d("TAG", "doInBackground: "+wordTranslation);
    }


    private class Hinditranslation extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);


                if (n > 0) out.append(new String(b, 0, n));
            }


            return out.toString();
        }


        @Override
        protected String doInBackground(Void... params) {

            Log.d("My TAg", "doInBackground: calling rest");
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            // HttpGet httpGet = new HttpGet("http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=15&limit=1&api_key=8d93a189fb620cfa578070b02f8056778a640192bd39b10a4");

            HttpGet httpGet = new HttpGet("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170221T102515Z.fc9649d041fb5960.9c4e8caa31a36d7eb789cb3fae48c0e4c2cafd46&text="+getWord().trim()+"&lang=hi&[format=html]&[options=1]");

            String text = null;

            try {
                Log.d("My TAg", "doInBackground: going to call rest");
                HttpResponse response = httpClient.execute(httpGet, localContext);

                Log.d("My TAg", "doInBackground: done calling rest");
                HttpEntity entity = response.getEntity();



                text = EntityUtils.toString(entity, HTTP.UTF_8);


               // Log.d("TAG", "doInBackground: "+text);
               // Log.d("TAG", "doInBackground: by doc "+doc.getElementsByTagName("Translation").item(0).toString());


            } catch (Exception e) {
                return e.getLocalizedMessage();
            }




            return text;
        }

        protected void onPostExecute(String results) {
            if (results != null) {

                try {
                    JSONObject jsonObj = new JSONObject(results);

                    JSONArray jsonarray =jsonObj.getJSONArray("text");
                    if(jsonObj.getInt("code") == 200) {

                        wordTranslation = jsonarray.getString(0);

                    }else{
                        wordTranslation = "null";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    completeFetching();
                }



                Log.d("my text", "onPostExecute: Executed");
            }

        }


    }





}