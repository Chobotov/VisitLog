package com.android.visitlog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class GroupsActivityFragment extends Fragment {

    View v;

    RecyclerView recyclerView;
    GroupsAdapter adapter;
    ArrayList<Group> groups;
    GroupsAdapter.ClickListener clickListener;
    TextView countGroup;

    MenuItem search;
    MenuItem edit;
    MenuItem itemCheckBox;


    Toolbar toolbar;

    DBHelper helper;

    // day, month, year

    public GroupsActivityFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_groups_activity, container, false);


        recyclerView = v.findViewById(R.id.groups_recyclerView);
        countGroup = v.findViewById(R.id.countGroup);

        toolbar = v.findViewById(R.id.toolbar_main_group);

        helper = new DBHelper(v.getContext());



        if (groups == null) {
            groups = new ArrayList<>();
            updateGroups();
        }

        clickListener = new GroupsAdapter.ClickListener() {
            @Override
            public void onLongItemClick(Group item) {

//                if (editMode) {
//                    helper.removeGroup(item.Name);
//                    groups.remove(item);
//                    update();
//                    setCounterText(groups.size());
//
//                } else {
//
//                    ArrayList<People> peopleInGroup = helper.getGroupMembers(item.Name);
//
//                    for (People i : peopleInGroup) {
//                        if (!helper.containsDataPeople(i, data[0], data[1], data[2])) {
//                            helper.SetDataInDataTable(i.Name,  data[0], data[1], data[2]);
//                        }
//                    }
////
////                    Toast.makeText(v.getContext(),
////                            getResources().getString(R.string.addGroupDate1) + " "
////                                    + item.Name + " "
////                                    + getResources().getString(R.string.addGroupDate2) + " "
////                                    + data[0] + "." + data[1] + "." + data[2]
////                            ,
////                            Toast.LENGTH_SHORT).show();
//
//                }

            }

            @Override
            public void onItemClick(Group item) {
                //if (!editMode) {
                    Intent intent = new Intent(v.getContext(), GroupActivity.class);
                    intent.putExtra("edit",true);

                    intent.putExtra("name", String.valueOf(item.Name));
//                    intent.putExtra("year", String.valueOf(data[0]));
//                    intent.putExtra("month", String.valueOf(data[1]));
//                    intent.putExtra("day", String.valueOf(data[2]));
                    startActivity(intent);
               // }
            }

            @Override
            public void onMoreItemClick(Group item, MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.removeItem){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setCancelable(true);
                    builder.setMessage("Удалить группу " +'"' + item.Name + '"' + " ?");
                    builder.setPositiveButton("Да", (dialogInterface, i) -> {
                        helper.removeGroup(item.Name);
                        groups.remove(item);
                        update();
                    });
                    builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


//                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Группа "+item.Name+" удалена", Snackbar.LENGTH_LONG)
//                            .setAction("Отмена", (View.OnClickListener) v -> {
//                            }).show();



                }
                else if(menuItem.getItemId() == R.id.renameItem){
                    Toast.makeText(getContext(),"Ещё не готово",Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onAddItemClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setCancelable(true);

                View view1 = LayoutInflater.from(v.getContext()).inflate(R.layout.add_people_alert, null);

                builder.setView(view1);

                builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                    EditText editText = view1.findViewById(R.id.text_edit_alertview);
                    addNewGroup(editText.getText().toString());
                    updateGroups();

                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        };

        if (clickListener != null)
            adapter = new GroupsAdapter(getContext(), clickListener, groups);
        else
            adapter = new GroupsAdapter(getContext(), null, groups);

        adapter.EmptyItemEnable = true;
        adapter.MoreButtonEnable = true;

        recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(false);

        v.findViewById(R.id.temp).requestFocus();
        setCounterText(groups.size());

        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        return v;
    }

    //затычка
    private String[] GetData() {
        return new String[]{"00","00","0000"};
    }


    public void update(){
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
            setCounterText(groups.size());
        }
    }


    public void setCounterText(int text){
        if(countGroup != null)
            countGroup.setText(text + " "+ getResources().getString(R.string.Groups));
    }

    private void updateGroups() {
        groups.clear();
        groups.addAll(helper.getAllGroups());

        for (Group group : groups) {
            group.Count = helper.getGroupMembers(group.Name).size();
        }
        if (groups != null) {
            update();
            setCounterText(groups.size());
        }
    }




    public void onResume() {
        updateGroups();
        super.onResume();


    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.people_activity_menu, menu);
        super.onCreateOptionsMenu(menu,menuInflater);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemCheckBox = menu.findItem(R.id.itemCheckBox);
        itemCheckBox.setVisible(false);
        edit.setVisible(false);
        itemCheckBox.setOnMenuItemClickListener(item -> {
            return false;
        });

        edit.setOnMenuItemClickListener(menuItem -> {
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnSearchClickListener(view -> {
        });

        mSearchView.setOnCloseListener(() -> {
            updateGroups();
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
                    updateGroups();
                } else {
                    groups.clear();
                    groups.addAll(helper.getGroupsFilter(newText));
                    update();
                }

                setCounterText(groups.size());
                return false;
            }
        });
    }


    private void setRemoveVisible(boolean b) {
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        ((AppCompatActivity)(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(b);
    }



    private void addNewGroup(String name) {

        if (!name.equals("")) {

            if (!helper.containsGroup(new Group(name))) {

                helper.addGroup(name);
                groups.add(new Group(name));

            } else {

                int counter = 2;

                while (helper.containsGroup(new Group(name + counter))) {
                    counter++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setCancelable(true);

                String newName = name + counter;

                builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    helper.addGroup(newName);
                    groups.add(new Group(name));

                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            Toast.makeText(v.getContext(),
                    getResources().getString(R.string.AlertEmptyName),
                    Toast.LENGTH_SHORT).show();
        }


    }

}
