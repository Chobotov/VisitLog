package com.android.visitlog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.visitlog.DialogFileMenu.DirPickerActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class GroupsActivityFragment extends Fragment {

    private static final int RESULT_OUT_CODE = 1;
    private static final int RESULT_IN_CODE = 2;

    View v;

    RecyclerView recyclerView;
    GroupsAdapter adapter;
    ArrayList<Group> groups;
    GroupsAdapter.ClickListener clickListener;
    TextView countGroup;

    MenuItem search;
    MenuItem itemCheckBox;
    MenuItem save;
    MenuItem open;


    Toolbar toolbar;

    DBHelper helper;

    String day, month, year;

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
            public void onLongItemClick(Group item, GroupsAdapter.ItemViewHolder holder) {


                holder.showPopup(holder.itemView,
                        x->{
                            onMoreItemClick(item,x);
                            return false;
                        }
                );
            }

            @Override
            public void onItemClick(Group item) {
                //if (!editMode) {
                    Intent intent = new Intent(v.getContext(), GroupActivity.class);
                    intent.putExtra("edit",true);

                    intent.putExtra("name", String.valueOf(item.Name));
                    intent.putExtra("year", String.valueOf(year));
                    intent.putExtra("month", String.valueOf(month));
                    intent.putExtra("day", String.valueOf(day));
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setCancelable(true);

                    View view1 = LayoutInflater.from(v.getContext()).inflate(R.layout.add_people_alert, null);

                    builder.setView(view1);

                    builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                        EditText editText = view1.findViewById(R.id.text_edit_alertview);
                        RenameGroup(editText.getText().toString(),item.Name);
                        updateGroups();

                    });
                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();
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
        adapter.MoreButtonEnable = false;

        recyclerView.setLayoutManager(new GridLayoutManager(v.getContext(),2));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusable(false);
        v.findViewById(R.id.temp).requestFocus();

        setCounterText(groups.size());




        return v;
    }

    private void RenameGroup(String name , String groupName) {

        final  String GroupName = groupName;
        if (!name.equals("")) {

            if (!helper.containsGroup(new Group(name))) {

                helper.RenameGroup(GroupName,name);

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
                    helper.RenameGroup(GroupName, newName);


                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        } else {
            Snackbar.make(v,getResources().getString(R.string.AlertEmptyGroupName),Snackbar.LENGTH_LONG).show();

        }


    }




    public void setCounterText(int text){
            countGroup.setText(text+"");
    }

    public void updateGroups() {
        groups.clear();
        groups.addAll(helper.getAllGroups());

        for (Group group : groups) {
            group.Count = helper.getGroupMembers(group.Name).size();
        }

        if (groups != null) {
            update();
        }

        setCounterText(groups.size());

    }


    public void update(){
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
            setCounterText(groups.size());
        }
    }


    public void onResume() {
        updateGroups();
        super.onResume();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.groups_activity_menu, menu);
        super.onCreateOptionsMenu(menu,menuInflater);

        menu.findItem(R.id.removeItem).setVisible(false);
        menu.findItem(R.id.renameItem).setVisible(false);
        menu.findItem(R.id.editMod).setVisible(false);

        open = menu.findItem(R.id.open_data);
        save = menu.findItem(R.id.save_data);
        search = menu.findItem(R.id.app_bar_search);


        itemCheckBox = menu.findItem(R.id.itemCheckBox);
        itemCheckBox.setVisible(false);

        save.setOnMenuItemClickListener( x->{
            //openFile();
//            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//            intent.setType("file/*");
//            startActivityForResult(intent, REQUEST_GET_TEXTURE);
            Intent intent = new Intent(v.getContext(), DirPickerActivity.class);
            intent.putExtra(DirPickerActivity.KEY_MODE, DirPickerActivity.Mode.DIRECTORY);
            startActivityForResult(intent, RESULT_OUT_CODE);
            return false;
        });

        open.setOnMenuItemClickListener( x->{

            Intent intent = new Intent(v.getContext(), DirPickerActivity.class);
            intent.putExtra(DirPickerActivity.KEY_MODE, DirPickerActivity.Mode.FILE);
            startActivityForResult(intent, RESULT_IN_CODE);
            return false;
        });

        SearchView mSearchView = (SearchView) search.getActionView();

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
                    for (Group group : groups) {
                        group.Count = helper.getGroupMembers(group.Name).size();
                    }
                    update();
                }

                setCounterText(groups.size());
                return false;
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == RESULT_OUT_CODE && resultCode == RESULT_OK) {
            Uri data = i.getData();
            if (data != null) {

                String path = data.getPath();

                if (path != null) {
//                    String fileDataString = readFromFile(helper.getPath());
//                    saveToFile(data.getPath(),fileDataString);//helper.getPath()


                    try {

                        copyFile(helper.getPath(), path+"/"+ helper.DATABASE_NAME+ helper.DATABASE_VERSION);
                        Snackbar.make(v,getResources().getString(R.string.DataSaveAlert),Snackbar.LENGTH_LONG).show();

                    }
                    catch (Exception e){
                        Log.e("Keliz",e.getMessage());
                    }
                 //newFile
                    Log.e("Keliz",data.getPath());
                }
            }
        }
        else if(requestCode == RESULT_IN_CODE &&  resultCode == RESULT_OK) {
            Uri data = i.getData();
            if (data != null) {

                String path = data.getPath();

                if (path != null) {

                    try {
                        //helper.getPath(
                       // path+"/VisitLog"+helper.DATABASE_VERSION
                        copyFile(path, helper.getPath());
                        Snackbar.make(v,getResources().getString(R.string.DataOpenAlert),Snackbar.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Log.e("Keliz",e.getMessage());
                    }


                    //newFile
                    Log.e("Keliz",data.getPath());
                }
            }
        }
    }


    public static void copyFile(String srcName, String targetName)
            throws IOException {
        if (srcName.equals(targetName))
            return;
        if (new File(targetName).isDirectory()) {
            if (!targetName.endsWith("/"))
                targetName = targetName.concat("/");
            targetName = targetName.concat(new File(srcName).getName());
        }
        Log.d("Keliz", "Copying file: " + srcName + "\n  to: " + targetName);
        FileChannel src = new FileInputStream(srcName).getChannel();
        FileChannel out = new FileOutputStream(targetName).getChannel();
        src.transferTo(0, src.size(), out);
        src.close();
        out.close();
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
                    updateGroups();
                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        } else {
            Snackbar.make(v,getResources().getString(R.string.AlertEmptyGroupName),Snackbar.LENGTH_LONG).show();
        }
        updateGroups();
        update();
    }

    public void GetData(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
