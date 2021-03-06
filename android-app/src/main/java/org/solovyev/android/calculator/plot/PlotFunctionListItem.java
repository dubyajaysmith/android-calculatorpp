package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.view.ViewBuilder;
import org.solovyev.android.view.ViewFromLayoutBuilder;

public class PlotFunctionListItem implements ListItem {

    private static final String PREFIX = "plot_function_";

    @NotNull
	private PlotFunction plotFunction;

	@NotNull
	private ViewBuilder<View> viewBuilder;

    @NotNull
    private String tag;

    public PlotFunctionListItem(@NotNull PlotFunction plotFunction) {
		this.plotFunction = plotFunction;
		this.viewBuilder = ViewFromLayoutBuilder.newInstance(R.layout.cpp_plot_function_list_item);
        this.tag = PREFIX + plotFunction.getXyFunction().getExpressionString();
    }

	@Nullable
	@Override
	public OnClickAction getOnClickAction() {
		return null;
	}

	@Nullable
	@Override
	public OnClickAction getOnLongClickAction() {
		return null;
	}

	@NotNull
	@Override
	public View updateView(@NotNull Context context, @NotNull View view) {
        final Object viewTag = view.getTag();
        if ( viewTag instanceof String ) {
            if ( this.tag.equals(viewTag) ) {
                return view;
            } else if (((String) viewTag).startsWith(PREFIX)) {
                fillView(view, context);
                return view;
            } else {
                return build(context);
            }
        }

		return build(context);
	}

	@NotNull
	@Override
	public View build(@NotNull Context context) {
		final View root = buildView(context);
		fillView(root, context);
		return root;
	}

    private View buildView(@NotNull Context context) {
        return viewBuilder.build(context);
    }

    private void fillView(@NotNull View root, @NotNull final Context context) {
        root.setTag(tag);

        final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

        final TextView expressionTextView = (TextView) root.findViewById(R.id.cpp_plot_function_expression_textview);
        expressionTextView.setText(plotFunction.getXyFunction().getExpressionString());

        final CheckBox pinnedCheckBox = (CheckBox) root.findViewById(R.id.cpp_plot_function_pinned_checkbox);
        pinnedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean pin) {
                if (pin) {
                    if (!plotFunction.isPinned()) {
                        plotFunction = plotter.pin(plotFunction);
                    }
                } else {
                    if (plotFunction.isPinned()) {
                        plotFunction = plotter.unpin(plotFunction);
                    }
                }
            }
        });
        pinnedCheckBox.setChecked(plotFunction.isPinned());

        final CheckBox visibleCheckBox = (CheckBox) root.findViewById(R.id.cpp_plot_function_visible_checkbox);
        visibleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean show) {
                if (show) {
                    if (!plotFunction.isVisible()) {
                        plotFunction = plotter.show(plotFunction);
                    }
                } else {
                    if (plotFunction.isVisible()) {
                        plotFunction = plotter.hide(plotFunction);
                    }
                }
            }
        });
        visibleCheckBox.setChecked(plotFunction.isVisible());

		final ImageButton settingsButton = (ImageButton) root.findViewById(R.id.cpp_plot_function_settings_button);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CalculatorPlotFunctionSettingsActivity.startActivity(context, plotFunction);
			}
		});
    }
}
