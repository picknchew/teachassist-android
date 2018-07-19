package me.picknchew.teachassist.courses.course;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import me.picknchew.teachassist.R;
import me.picknchew.teachassist.databinding.CardAssignmentBinding;
import me.picknchew.teachassistapi.model.Assignment;
import me.picknchew.teachassistapi.model.Category;
import me.picknchew.teachassistapi.model.Course;
import me.picknchew.teachassistapi.model.Mark;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
    private static final int UNSELECTED = -1;
    private int selectedItem = UNSELECTED;

    private RecyclerView recyclerView;

    private final List<Assignment> assignments;
    private final Map<Category, Integer> totalWeights = new HashMap<>(Category.values().length);

    AssignmentAdapter(List<Assignment> assignments) {
        this.assignments = assignments;

        updateWeights();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardAssignmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.card_assignment, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    public void updateWeights() {
        for (Category category : Category.values()) {
            int totalWeight = 0;

            for (Assignment assignment : assignments) {
                Map<Category, Mark> marks = assignment.getMarks();

                if (marks.containsKey(category)) {
                    Mark mark = marks.get(category);

                    if (mark.getMark() == null) {
                        continue;
                    }

                    totalWeight += Integer.parseInt(mark.getWeight());
                }
            }

            totalWeights.put(category, totalWeight);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Assignment assignment = assignments.get(i);
        View view = viewHolder.itemView;

        viewHolder.bind(assignment);
        view.setTag(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private final ExpandableLayout expandableLayout;
        private final TextView expandButton;
        private final CardAssignmentBinding binding;

        private ViewHolder(CardAssignmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            expandableLayout = itemView.findViewById(R.id.assignment);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            expandButton = itemView.findViewById(R.id.assignment_name);

            expandButton.setOnClickListener(this);
        }

        private void bind(Assignment assignment) {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            Context context = itemView.getContext();
            ViewGroup marksView = (ViewGroup) expandableLayout.findViewById(R.id.marks);
            Map<Category, Mark> marks = assignment.getMarks();

            List<View> rows = new ArrayList<>();

            for (Category category : Category.values()) {
                if (!marks.containsKey(category)) {
                    continue;
                }

                View row = inflater.inflate(R.layout.row_mark, marksView, false);

                TextView categoryTextView = (TextView) row.findViewById(R.id.category_name);
                TextView markTextView = (TextView) row.findViewById(R.id.mark);

                Mark mark = marks.get(category);

                int stringId = 0;

                switch (category) {

                    case KNOWLEDGE:
                        stringId = R.string.knowledge_understanding;
                        break;
                    case THINKING:
                        stringId = R.string.thinking;
                        break;
                    case COMMUNICATION:
                        stringId = R.string.communication;
                        break;
                    case APPLICATION:
                        stringId = R.string.application;
                        break;
                    case OTHER:
                        stringId = R.string.other;
                        break;
                    default:
                        break;
                }

                if (mark.getMark() != null) {
                    double categoryPercentage = Double.parseDouble(mark.getWeight()) / (double) totalWeights.get(category) * 100.0D;
                    long markPercentage = Math.round(Double.parseDouble(mark.getMark()) / Double.parseDouble(mark.getOutOf()) * 100.0D);

                    categoryTextView.setText(context.getString(stringId, categoryPercentage < 1 ? "<1" : Math.round(categoryPercentage)));
                    markTextView.setText(context.getString(R.string.mark, mark.getMark(), mark.getOutOf(), Long.toString(markPercentage)));
                } else {
                    categoryTextView.setText(context.getString(stringId, "-"));
                    markTextView.setText(context.getString(R.string.mark, "-", mark.getOutOf(), "-"));
                }

                rows.add(row);
            }

            // remove previous marks.
            marksView.removeAllViews();

            for (View row : rows) {
                marksView.addView(row);
            }

            binding.setAssignment(assignment);
            binding.executePendingBindings();

            expandButton.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);

            if (holder != null) {
                holder.expandButton.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();

            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandButton.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }
}
