package com.mega.matrimony.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Model.SavedItem;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedSearchActivity extends AppCompatActivity {

    ListView lv_saved;
    List<SavedItem> list = new ArrayList<>();
    SavedAdapter adapter;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    boolean continue_request;
    int page = 0;
    TextView tv_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved Search");

        common = new Common(this);
        session = new SessionManager(this);

        tv_no_data = findViewById(R.id.tv_no_data);

        page = page + 1;
        getListdata(page);

        lv_saved = (ListView) findViewById(R.id.lv_saved);
        adapter = new SavedAdapter(this, list);
        lv_saved.setAdapter(adapter);

        lv_saved.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        if (pd != null)
                            pd.dismiss();
                        page = page + 1;
                        getListdata(page);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });
    }

    private void getListdata(final int page) {
        pd = new ProgressDialog(SavedSearchActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequest(Utils.saved_search + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                AppDebugLog.print("response : "+response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    continue_request = object.getBoolean("continue_request");
                    if (total_count != 0) {
                        lv_saved.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                        if (total_count != list.size()) {
                            JSONArray data = object.getJSONArray("data");
                            AppDebugLog.print("data count : : "+data.length());
                            AppDebugLog.print("response : "+response);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                SavedItem item = new SavedItem();
                                item.setName(obj.getString("search_name"));
                                String phot;
                                if (obj.getString("with_photo").equals("photo_search")) {
                                    phot = "With Photo";
                                } else {
                                    phot = "";
                                }

                                item.setId(obj.getString("id"));
                                String detail = checkAge(obj.getString("from_age"), obj.getString("to_age")) +
                                        checkheight(common.calculateHeight(obj.getString("from_height")), common.calculateHeight(obj.getString("to_height"))) +
                                        checkNull(obj.getString("marital_status")) + checkNull(obj.getString("religion_str")) +
                                        checkNull(obj.getString("caste_str")) + checkNull(obj.getString("mother_tongue_str")) +
                                        checkNull(obj.getString("country_str")) + checkNull(obj.getString("state_str")) +
                                        checkNull(obj.getString("city_str")) + checkNull(obj.getString("education_str")) +
                                        checkNull(obj.getString("occupation_str")) + checkNull(obj.getString("employee_in")) +
                                        checkNull(obj.getString("income")) + checkNull(obj.getString("diet")) +
                                        checkNull(obj.getString("drink")) + checkNull(obj.getString("smoking")) +
                                        checkNull(obj.getString("complexion")) + checkNull(obj.getString("bodytype")) +
                                        checkNull(obj.getString("star_str")) + checkNull(obj.getString("manglik")) +
                                        checkNull(obj.getString("keyword")) + checkNull(obj.getString("id_search")) + checkNull(phot);

                                detail = detail.trim();
                                item.setSearch_data(detail.substring(0, detail.length() - 1));
                                String search_page_nm = obj.getString("search_page_nm");
                                item.setSearch_name(search_page_nm);
                                if (search_page_nm.equals(Utils.TYPE_SEARCH_QUICK)) {
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                    param.put("from_age", checkNull1(obj.getString("from_age")));
                                    param.put("to_age", checkNull1(obj.getString("to_age")));
                                    param.put("from_height", checkNull1(obj.getString("from_height")));
                                    param.put("to_height", checkNull1(obj.getString("to_height")));
                                    param.put("looking_for", checkNull1(obj.getString("marital_status")));
                                    param.put("religion", checkNull1(obj.getString("religion")));
                                    param.put("caste", checkNull1(obj.getString("caste")));
                                    param.put("mothertongue", checkNull1(obj.getString("mother_tongue")));
                                    param.put("country", checkNull1(obj.getString("country")));
                                    param.put("state", checkNull1(obj.getString("state")));
                                    param.put("city", checkNull1(obj.getString("city")));
                                    param.put("education", checkNull1(obj.getString("education")));
                                    param.put("photo_search", checkNull1(obj.getString("with_photo")));
                                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                        param.put("gender", "Male");
                                    } else {
                                        param.put("gender", "Female");
                                    }
                                    item.setSearchData(param);
                                } else if (search_page_nm.equals(Utils.TYPE_SEARCH_ADVANCE)) {
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                    param.put("from_age", checkNull1(obj.getString("from_age")));
                                    param.put("to_age", checkNull1(obj.getString("to_age")));
                                    param.put("from_height", checkNull1(obj.getString("from_height")));
                                    param.put("to_height", checkNull1(obj.getString("to_height")));
                                    param.put("looking_for", checkNull1(obj.getString("marital_status")));
                                    param.put("religion", checkNull1(obj.getString("religion")));
                                    param.put("caste", checkNull1(obj.getString("caste")));
                                    param.put("mothertongue", checkNull1(obj.getString("mother_tongue")));
                                    param.put("country", checkNull1(obj.getString("country")));
                                    param.put("state", checkNull1(obj.getString("state")));
                                    param.put("city", checkNull1(obj.getString("city")));
                                    param.put("education", checkNull1(obj.getString("education")));
                                    param.put("photo_search", checkNull1(obj.getString("with_photo")));

                                    param.put("occupation", checkNull1(obj.getString("occupation")));
                                    param.put("employee_in", checkNull1(obj.getString("employee_in")));
                                    param.put("income", checkNull1(obj.getString("income")));
                                    param.put("diet", checkNull1(obj.getString("diet")));
                                    param.put("drink", checkNull1(obj.getString("drink")));
                                    param.put("smoking", checkNull1(obj.getString("smoking")));
                                    param.put("complexion", checkNull1(obj.getString("complexion")));
                                    param.put("bodytype", checkNull1(obj.getString("bodytype")));
                                    param.put("star", checkNull1(obj.getString("star")));
                                    param.put("manglik", checkNull1(obj.getString("manglik")));
                                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                        param.put("gender", "Male");
                                    } else {
                                        param.put("gender", "Female");
                                    }
                                    item.setSearchData(param);
                                } else if (search_page_nm.equals(Utils.TYPE_SEARCH_ID)) {
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                    param.put("txt_id_search", obj.getString("id_search"));

                                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                        param.put("gender", "Male");
                                    } else {
                                        param.put("gender", "Female");
                                    }
                                    item.setSearchData(param);
                                } else if (search_page_nm.equals(Utils.TYPE_SEARCH_KEYWORD)) {
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
                                    param.put("keyword", obj.getString("keyword"));
                                    param.put("photo_search", obj.getString("with_photo"));
                                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                        param.put("gender", "Male");
                                    } else {
                                        param.put("gender", "Female");
                                    }
                                    item.setSearchData(param);
                                }
                                list.add(item);

                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        lv_saved.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    private String checkNull(String text) {
        if (text != null && !text.equals("") && !text.equals("null")) {
            return text + ", ";
        }
        return "";
    }

    private String checkNull1(String text) {
        if (text != null && !text.equals("") && !text.equals("null")) {
            return text;
        }
        return "";
    }

    private String checkheight(String from, String to) {
        if (from != null && to != null && !from.equals("") && !to.equals("") && !from.equals("null") && !to.equals("null")) {
            return from + " to " + to + ", ";
        } else if (from != null && !from.equals("") && !from.equals("null")) {
            return from + ", ";
        } else if (to != null && !to.equals("") && !to.equals("null")) {
            return to + ", ";
        }
        return "";
    }

    private String checkAge(String from, String to) {
        if (from == null && to == null) {
            return "";
        }

        if (from != null && to != null && !from.equals("") && !to.equals("") && !from.equals("null") && !to.equals("null")) {
            return from + " to " + to + " Year, ";
        } else if (from != null && !from.equals("") && !from.equals("null")) {
            return from + " Year, ";
        } else if (to != null && !to.equals("") && !to.equals("null")) {
            return to + " Year, ";
        }
        return "";
    }

    public class SavedAdapter extends ArrayAdapter<SavedItem> {
        Context context;
        List<SavedItem> list;

        public SavedAdapter(Context context, List<SavedItem> list) {
            super(context, R.layout.saved_item, list);
            this.context = context;
            this.list = list;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.saved_item, null, true);

            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_search_name = (TextView) rowView.findViewById(R.id.tv_search_name);
            TextView tv_detail = (TextView) rowView.findViewById(R.id.tv_detail);
            Button img_delete = rowView.findViewById(R.id.img_delete);

            final SavedItem item = list.get(position);
            tv_name.setText(item.getName());
            tv_detail.setText(item.getSearch_data());
            tv_search_name.setText("Save from " + item.getSearch_name());

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAlert(item.getId(), position);
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, com.mega.matrimony.Activities.SearchResultActivity.class);
                    AppDebugLog.print("param in SaveSearchActivity : " + item.getSearchData());
                    i.putExtra("searchData", Common.getJsonStringFromObject(item.getSearchData()));
                    startActivity(i);
                }
            });

            return rowView;

        }
    }

    private void deleteAlert(final String id, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SavedSearchActivity.this);
        alert.setMessage("Are you sure you want to delete this search?");
        alert.setTitle("Delete Search");
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteApi(id, position);

            }
        });
        alert.show();
    }

    private void deleteApi(String id, final int position) {
        pd = new ProgressDialog(SavedSearchActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("saved_search_id", id);

        common.makePostRequest(Utils.delete_saved_search, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errormessage"));
                    if (object.getString("status").equals("success")) {
                        list.remove(position);
                        if (list.size() == 0) {
                            lv_saved.setVisibility(View.GONE);
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
