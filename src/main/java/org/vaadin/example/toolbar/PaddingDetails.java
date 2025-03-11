package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.vaadin.example.Miniature;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PaddingDetails extends Details implements Switchable {
    private final IntegerField paddingTopField = new IntegerField("Top");
    private final IntegerField paddingBottomField = new IntegerField("Bottom");
    private final IntegerField paddingRightField = new IntegerField("Right");
    private final IntegerField paddingLeftField = new IntegerField("Left");
    private final Supplier<Optional<Miniature>> miniatureSupplier;
    private final Consumer<Miniature> updateConsumer;

    public PaddingDetails(Supplier<Optional<Miniature>> miniatureSupplier, Consumer<Miniature> updateConsumer) {
        super("Image paddings");
        this.miniatureSupplier = miniatureSupplier;
        this.updateConsumer = updateConsumer;

        disable();
        applyStyles();
        setWidthFull();

        add(getPaddingImagePanel());
    }

    private void applyStyles() {
        addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);

        paddingTopField.setWidth("120px");
        paddingTopField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingBottomField.setWidth("120px");
        paddingBottomField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingRightField.setWidth("120px");
        paddingRightField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingLeftField.setWidth("120px");
        paddingLeftField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
    }

    private Component getPaddingImagePanel() {
        paddingTopField.setStep(1);
        paddingTopField.setValue(0);
        paddingTopField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingTopField.setStepButtonsVisible(true);
        paddingTopField.addValueChangeListener(e -> {
            miniatureSupplier.get().ifPresent(miniature -> {
                miniature.setPaddingTop(paddingTopField.getValue());
                updateConsumer.accept(miniature);
            });
        });

        paddingBottomField.setStep(1);
        paddingBottomField.setValue(0);
        paddingBottomField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingBottomField.setStepButtonsVisible(true);
        paddingBottomField.addValueChangeListener(e -> {
            miniatureSupplier.get().ifPresent(miniature -> {
                miniature.setPaddingBottom(paddingBottomField.getValue());
                updateConsumer.accept(miniature);
            });
        });

        paddingRightField.setStep(1);
        paddingRightField.setValue(0);
        paddingRightField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingRightField.setStepButtonsVisible(true);
        paddingRightField.addValueChangeListener(e -> {
            miniatureSupplier.get().ifPresent(miniature -> {
                miniature.setPaddingRight(paddingRightField.getValue());
                updateConsumer.accept(miniature);
            });
        });

        paddingLeftField.setStep(1);
        paddingLeftField.setValue(0);
        paddingLeftField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        paddingLeftField.setStepButtonsVisible(true);
        paddingLeftField.addValueChangeListener(e -> {
            miniatureSupplier.get().ifPresent(miniature -> {
                miniature.setPaddingLeft(paddingLeftField.getValue());
                updateConsumer.accept(miniature);
            });
        });

        VerticalLayout verticalLayout = new VerticalLayout(paddingTopField, new HorizontalLayout(paddingLeftField, paddingRightField), paddingBottomField);
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setSpacing(false);
        return verticalLayout;
    }

    public void setPaddings(int top, int bottom, int left, int right) {
        paddingTopField.setValue(top);
        paddingBottomField.setValue(bottom);
        paddingLeftField.setValue(left);
        paddingRightField.setValue(right);
    }

    @Override
    public void enable() {
        setEnabled(true);
    }

    @Override
    public void disable() {
        setEnabled(false);
        setPaddings(0, 0, 0, 0);
        setOpened(false);
    }
}
