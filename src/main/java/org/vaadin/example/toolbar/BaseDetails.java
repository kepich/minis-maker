package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

public class BaseDetails extends Details implements Switchable {
    private final NumberField baseWidthField = new NumberField("Width (mm)");
    private final NumberField baseLengthField = new NumberField("Length (mm)");
    private final Checkbox isCircleCheckBox = new Checkbox("Circle base", false,
        e -> baseLengthField.setEnabled(!e.getValue()));
    private final Checkbox isDrawBaseCheckBox = new Checkbox("Draw base", false,
        e -> {
            isCircleCheckBox.setValue(false);
            baseLengthField.setEnabled(e.getValue());
            isCircleCheckBox.setEnabled(e.getValue());
        });

    public BaseDetails() {
        super("Base");

        disable();
        applyStyles();
        setWidthFull();

        add(getBaseDetailsPanel());
    }

    private void applyStyles() {
        addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);

        baseWidthField.setWidth("90px");
        baseWidthField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        baseLengthField.setWidth("90px");
        baseLengthField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    }

    private Component getBaseDetailsPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();
        baseLengthField.setEnabled(false);
        isCircleCheckBox.setEnabled(false);
        baseWidthField.addValueChangeListener(e -> {
            if (isCircleCheckBox.isEnabled() && isCircleCheckBox.getValue()) {
                baseLengthField.setValue(baseWidthField.getValue());
            }
        });

        verticalLayout.add(
            new HorizontalLayout(isDrawBaseCheckBox, isCircleCheckBox),
            new HorizontalLayout(baseWidthField, baseLengthField));
        return verticalLayout;
    }

    @Override
    public void enable() {
        setEnabled(true);
    }

    @Override
    public void disable() {
        setEnabled(false);
        setOpened(false);
    }
}
