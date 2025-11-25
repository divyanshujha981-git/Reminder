package com.reminder.main.UserInterfaces.SendTaskPage;


import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.SqLite.Tasks.TaskConstants;
import com.reminder.main.SqLite.Tasks.TaskData;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements
        CustomInterfaces.BindView,
        CustomInterfaces.ContextualActionBarCallback{

    private final ArrayList<TaskData> taskList;
    private final CustomInterfaces.BindView bindView = this;
    private Context context;
    private Resources resources;
    private Typeface typeface;
    private final DecimalFormat minuteFormat = new DecimalFormat("00");
    private final Map<String, TaskData> itemsSelected = new HashMap<>();
    private final ArrayList<Integer> itemsSelectedIndex = new ArrayList<>();

    private final int
            timeStringFormat = R.string.task_card_time_format,
            repeatStringFormat = R.string.task_card_repeat_status_format,
            setTopicStringFormat = R.string.set_topic_task_card_format;

    private String[] repeatType, amPm;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final CustomInterfaces.ContextualActionBar contextualActionBar;


    public TasksAdapter(
            ArrayList<TaskData> list,
            Context mainActivityContext
    ) {
        this.taskList = list;
        contextualActionBar = (CustomInterfaces.ContextualActionBar) mainActivityContext;
        contextualActionBar.setContextualActionBarVisible(this, null);
        setHasStableIds(true);
    }







    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        this.resources = context.getResources();
        typeface = context.getResources().getFont(R.font.sans_serif_medium);
        repeatType = resources.getStringArray(R.array.repeatType);
        amPm = resources.getStringArray(R.array.amPm);


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_card, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            bindView.primaryViewBinding(
                    (LinearLayout) holder.itemView,
                    taskList.get(position),
                    holder.getAdapterPosition()
            );
        } catch (Exception ignored) {
        }

    }


    @Override
    public void primaryViewBinding(LinearLayout view, TaskData itemView, int position) {
        MaterialCardView cardView = (MaterialCardView) view.getChildAt(1);
        ((TextView) cardView.getChildAt(2)).setText(resources.getString(setTopicStringFormat, itemView.getTopic()));

        handler.post(() -> bindView.secondaryViewBinding(cardView, itemView, position));


        ((TextView) view.getChildAt(0))
                .setText(
                        resources.getString(
                                timeStringFormat,
                                itemView.getHour(),
                                minuteFormat.format(itemView.getMinute()),
                                amPm[itemView.getAmPm()]
                        )
                );


        ((TextView) cardView.getChildAt(3)).setText(
                Html.fromHtml(
                        resources.getString(
                                repeatStringFormat,
                                repeatType[itemView.getRepeatStatus() - 1]),
                        FROM_HTML_MODE_LEGACY)

        );


    }


    private byte i = 0;
    @Override
    public void secondaryViewBinding(MaterialCardView view, TaskData itemView, int position) {

        if (itemView.getRepeatStatus() != TaskConstants.REPEAT_STATUS_NO_REPEAT) {
            LinearLayout daysInWeek = (LinearLayout) view.getChildAt(4);

            try {
                // FOR E.G:- [1, 2, 4, 5, 7]
                for (; i < itemView.getDateArray().length(); i++) {
                    TextView days = (TextView) daysInWeek.getChildAt((int) itemView.getDateArray().get(i) - 1);
                    days.setTypeface(typeface);
                    days.setEnabled(true);
                }
                i = 0;
            } catch (JSONException ignored) {
            }
        }

        if (itemView.getPinned() == TaskConstants.PINNED_YES)
            view.getChildAt(0).setVisibility(VISIBLE);

        view.setOnLongClickListener(v -> {
            startSelection(itemView, view, position);
            //startSelection(itemView.getTaskId(), view, position);
            return true;
        });

        view.setOnClickListener(v -> {
            startSelection(itemView, view, position);
            //startSelection(itemView.getTaskId(), view, position);
        });

        setTaskCardSelected(itemView.getSelected(), view);


        if (itemView.getAlreadyDone() == TaskConstants.ALREADY_DONE_YES_BYTE) {
            view.getChildAt(1).setVisibility(VISIBLE);
        }

    }






    private void startSelection(TaskData taskData, MaterialCardView view, int index) {

        if (itemsSelected.containsKey(taskData.getTaskId())) {
            itemsSelected.remove(taskData.getTaskId());
            itemsSelectedIndex.remove((Object) index);
            setTaskCardSelected(false, view);

        }
        else {
            itemsSelected.put(taskData.getTaskId(), taskData);
            itemsSelectedIndex.add(index);
            setTaskCardSelected(true, view);
        }

        contextualActionBar.changeTitle(String.valueOf(itemsSelected.size()));


//        if (!longClickSelected) {
//            longClickSelected = true;
//            contextualActionBar.setContextualActionBarVisible(this, null);
//        }

        Log.d("TAG", "startSelection: " + itemsSelected.toString());

        //if (itemsSelectedIDs.isEmpty()) {
        //    longClickSelected = false;
        //    contextualActionBar.setContextualActionBarInVisible();
        //}
        //else {
        //    // change topic
        //    contextualActionBar.changeTitle(itemsSelectedIDs.size()+"");
        //}



    }




    private void setTaskCardSelected(boolean condition, MaterialCardView taskCard) {
         if (condition) {
             taskCard.getChildAt(5).setVisibility(VISIBLE);
             ((LinearLayout) taskCard.getParent()).setBackground(ContextCompat.getDrawable(context, R.drawable.selected_background_card));
         }
         else {
             taskCard.getChildAt(5).setVisibility(GONE);
             ((LinearLayout) taskCard.getParent()).setBackground(null);
         }

    }



    public Map<String, TaskData> getSelectedTask() {
        return itemsSelected;
    }








    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void selectAll() {

        for (int i = 0; i < taskList.size(); i++) {
            itemsSelected.put(taskList.get(i).getTaskId(), taskList.get(i));
        }

        for (int i = 0; i < taskList.size(); i++) {
            itemsSelectedIndex.add(i);
        }

        //itemsSelected = taskList.stream().map(TaskData::getTaskId).collect(Collectors.toCollection(ArrayList::new));
        //itemsSelectedIndex = IntStream.range(0, taskList.size()).boxed().collect(Collectors.toCollection(ArrayList::new));

        for (TaskData taskData : taskList) {
            taskData.setSelected(true);
        }

        notifyDataSetChanged();
        contextualActionBar.changeTitle(itemsSelected.size()+"");

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void unSelectAll(boolean actionBarDestroy) {
        itemsSelected.clear();
        itemsSelectedIndex.clear();

        for (TaskData taskData : taskList) {
            taskData.setSelected(false);
        }

        contextualActionBar.changeTitle("0");
        notifyDataSetChanged();

        //if (actionBarDestroy) {
        //    longClickSelected = false;
        //}

    }







    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }


}



