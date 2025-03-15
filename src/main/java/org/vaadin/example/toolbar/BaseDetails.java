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
import java.util.function.Consumer;

public class BaseDetails extends Details implements Switchable {
    private final MiniaturesService miniaturesService;
    private final Consumer<Miniature> updateConsumer;
    private final IntegerField baseWidthField = new IntegerField("Width (mm)");
    private final IntegerField baseLengthField = new IntegerField("Length (mm)");
    private final IntegerField baseOffsetLeftField = new IntegerField("Offset left");
    private final IntegerField baseOffsetRightField = new IntegerField("Offset right");
    private final Checkbox isCircleCheckBox;
    private final Checkbox isDrawBaseCheckBox;

    public BaseDetails(MiniaturesService miniaturesService, Consumer<Miniature> updateConsumer) {
        super("Base");
        this.miniaturesService = miniaturesService;
        this.updateConsumer = updateConsumer;
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

        baseOffsetLeftField.setWidth("90px");
        baseOffsetLeftField.setMin(0);
        baseOffsetLeftField.setStepButtonsVisible(true);
        baseOffsetLeftField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        baseOffsetRightField.setWidth("90px");
        baseOffsetRightField.setMin(0);
        baseOffsetRightField.setStepButtonsVisible(true);
        baseOffsetRightField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    }

    private void configure() {
        this.isDrawBaseCheckBox.addValueChangeListener(e -> {
            isCircleCheckBox.setValue(false);
            baseLengthField.setEnabled(e.getValue());
            isCircleCheckBox.setEnabled(e.getValue());
            miniaturesService.selected().ifPresent(m -> {
                m.setDrawBase(e.getValue());
                updateConsumer.accept(m);
            });
        });

        this.isCircleCheckBox.addValueChangeListener(e -> {
            baseLengthField.setEnabled(!e.getValue());
            if (e.getValue()) {
                miniaturesService.selected().ifPresent(m -> {
                    m.setBaseLengthMm(baseWidthField.getValue());
                    updateConsumer.accept(m);
                });
            } else {
                Optional.ofNullable(baseLengthField.getValue())
                    .ifPresent(v -> miniaturesService.selected().ifPresent(m -> {
                        m.setBaseLengthMm(v);
                        updateConsumer.accept(m);
                    }));
            }
        });

        baseWidthField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseWidthMm(e.getValue()));
            if (isCircleCheckBox.isEnabled() && isCircleCheckBox.getValue()) {
                baseLengthField.setValue(baseWidthField.getValue());
            }
            miniaturesService.selected().ifPresent(updateConsumer);
        });

        baseLengthField.setEnabled(false);
        baseLengthField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseLengthMm(e.getValue()));
            miniaturesService.selected().ifPresent(updateConsumer);
        });

        baseOffsetLeftField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseOffsetLeft(e.getValue()));
            miniaturesService.selected().ifPresent(updateConsumer);
        });
        baseOffsetRightField.addValueChangeListener(e -> {
            miniaturesService.selected().ifPresent(m -> m.setBaseOffsetRight(e.getValue()));
            miniaturesService.selected().ifPresent(updateConsumer);
        });
    }

    private Component getBaseDetailsPanel() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(false);
        verticalLayout.setWidthFull();

        verticalLayout.add(
            new HorizontalLayout(isDrawBaseCheckBox, isCircleCheckBox),
            new HorizontalLayout(baseWidthField, baseLengthField),
            new HorizontalLayout(baseOffsetLeftField, baseOffsetRightField));
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
        baseOffsetLeftField.setValue(miniature.getBaseOffsetLeft());
        baseOffsetRightField.setValue(miniature.getBaseOffsetRight());
        isDrawBaseCheckBox.setValue(miniature.isDrawBase());
        isCircleCheckBox.setValue(miniature.getBaseWidthMm() == miniature.getBaseLengthMm());
    }
}
