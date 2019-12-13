package com.android.visitlog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PeopleActivity extends AppCompatActivity {

    public static ArrayList<People> people_list;
    public static ArrayList<Group> group_list;


    TabLayout tabLayout;
    MenuItem search;
    MenuItem edit;
    MenuItem itemCheckBox;

    Toolbar toolbar;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    FloatingActionButton floatingActionButton;

    PeopleFragment peopleFragment;
    GroupsFragment groupsFragment;

    DBHelper helper;

    public String groupName;
    public String year;
    public String month;
    public String day;

    //boolean editMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        helper = new DBHelper(this);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.hide();

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pageView);
        pageAdapter = new PageAdapter(getSupportFragmentManager());

        if (group_list == null) {
            group_list = new ArrayList<>();
            updateGroups();
        }

        if (people_list == null) {
            people_list = new ArrayList<>();
            updatePeople();
        }

        PeopleAdapter.ClickListener clickItemPeople = new PeopleAdapter.ClickListener() {

            @Override
            public void onLongItemClick(People item) {
                /*
                if (editMode) {
                    helper.removePeople(item.Name);

                    people_list.remove(item);
                    peopleFragment.update();
                    peopleFragment.setCounterText(people_list.size());
                    Toast.makeText(PeopleActivity.this,
                            item.Name + " " + getResources().getString(R.string.hasRemoved),
                            Toast.LENGTH_SHORT).show();
                }
                 */
            }

            @Override
            public void onItemClick(People item) {
                //if (!editMode) {
                    if (!helper.containsDataPeople(item, year, month, day)) {
                        helper.SetDataInDataTable(item.Name, year, month, day);

                        Toast.makeText(PeopleActivity.this,
                                item.Name + " " + getResources().getString(R.string.AddData) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PeopleActivity.this,
                                item.Name + " " + getResources().getString(R.string.PeopleAlreadyHaveThisDate) + " " + day + "." + month + "." + year,
                                Toast.LENGTH_SHORT).show();
                  //  }
                }
            }

        };

        PeopleAdapter.RemoveListener removeListener = item -> {
            helper.removePeople(item.Name);
            people_list.remove(item);
            peopleFragment.update();
            peopleFragment.setCounterText(people_list.size());
            Toast.makeText(PeopleActivity.this,
                    item.Name + " " + getResources().getString(R.string.hasRemoved),
                    Toast.LENGTH_SHORT).show();
        };

        GroupsAdapter.ClickListener clickItemGroups = new GroupsAdapter.ClickListener() {

            @Override
            public void onLongItemClick(Group item, GroupsAdapter.ItemViewHolder holder) {
                /*
                if (editMode) {
                    helper.removeGroup(item.Name);
                    group_list.remove(item);
                    groupsFragment.update();
                    groupsFragment.setCounterText(group_list.size());
                */
                //}
                //else {
                    ArrayList<People> peopleInGroup = helper.getGroupMembers(item.Name);

                    for (People i : peopleInGroup) {
                        if (!helper.containsDataPeople(i, year, month, day)) {
                            helper.SetDataInDataTable(i.Name, year, month, day);
                        }
                    }

                    Toast.makeText(PeopleActivity.this,
                            getResources().getString(R.string.addGroupDate1) + " "
                                    + item.Name + " "
                                    + getResources().getString(R.string.addGroupDate2) + " "
                                    + day + "." + month + "." + year
                            ,
                            Toast.LENGTH_SHORT).show();
                //}
            }

            @Override
            public void onItemClick(Group item) {
                //if (!editMode) {
                    groupName = item.Name;
                    Intent intent = new Intent(PeopleActivity.this, GroupActivity.class);
                    intent.putExtra("edit",false);
                    intent.putExtra("name", String.valueOf(item.Name));
                    intent.putExtra("year", String.valueOf(year));
                    intent.putExtra("month", String.valueOf(month));
                    intent.putExtra("day", String.valueOf(day));
                    startActivity(intent);
                //}
            }

            @Override
            public void onMoreItemClick(Group item, MenuItem menuItem) {
                
            }


            @Override
            public void onAddItemClick() {

            }
        };

        peopleFragment = new PeopleFragment(clickItemPeople, removeListener, people_list);
        groupsFragment = new GroupsFragment(clickItemGroups, group_list);

        pageAdapter.AddFragment(peopleFragment, getResources().getString(R.string.People));
        pageAdapter.AddFragment(groupsFragment, getResources().getString(R.string.Groups));

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        toolbar = findViewById(R.id.toolbar_all_people);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add People");

        floatingActionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(PeopleActivity.this).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);

            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                EditText editText = view1.findViewById(R.id.text_edit_alertview);
                if (tabLayout.getSelectedTabPosition() == 0) {
                    addNewPeople(editText.getText().toString());
                    updatePeople();
                } else {
                    addNewGroup(editText.getText().toString());
                    updateGroups();

                }

            });
            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        });



        Intent intent = getIntent();

        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");


    }

    @Override
    protected void onResume() {
        updatePeople();
        updateGroups();
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.people_activity_menu, menu);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);

        edit.setEnabled(false);
        edit.setVisible(false);

        itemCheckBox = menu.findItem(R.id.itemCheckBox);
        itemCheckBox.setVisible(false);

        itemCheckBox.setOnMenuItemClickListener(item -> {

            return false;
        });

        edit.setOnMenuItemClickListener(menuItem -> {
            //if (editMode) {
                peopleFragment.setRemoveVisible(false);
                floatingActionButton.hide();
                //editMode = !editMode;
            /*
            } else {
                edit.setVisible(false);
                peopleFragment.setRemoveVisible(true);
                floatingActionButton.show();
                editMode = !editMode;
            }
            */
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {

            /*
            if (!editMode) {
                edit.setVisible(false);
            }

            */
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        });

        mSearchView.setOnCloseListener(() -> {
            updateGroups();
            updatePeople();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            /*
            if (!editMode) {
                edit.setVisible(true);
                edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            */
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
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        updatePeople();
                    } else {
                        updateGroups();
                    }
                } else {

                    if (tabLayout.getSelectedTabPosition() == 0) {
                        people_list.clear();
                        people_list.addAll(helper.getPeopleFilter(newText));
                        peopleFragment.update();
                    } else {
                        group_list.clear();
                        group_list.addAll(helper.getGroupsFilter(newText));
                        groupsFragment.update();
                    }

                }

                peopleFragment.setCounterText(people_list.size());
                groupsFragment.setCounterText(group_list.size());
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            //if (editMode) {

                peopleFragment.setRemoveVisible(false);
                //floatingActionButton.hide();
                //editMode = !editMode;
                //edit.setVisible(true);

            //} else {
                finish();
            //}
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

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
            peopleFragment.setCounterText(people_list.size());
        } else {
            Toast.makeText(PeopleActivity.this,
                    "Не удалось добавить пустое наименование",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewGroup(String name) {
        if (!name.equals("")) {
            if (!helper.containsGroup(new Group(name))) {
                helper.addGroup(name);
                group_list.add(new Group(name));

            } else {
                int counter = 2;

                while (helper.containsGroup(new Group(name + counter))) {
                    counter++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(PeopleActivity.this);

                builder.setCancelable(true);

                String newName = name + counter;


                builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    helper.addGroup(newName);
                    group_list.add(new Group(name));

                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            groupsFragment.setCounterText(group_list.size());
        } else {
            Toast.makeText(PeopleActivity.this,
                    getResources().getString(R.string.AlertEmptyName),
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void updatePeople() {
        people_list.clear();
        people_list.addAll(helper.getAllPeople());

        if (peopleFragment != null) {
            peopleFragment.update();
            peopleFragment.setCounterText(people_list.size());
            tabLayout.setScrollX(0);
        }

    }

    private void updateGroups() {
        group_list.clear();
        group_list.addAll(helper.getAllGroups());
        for (Group group : group_list) {
            group.Count = helper.getGroupMembers(group.Name).size();
        }
        if (groupsFragment != null) {
            groupsFragment.update();
            groupsFragment.setCounterText(group_list.size());
        }
    }

}
