package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.vaadin.example.Miniature;
import org.vaadin.example.service.MiniaturesService;

import java.util.Optional;

public class BaseDetails extends Details implements Switchable {
    private final MiniaturesService miniaturesService;
    private final IntegerField baseWidthField = new IntegerField("Width (mm)");
    private final IntegerField baseLengthField = new IntegerField("Length (mm)");
    private final Checkbox isCircleCheckBox;
    private final Checkbox isDrawBaseCheckBox;

    public BaseDetails(MiniaturesService miniaturesService) {
        super("Base");
        this.miniaturesService = miniaturesService;
        this.isCircleCheckBox = new Checkbox("Circle base", false);
        this.isDrawBaseCheckBox = new Checkbox("Draw base", false);

        disable();
        applyStyles();
        configure();
        setWidthFull();

        add(getBaseDetailsPanel());
    }

    private void applyStyles() {
        addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);

        baseWidthField.setWidth("90px");
        baseWidthField.setMin(20);
        baseWidthField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        baseLengthField.setWidth("90px");
        baseLengthField.setMin(20);
        baseLengthField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    }

    private void configure() {
        this.isDrawBaseCheckBox.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setDrawBase(e.getValue()));
            isCircleCheckBox.setValue(false);
            baseLengthField.setEnabled(e.getValue());
            isCircleCheckBox.setEnabled(e.getValue());
        });

        this.isCircleCheckBox.addValueChangeListener(e -> {
            baseLengthField.setEnabled(!e.getValue());
            if (e.getValue()) {
                miniaturesService.selected().ifPresent(m -> m.setBaseLengthMm(baseWidthField.getValue()));
            } else {
                Optional.ofNullable(baseLengthField.getValue())
                    .ifPresent(v -> miniaturesService.selected().ifPresent(m -> m.setBaseLengthMm(v)));
            }
        });

        baseWidthField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseWidthMm(e.getValue()));
            if (isCircleCheckBox.isEnabled() && isCircleCheckBox.getValue()) {
                baseLengthField.setValue(baseWidthField.getValue());
            }
        });

        baseLengthField.setEnabled(false);
        baseLengthField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseLengthMm(e.getValue()));
        });
    }

    private Component getBaseDetailsPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();

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

    public void init(Miniature miniature) {
        enable();
        setOpened(true);
        baseWidthField.setValue(miniature.getBaseWidthMm());
        baseLengthField.setValue(miniature.getBaseLengthMm());
        isDrawBaseCheckBox.setValue(miniature.isDrawBase());
        isCircleCheckBox.setValue(miniature.getBaseWidthMm() == miniature.getBaseLengthMm());
    }
}
