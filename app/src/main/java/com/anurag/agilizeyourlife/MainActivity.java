package com.anurag.agilizeyourlife;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.life_backlog_button) Button lifeBacklogButton;
    @BindView(R.id.sprint_cycle_button) Button sprintCycleButton;
    @BindView(R.id.todo_list_button) Button todoListButton;
    @BindView(R.id.completed_tasks_button) Button completedTasksButton;
    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.motivational_quote) TextView textViewQuote;
    @BindView(R.id.quote_author) TextView textViewAuthor;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        // Create an ad request
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        setOnClickListeners();
        mProgressBar.setVisibility(View.VISIBLE);
        callAsynchronousTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Map<String, String> intentMsgs = new HashMap<String, String>();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_life_backlog) {
            //Open Life Backlog Screen
            goToActivity(LifeBacklogActivity.class, intentMsgs);
            return true;
        }else if(id == R.id.action_sprint_cycle) {
            //Open Sprint Cycle Screen
            goToActivity(SprintCycleActivity.class, intentMsgs);
            return true;
        }else if(id == R.id.action_todo_list) {
            //Open To Do Tasks Screen
            goToActivity(ToDoListActivity.class, intentMsgs);
            return true;
        }else if(id == R.id.action_completed_tasks){
            //Open Completed Tasks screen
            goToActivity(CompletedTasksActivity.class, intentMsgs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method handles onClickListeners events for the buttons on the
     * main screen
     */
    private void setOnClickListeners() {
        Map<String,String> lifeBacklogIntent = new HashMap<String,String>();
        setOnClickListener(lifeBacklogButton,LifeBacklogActivity.class,lifeBacklogIntent);
        Map<String,String> sprintCycleIntent = new HashMap<String,String>();
        setOnClickListener(sprintCycleButton,SprintCycleActivity.class,sprintCycleIntent);
        Map<String,String> toDoListIntent = new HashMap<String,String>();
        setOnClickListener(todoListButton,ToDoListActivity.class,toDoListIntent);
        Map<String,String> completedTasksIntent = new HashMap<String,String>();
        setOnClickListener(completedTasksButton,CompletedTasksActivity.class,completedTasksIntent);
    }

    /**
     * Set onClickListener
     * @param button
     * @param cls
     * @param intentMsgs
     */
    public void setOnClickListener(Button button, final Class<?> cls, final Map<String, String> intentMsgs){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("Click Listener",cls.toString()+"Button clicked");
                goToActivity(cls, intentMsgs);
            }
        });
    }

    /**
     * Go to the next activity
     * @param cls
     * @param intentMsgs
     */
    public void goToActivity(Class<?> cls, Map<String, String> intentMsgs) {
        Intent intent = new Intent(this, cls);
        for(String key: intentMsgs.keySet()) {
            intent.putExtra(key, intentMsgs.get(key));
        }
        startActivity(intent);
    }


    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new AsyncHttpTask().execute();
                        } catch (Exception e) {
                            textViewQuote.setText(getResources().getString(R.string.failed));
                            textViewAuthor.setText("");
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000); //execute in every 60000 ms
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        String quote = null;
        String author = null;
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 1;
            Log.d("Background call", "Call made");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                URL url = new URL("http://api.forismatic.com/api/1.0/");

                Map<String,Object> postParams = new LinkedHashMap<String,Object>();
                postParams.put("method", "getQuote");
                postParams.put("format", "json");
                postParams.put("key", null);
                postParams.put("lang", "en");

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : postParams.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(5000);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(postDataBytes);
                urlConnection.connect();

                Log.d("Async", urlConnection.getResponseCode() + "");
                Log.d("Async",urlConnection.getResponseMessage());
                if(urlConnection.getResponseCode() == 200) {

                }else{
                    throw new Exception("Failed response from server"+urlConnection.getResponseCode());
                }

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                    Log.d("Async",line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                JSONObject response = new JSONObject(jsonStr);
                quote = response.getString("quoteText");
                author = response.getString("quoteAuthor");
                Log.d("Async Result",quote + "- "+author);
                if(quote!=null || "".equalsIgnoreCase(quote.trim())){
                    result = 0;
                }
                return result;
            } catch (Exception e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d("Post execute", "post execute");
            // Download complete. Lets update UI
            if (result == null || result != 0){
                textViewQuote.setText(getResources().getString(R.string.failed));
                textViewAuthor.setText("");
                Toast.makeText(getBaseContext(), getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
            } else {
                textViewQuote.setText("\""+quote+"\"");
                textViewAuthor.setText(author);
            }
            /*
            /Hide progressbar
            */
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
