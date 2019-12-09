package com.android.visitlog;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class PeopleActivityFragment extends Fragment {

    View v;
    public static ArrayList<People> people_list;

    Button floatingActionButton;

    DBHelper helper;

    Toolbar toolbar;

    MenuItem search;
    MenuItem edit;
    MenuItem itemCheckBox;

    public String year;
    public String month;
    public String day;

    boolean editMode = false;

    private RecyclerView recyclerView;

    private PeopleAdapter adapter;
    private PeopleAdapter.ClickListener clickListener;
    private PeopleAdapter.RemoveListener removeListener;

    private TextView countPeople;

    public PeopleActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_people_activity, container, false);

        helper = new DBHelper(getContext());

        floatingActionButton = v.findViewById(R.id.floatingActionButton);

        toolbar = v.findViewById(R.id.toolbar_all_people);
       // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);

        if (people_list == null) {
            people_list = new ArrayList<>();
            updatePeople();
        }

        PeopleAdapter.ClickListener clickItemPeople = new PeopleAdapter.ClickListener() {

            @Override
            public void onLongItemClick(People item) {
                if (editMode) {
                    helper.removePeople(item.Name);

                    people_list.remove(item);
                    update();
                    setCounterText(people_list.size());
                    Toast.makeText(getContext(),
                            item.Name + " " + getResources().getString(R.string.hasRemoved),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemClick(People item) {
                if (!editMode) {
                    if (!helper.containsDataPeople(item, year, month, day)) {
                        helper.SetDataInDataTable(item.Name, year, month, day);

                        Toast.makeText(getContext(),
                                item.Name + " " + getResources().getString(R.string.AddData) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                item.Name + " " + getResources().getString(R.string.PeopleAlreadyHaveThisDate) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };

        this.clickListener = clickItemPeople;

        PeopleAdapter.RemoveListener removeListener = item -> {
            helper.removePeople(item.Name);
            people_list.remove(item);
            update();
            setCounterText(people_list.size());
            Toast.makeText(getContext(),
                    item.Name + " " + getResources().getString(R.string.hasRemoved),
                    Toast.LENGTH_SHORT).show();
        };

        this.removeListener = removeListener;

        recyclerView = v.findViewById(R.id.alert_people_recyclerView);
        countPeople = v.findViewById(R.id.scountPeople);

        if (clickListener != null && removeListener!= null) {
            adapter = new PeopleAdapter(getContext(), clickListener, removeListener, people_list);
            adapter.setRemoveVisible(false);
        }
        else if (clickListener != null)
            adapter = new PeopleAdapter(getContext(), clickListener, people_list);
        else
            adapter = new PeopleAdapter(getContext(), null, people_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(false);
        v.findViewById(R.id.stemp).requestFocus();

        setCounterText(people_list.size());

        floatingActionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);

            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                EditText editText = view1.findViewById(R.id.text_edit_alertview);
                    addNewPeople(editText.getText().toString());
                    updatePeople();
            });
            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        });

        return  v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.people_activity_menu, menu);
        super.onCreateOptionsMenu(menu,menuInflater);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemCheckBox = menu.findItem(R.id.itemCheckBox);
        itemCheckBox.setVisible(false);

        itemCheckBox.setOnMenuItemClickListener(item -> {
            return false;
        });

        edit.setOnMenuItemClickListener(menuItem -> {
            if (editMode) {
                this.setRemoveVisible(false);
                //floatingActionButton.hide();
                editMode = !editMode;
            } else {
                edit.setVisible(false);
                this.setRemoveVisible(true);
                //floatingActionButton.show();
                editMode = !editMode;
            }
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {

            if (!editMode) {
                edit.setVisible(false);
            }
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(false);

        });

        mSearchView.setOnCloseListener(() -> {
            updatePeople();
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (!editMode) {
                edit.setVisible(true);
                edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            return false;
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("")) {
                    updatePeople();
                } else {
                    people_list.clear();
                    people_list.addAll(helper.getPeopleFilter(newText));
                    update();
                }

                setCounterText(people_list.size());
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (editMode) {

                setRemoveVisible(false);
                //floatingActionButton.hide();
                editMode = !editMode;
                edit.setVisible(true);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void update(){
        if(adapter!=null && v!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setCounterText(int text){
        if(countPeople!=null)
            countPeople.setText(text + " "+ getResources().getString(R.string.People));
    }

    public void setRemoveVisible(boolean enable){
        if(adapter != null){
            adapter.setRemoveVisible(enable);
        }
    }

    public boolean getRemoveVisible(){
        if(adapter!= null){
            return adapter.isRemoveVisible();
        }
        return false;
    }

    public void GetData(String Year,String Month,String Day){
        year = Year;
        month = Month;
        day = Day;
    }

    private void addNewPeople(String name) {
        if (!name.equals("")) {

            if (!helper.containsPeople(new People(name))) {
                helper.addPeople(name);
                people_list.add(new People(name));
            } else {
                int counter = 2;

                while (helper.containsPeople(new People(name + counter))) {
                    counter++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setCancelable(true);

                String newName = name + counter;

                builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    helper.addPeople(newName);
                    people_list.add(new People(newName));
                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            setCounterText(people_list.size());
        } else {
            Toast.makeText(getContext(),
                    "Не удалось добавить пустое наименование",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePeople() {
        people_list.clear();
        people_list.addAll(helper.getAllPeople());

        if (this != null) {
            update();
            setCounterText(people_list.size());
        }

    }
}
