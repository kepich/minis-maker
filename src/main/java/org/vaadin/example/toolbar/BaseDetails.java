package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.vaadin.example.service.MiniaturesService;

public class BaseDetails extends Details implements Switchable {
    private final IntegerField baseWidthField = new IntegerField("Width (mm)");
    private final IntegerField baseLengthField = new IntegerField("Length (mm)");
    private final Checkbox isCircleCheckBox = new Checkbox("Circle base", false,
        e -> baseLengthField.setEnabled(!e.getValue()));
    private final Checkbox isDrawBaseCheckBox = new Checkbox("Draw base", false,
        e -> {
            isCircleCheckBox.setValue(false);
            baseLengthField.setEnabled(e.getValue());
            isCircleCheckBox.setEnabled(e.getValue());
        });
    private final MiniaturesService miniaturesService;

    public BaseDetails(MiniaturesService miniaturesService) {
        super("Base");
        this.miniaturesService = miniaturesService;

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
            miniaturesService.selected().ifPresent(m -> m.setBaseWidthMm(e.getValue()));
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

    public void setBaseWidth(int baseWidth) {
        baseWidthField.setValue(baseWidth);
    }
}
